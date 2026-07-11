package com.springroadmap.docs.controller;

import com.springroadmap.docs.model.Book;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Controller REST para gestionar libros. Está anotado con @Operation y
 * @ApiResponse para que la documentación generada por springdoc muestre
 * resúmenes claros y códigos de respuesta esperados en Swagger UI.
 *
 * Persistencia en memoria (List + AtomicLong) para mantener el módulo
 * enfocado en la documentación de la API.
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Operaciones CRUD sobre el catálogo de libros")
public class BookController {

    private final List<Book> books = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public BookController() {
        // Datos semilla para que Swagger UI muestre respuestas no vacías al probar.
        books.add(new Book(sequence.incrementAndGet(), "Clean Code", "Robert C. Martin"));
        books.add(new Book(sequence.incrementAndGet(), "Effective Java", "Joshua Bloch"));
    }

    @Operation(summary = "Listar todos los libros del catálogo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de libros retornada correctamente")
    })
    @GetMapping
    public List<Book> findAll() {
        return List.copyOf(books);
    }

    @Operation(summary = "Buscar un libro por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un libro con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Book> findById(@PathVariable Long id) {
        Optional<Book> found = books.stream().filter(b -> b.id().equals(id)).findFirst();
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo libro en el catálogo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Libro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book input) {
        Book saved = new Book(sequence.incrementAndGet(), input.title(), input.author());
        books.add(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
