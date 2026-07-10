package com.springroadmap.files.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springroadmap.files.service.FileStorageService;

/**
 * Controlador REST para subir y descargar archivos.
 *
 * <p>Analogía: es el mostrador de recepción del almacén. El cliente
 * (frontend, Postman, curl) entrega o retira paquetes por HTTP y el
 * mostrador delega el guardado real al {@link FileStorageService}.</p>
 *
 * <p>Endpoints:
 * <ul>
 *   <li>{@code POST /api/files} — sube un archivo, devuelve {@code {"filename": "..."} }.</li>
 *   <li>{@code GET  /api/files/{name}} — descarga el archivo por su nombre.</li>
 * </ul></p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES (Servlet API pura):
 * protected void doPost(HttpServletRequest req, HttpServletResponse res) {
 *     Part part = req.getPart("file");          // API de Servlets 3.0
 *     InputStream in = part.getInputStream();   // el dev maneja streams a mano
 *     Files.copy(in, Paths.get("/tmp/" + part.getSubmittedFileName()));
 *     res.getWriter().write("{\"filename\":\"...\"}"); // JSON escrito a mano
 * }
 *
 * // AHORA (Spring MVC):
 * &#64;PostMapping("/api/files")
 * public Map&lt;String, String&gt; upload(&#64;RequestParam MultipartFile file) {
 *     return Map.of("filename", storage.store(file)); // Spring parsea el multipart
 *                                                     // y Jackson serializa el JSON.
 * }
 * </pre>
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    // Constructor injection: Spring pasa el FileStorageService en el constructor.
    // Es preferible a @Autowired de campo porque permite 'final' y facilita tests.
    private final FileStorageService storage;

    public FileController(FileStorageService storage) {
        this.storage = storage;
    }

    /**
     * Recibe un archivo multipart y lo guarda vía {@link FileStorageService}.
     *
     * @param file parámetro {@code file} del formulario multipart.
     * @return JSON {@code {"filename": "<uuid.ext>"}} con el nombre asignado.
     */
    // PREGUNTA DE ALUMNO — "¿por qué @RequestParam y no @RequestBody?"
    //   Porque multipart/form-data envía CADA parte por separado (no un JSON único).
    //   @RequestParam("file") le dice a Spring: "el archivo viene en la parte llamada 'file'".
    @PostMapping
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String stored = storage.store(file);

        // HashMap simple para el JSON. Podríamos usar un record, pero mantener
        // el estilo Java 8 acerca el ejemplo al perfil del alumno objetivo.
        Map<String, String> body = new HashMap<>();
        body.put("filename", stored);
        return ResponseEntity.ok(body);
    }

    /**
     * Descarga un archivo previamente subido. Devuelve el {@link Resource}
     * como cuerpo de la respuesta con {@code Content-Disposition: attachment}
     * para que el navegador lo baje como archivo (en vez de mostrarlo inline).
     *
     * @param name nombre único devuelto por el POST.
     */
    @GetMapping("/{name:.+}")
    public ResponseEntity<Resource> download(@PathVariable("name") String name) {
        Resource resource = storage.load(name);
        return ResponseEntity.ok()
                // application/octet-stream = bytes crudos; el cliente decide qué hacer.
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                // 'attachment' fuerza la descarga; 'inline' mostraría el archivo en el navegador.
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
