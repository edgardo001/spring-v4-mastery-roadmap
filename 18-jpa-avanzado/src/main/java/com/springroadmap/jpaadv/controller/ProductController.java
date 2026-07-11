package com.springroadmap.jpaadv.controller;

import com.springroadmap.jpaadv.domain.Product;
import com.springroadmap.jpaadv.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST del catálogo de productos.
 *
 * Analogía: la RECEPCIÓN del catálogo. El cliente pide "dame productos que
 * contengan 'lap' en la página 0, 10 por página" y el recepcionista traduce
 * eso a un `Pageable` y consulta al repositorio.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 *   // ANTES: `@Autowired private ProductRepository repo;` en campo.
 *   //        + paginación manual (LIMIT/OFFSET calculados a mano).
 *   // AHORA: constructor injection + Pageable de Spring Data.
 * </pre>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // `final` = una vez asignada la referencia en el constructor no se
    // puede reasignar. Buena práctica para dependencias.
    private final ProductRepository repository;

    // Constructor injection (sin @Autowired — Spring 4+ detecta el único
    // constructor automáticamente).
    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * GET /api/products?q=lap&page=0&size=10
     *
     * `@RequestParam(defaultValue = "")`: si el cliente no manda `q`,
     * asumimos cadena vacía → devuelve todos los productos paginados
     * (porque `LIKE '%%'` matchea todo).
     *
     * `PageRequest.of(page, size)`: construye un Pageable con página y tamaño
     * indicados. Spring podría inyectar `Pageable` directamente, pero aquí
     * lo construimos a mano para dejar explícita la mecánica.
     */
    @GetMapping
    public Page<Product> list(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByNameContaining(q, pageable);
    }

    /**
     * GET /api/products/{id}
     *
     * Usa `findWithCategoryById` — que trae el producto + su category en
     * UN SOLO SELECT gracias a @EntityGraph. Evita el N+1 y la
     * LazyInitializationException si el cliente accede a `category.name`.
     *
     * `Optional.map(...).orElseGet(...)`:
     * - Si hay valor: envuelve en 200 OK.
     * - Si no: devuelve 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        return repository.findWithCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
