package com.springroadmap.aws.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springroadmap.aws.service.S3Service;

/**
 * Controller REST para operaciones S3 (upload / download).
 *
 * <p><b>Analogía del mundo real:</b> es la ventanilla de recepción de la bodega S3.
 * El cliente se acerca con su caja (POST) o su ticket (GET), y la ventanilla llama al
 * empleado ({@link S3Service}) para que haga el trabajo real.
 *
 * <p><b>Endpoints:</b>
 * <ul>
 *   <li><code>POST /api/s3/{key}</code> — sube bytes al bucket con la clave dada.</li>
 *   <li><code>GET  /api/s3/{key}</code> — descarga los bytes del objeto con esa clave.</li>
 * </ul>
 */
// `@RestController` = @Controller + @ResponseBody. Cada método devuelve el body directamente
// (JSON, bytes, texto) — NO nombres de vista.
@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Sube un objeto a S3.
     * <p>PREGUNTA DE ALUMNO — "¿por qué recibo el body como byte[] y no como MultipartFile?"
     * Para simplificar el ejemplo. Un endpoint real de subida usaría `@RequestParam MultipartFile file`
     * y luego `file.getBytes()`. Aquí aceptamos raw bytes en el body (content-type: application/octet-stream).
     *
     * @param key     path variable, viene de la URL.
     * @param content body de la request (bytes crudos).
     * @return 200 OK con mensaje de confirmación.
     */
    @PostMapping(value = "/{key}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> upload(@PathVariable String key, @RequestBody byte[] content) {
        s3Service.upload(key, content);
        // `ResponseEntity.ok(...)` = builder para 200 OK con body.
        return ResponseEntity.ok("Uploaded key=" + key + " (" + content.length + " bytes)");
    }

    /**
     * Descarga un objeto desde S3.
     *
     * @param key clave del objeto.
     * @return bytes del objeto con content-type binario.
     */
    @GetMapping(value = "/{key}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@PathVariable String key) {
        byte[] data = s3Service.download(key);
        return ResponseEntity.ok(data);
    }
}
