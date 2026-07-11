package com.springroadmap.testcontainers.controller;

import com.springroadmap.testcontainers.domain.Product;
import com.springroadmap.testcontainers.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST del CRUD de Products.
 *
 * <p>Analogía: es la "recepción" del hotel. Recibe peticiones HTTP (huéspedes),
 * las traduce a llamadas al repositorio (llaves de habitación) y devuelve
 * respuestas HTTP (confirmaciones).</p>
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET    /api/products         — lista todos los productos</li>
 *   <li>GET    /api/products/{id}    — obtiene uno por id</li>
 *   <li>POST   /api/products         — crea uno nuevo</li>
 *   <li>DELETE /api/products/{id}    — elimina uno</li>
 * </ul></p>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué inyectar por constructor en vez de @Autowired?"
 *   Constructor injection permite marcar el campo <code>final</code> (inmutable),
 *   facilita los tests unitarios (no necesita Spring) y es la práctica oficial
 *   recomendada desde Spring 4.3.</p>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // final: el repositorio no cambia una vez inyectado.
    private final ProductRepository repository;

    // Constructor injection. Spring detecta el único constructor y le pasa el bean.
    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todos los productos. GET /api/products
     */
    @GetMapping
    public List<Product> list() {
        return repository.findAll();
    }

    /**
     * Obtiene un producto por id. GET /api/products/{id}
     * Retorna 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        // Optional evita NullPointerException: encapsula "puede o no existir".
        return repository.findById(id)
                .map(ResponseEntity::ok)         // si existe -> 200 OK con el body
                .orElse(ResponseEntity.notFound().build()); // si no -> 404
    }

    /**
     * Crea un producto. POST /api/products con body JSON.
     */
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product saved = repository.save(product);
        return ResponseEntity.ok(saved);
    }

    /**
     * Elimina un producto. DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();  // 204
    }
}

/*
 * ============================================================================
 * ANTES (Java 8 / Spring MVC 4) vs AHORA (Spring 6 + Boot 4)
 * ============================================================================
 * ANTES: @Controller + @ResponseBody por método.
 *   @Controller
 *   public class ProductController {
 *       @RequestMapping(value="/api/products", method=RequestMethod.GET)
 *       @ResponseBody
 *       public List<Product> list() { ... }
 *   }
 *
 * AHORA: @RestController implica @ResponseBody y @GetMapping es más conciso.
 *   @RestController @RequestMapping("/api/products")
 *   public class ProductController {
 *       @GetMapping public List<Product> list() { ... }
 *   }
 *
 * ANTES: chequeo null manual.
 *   Product p = repo.findById(id);
 *   if (p == null) return ResponseEntity.notFound().build();
 *   return ResponseEntity.ok(p);
 *
 * AHORA: Optional + method reference.
 *   return repo.findById(id).map(ResponseEntity::ok).orElse(notFound());
 * ============================================================================
 */
