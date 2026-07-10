package com.springroadmap.jdbc.controller;

import com.springroadmap.jdbc.domain.Customer;
import com.springroadmap.jdbc.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST del CRUD de customers.
 *
 * Analogía: es el "mostrador de atención al público". Recibe pedidos HTTP,
 * pregunta al repositorio y devuelve la respuesta con el código correcto.
 *
 * Por simplicidad pedagógica, el controller habla directamente con el
 * repositorio (no hay Service). En módulos posteriores separaremos capas.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué 201 Created y no 200 OK al crear?"
 *   Es la convención REST: 201 significa "recurso creado". El header
 *   Location apunta a la URL del nuevo recurso.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository repository;

    public CustomerController(final CustomerRepository repository) {
        this.repository = repository;
    }

    /** GET /api/customers -> 200 con la lista. */
    @GetMapping
    public List<Customer> findAll() {
        return repository.findAll();
    }

    /**
     * GET /api/customers/{id} -> 200 con el customer, o 404 si no existe.
     *
     * ANTES (Java 8):
     *   Optional<Customer> opt = repository.findById(id);
     *   if (opt.isPresent()) return ResponseEntity.ok(opt.get());
     *   return ResponseEntity.notFound().build();
     * AHORA (Java 21): usamos `map` + `orElseGet` en una sola expresión.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable final Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/customers -> 201 Created + header Location.
     * Recibimos el Customer sin id; la BD lo genera y lo devolvemos.
     */
    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody final Customer input) {
        final Customer saved = repository.save(input);
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** DELETE /api/customers/{id} -> 204 No Content si borró, 404 si no existía. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        final boolean deleted = repository.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
