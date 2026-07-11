package com.springroadmap.securityadv.controller;

import com.springroadmap.securityadv.domain.Document;
import com.springroadmap.securityadv.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST que expone {@link DocumentService}.
 *
 * <p><b>Nota importante:</b> este controlador NO tiene anotaciones de
 * seguridad. Toda la autorizacion vive en el SERVICIO. Esa es la buena
 * practica: el guardia esta cerca del recurso a proteger, no en la
 * capa web.</p>
 *
 * <p>Palabras clave:
 * <ul>
 *   <li>{@code @RestController} = {@code @Controller} + {@code @ResponseBody}
 *       en todos los metodos.</li>
 *   <li>{@code @RequestMapping("/api/docs")} — prefijo comun de rutas.</li>
 *   <li>{@code @GetMapping}, {@code @DeleteMapping} — atajos HTTP.</li>
 *   <li>{@code @PathVariable} — extrae la variable de la URL.</li>
 * </ul>
 * </p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES: inyeccion con campo + @Autowired
 * //   @Autowired private DocumentService service;
 *
 * // AHORA: inyeccion por constructor (unico constructor → sin @Autowired).
 * //   private final DocumentService service;
 * //   public DocumentController(DocumentService service) { this.service = service; }
 * </pre>
 */
@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    private final DocumentService service;

    /**
     * Constructor injection: Spring detecta el unico constructor
     * y le pasa el bean {@link DocumentService}.
     */
    public DocumentController(DocumentService service) {
        this.service = service;
    }

    /**
     * GET /api/docs → lista todos los documentos.
     * Requiere {@code USER} o {@code ADMIN} (verificado en el servicio).
     */
    @GetMapping
    public List<Document> findAll() {
        return service.findAll();
    }

    /**
     * GET /api/docs/{id} → devuelve un documento si el usuario es su dueño.
     * Object-level security aplicada en el servicio via {@code @PostAuthorize}.
     */
    @GetMapping("/{id}")
    public Document findById(@PathVariable Long id) {
        return service.findById(id);
    }

    /**
     * DELETE /api/docs/{id} → borra un documento.
     * Solo {@code ADMIN} (verificado en el servicio via {@code @PreAuthorize}).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
