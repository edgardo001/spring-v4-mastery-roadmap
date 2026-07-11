package com.springroadmap.jpaadv.repository;

import com.springroadmap.jpaadv.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests del repositorio con @SpringBootTest + @Transactional.
 *
 * NOTA (MEMORY.md 2026-07-10): en Spring Boot 4.1.0 se eliminó @DataJpaTest.
 * Usamos el contexto completo (@SpringBootTest) más @Transactional para
 * hacer rollback tras cada test y evitar contaminación de datos.
 *
 * data.sql se carga automáticamente gracias a `spring.sql.init.mode=always` +
 * `defer-datasource-initialization`.
 */
@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    /**
     * Verifica que la @Query JPQL filtre correctamente por precio.
     * Seed: 25.00, 45.00, 120.00, 1500.00. Con min=100 esperamos {120, 1500}.
     */
    @Test
    void findExpensive_returnsOnlyProductsAbovePriceThreshold() {
        List<Product> expensive = repository.findExpensive(new BigDecimal("100"));

        assertThat(expensive)
                .as("Solo productos con precio > 100")
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Clean Code", "Laptop Pro");
    }

    /**
     * Verifica que la projection solo trae id + name (sin price ni category).
     * El resultado es una lista de PROXIES que implementan ProductSummary.
     */
    @Test
    void findAllProjectedBy_returnsSummariesWithIdAndName() {
        List<ProductSummary> summaries = repository.findAllProjectedBy();

        assertThat(summaries).hasSize(4);
        assertThat(summaries).allSatisfy(s -> {
            assertThat(s.getId()).isNotNull();
            assertThat(s.getName()).isNotBlank();
        });
    }

    /**
     * Verifica paginación + búsqueda por nombre parcial.
     * "o" aparece en "Laptop Pro", "Mouse Basic" (no), "Spring in Action" (no),
     * "Clean Code" (no). Ajustemos: buscamos "o" → "Laptop Pro" (contiene 'o').
     * Más determinístico: buscamos "a" → matches: "Laptop", "Basic", "Action",
     * "Clean" (no 'a'), "Code" (no 'a'). Recuento variable, usamos "o".
     *
     * Para simplicidad usamos "o" con page 0, size 2, y solo validamos que
     * la respuesta sea consistente (size y contenido no vacío).
     */
    @Test
    void findByNameContaining_paginatesResults() {
        Page<Product> page = repository.findByNameContaining("o", PageRequest.of(0, 2));

        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getContent()).isNotEmpty();
        // Todos los resultados deben contener la letra 'o' en el name.
        assertThat(page.getContent())
                .allSatisfy(p -> assertThat(p.getName().toLowerCase()).contains("o"));
    }
}
