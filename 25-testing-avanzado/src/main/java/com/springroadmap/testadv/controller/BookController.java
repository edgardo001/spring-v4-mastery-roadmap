package com.springroadmap.testadv.controller;

import com.springroadmap.testadv.domain.Book;
import com.springroadmap.testadv.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Controller REST mínimo del CRUD de Books.
 *
 * Enfoque pedagógico: este módulo trata de TESTING con Testcontainers, no de arquitectura REST.
 * Por eso el controller habla directo con el repositorio (sin capa Service). Para ver la
 * separación por capas correcta, revisar módulos 04 y 07.
 *
 * Palabras clave explicadas:
 *   - {@code @RestController}: combo de {@code @Controller} + {@code @ResponseBody}. Devuelve JSON.
 *   - {@code @RequestMapping("/api/books")}: prefijo común a todos los endpoints de esta clase.
 *   - Constructor injection: pasamos el {@link BookRepository} por constructor. Spring lo cablea
 *     automáticamente. Sin Lombok (según MEMORY.md).
 *   - {@code ResponseEntity}: te permite controlar código HTTP y headers, no solo el cuerpo.
 *   - {@code URI.create(...)}: header {@code Location} típico en respuestas 201 Created.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES — {@code books.stream().collect(Collectors.toList())} para devolver la lista.
 *   AHORA — {@code books.stream().toList()} (Java 16+). Aquí devolvemos directo {@code List<Book>}.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository repository;

    // Constructor injection: preferido sobre @Autowired en campo (ver AGENTS.md, error #1).
    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    /** GET /api/books — lista todos los libros. */
    @GetMapping
    public List<Book> findAll() {
        return repository.findAll();
    }

    /** GET /api/books/{id} — busca por id, 404 si no existe. */
    @GetMapping("/{id}")
    public ResponseEntity<Book> findOne(@PathVariable Long id) {
        // Optional.map(...).orElseGet(...): patrón moderno para "si existe devuelve X, si no devuelve Y".
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/books — crea un libro. Aviso pedagógico: para simplificar recibimos la Entity
     * directamente, pero en producción se debe usar un DTO (ver AGENTS.md, error #5).
     */
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book incoming) {
        Book saved = repository.save(new Book(incoming.getTitle(), incoming.getAuthor()));
        return ResponseEntity.created(URI.create("/api/books/" + saved.getId())).body(saved);
    }

    /** DELETE /api/books/{id} — elimina; 204 No Content siempre (idempotente). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
