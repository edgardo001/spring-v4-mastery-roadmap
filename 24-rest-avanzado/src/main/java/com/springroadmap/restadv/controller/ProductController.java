package com.springroadmap.restadv.controller;

import com.springroadmap.restadv.domain.Product;
import com.springroadmap.restadv.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * Controller REST del catálogo.
 *
 * Enseña TRES técnicas de REST avanzado:
 *   1. Paginación con `Pageable` (Spring resuelve `?page=&size=` automáticamente).
 *   2. ETag + If-None-Match para cache condicional (ahorra ancho de banda).
 *   3. Versionado por header `X-API-Version` (mismo endpoint, forma distinta).
 *
 * ANTES (Java 8 / Spring MVC clásico):
 *   - Paginación manual: leer `page` y `size` como `@RequestParam` y armar
 *     LIMIT/OFFSET a mano.
 *   - Cache: siempre devolver 200 con el JSON entero — el cliente no tiene
 *     forma de decir "ya tengo la versión X".
 *   - Versionado: crear `/v1/products` y `/v2/products` (URI versioning).
 *
 * AHORA (Spring Boot 4.1):
 *   - `Pageable` inyectado por el `PageableHandlerMethodArgumentResolver`.
 *   - `ETag` + `If-None-Match`: si coincide, respondemos 304 (Not Modified).
 *   - Header-based versioning: el mismo URI cambia de forma según `X-API-Version`.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repository;

    // Constructor injection — Spring 4+ lo detecta sin @Autowired.
    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * GET /api/products?page=0&size=5
     *
     * Retorna un `Page<Product>` que Jackson serializa como:
     *   { "content": [...], "totalPages": ..., "totalElements": ..., ... }
     *
     * El `Pageable` viene resuelto por el argument resolver:
     *   - `?page=0&size=5` → PageRequest.of(0, 5)
     *   - sin params        → default (page=0, size=20).
     */
    @GetMapping
    public Page<Product> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * GET /api/products/{id}
     *
     * Tres caminos:
     *  - Header `X-API-Version: 2` presente → devuelve JSON envuelto en `data`.
     *  - Header `If-None-Match` coincide con el ETag actual → 304 Not Modified
     *    (sin cuerpo — cliente reutiliza su copia cacheada).
     *  - Caso normal → 200 con el producto + header `ETag`.
     *
     * @param id               id del producto.
     * @param ifNoneMatch      valor enviado por el cliente si ya tiene una copia.
     * @param apiVersion       versión pedida por header (default v1 → sin wrap).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> get(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch,
            @RequestHeader(value = "X-API-Version", required = false) String apiVersion) {

        Optional<Product> maybe = repository.findById(id);
        if (maybe.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Product product = maybe.get();

        // ETag = comilla + version + comilla (formato oficial RFC 7232).
        String etag = "\"" + product.version() + "\"";

        // Si el cliente ya tiene esta versión, no gastamos ancho de banda.
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .build();
        }

        // Versionado por header: v2 envuelve el cuerpo en { data: ... }.
        if ("2".equals(apiVersion)) {
            return ResponseEntity.ok()
                    .eTag(etag)
                    .body(Map.of("data", product));
        }

        // v1 (default) — respuesta plana.
        return ResponseEntity.ok()
                .eTag(etag)
                .body(product);
    }
}
