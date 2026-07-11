package com.springroadmap.restadv.repository;

import com.springroadmap.restadv.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repositorio in-memory con 20 productos precargados.
 *
 * Analogía: una BODEGA con 20 estantes numerados. `Pageable` es el
 * carrito que dice "quiero los estantes del 5 al 10" — evitamos cargar
 * los 20 estantes en RAM.
 *
 * Antes (JDBC crudo): `SELECT * FROM products LIMIT ? OFFSET ?` armado a mano.
 * Ahora: recibimos un `Pageable` (page + size) y devolvemos `Page<Product>`
 * que ya trae metadatos (`totalPages`, `totalElements`, `first`, `last`).
 */
@Repository
public class ProductRepository {

    // ConcurrentHashMap: thread-safe. Múltiples requests HTTP simultáneos
    // pueden leer sin que la JVM lance ConcurrentModificationException.
    private final ConcurrentHashMap<Long, Product> store = new ConcurrentHashMap<>();

    public ProductRepository() {
        // Precarga: 20 productos con `version` inicial "v1" para el ETag.
        for (long i = 1; i <= 20; i++) {
            store.put(i, new Product(
                    i,
                    "Product-" + i,
                    new BigDecimal("10.00").multiply(BigDecimal.valueOf(i)),
                    "v1"
            ));
        }
    }

    /**
     * Búsqueda paginada. Ordena por id ascendente y aplica la ventana
     * (offset, limit) del `Pageable`.
     */
    public Page<Product> findAll(Pageable pageable) {
        List<Product> all = store.values().stream()
                .sorted((a, b) -> Long.compare(a.id(), b.id()))
                .collect(Collectors.toList());

        int total = all.size();
        int from = (int) Math.min(pageable.getOffset(), total);
        int to = Math.min(from + pageable.getPageSize(), total);
        List<Product> slice = all.subList(from, to);

        // PageImpl(content, pageable, totalElements) — la firma que
        // Spring Data recomienda para evitar warnings de serialización.
        return new PageImpl<>(slice, pageable, total);
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
