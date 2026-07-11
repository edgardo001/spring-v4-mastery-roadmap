package com.springroadmap.testcontainers.repository;

import com.springroadmap.testcontainers.domain.Product;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración del ProductRepository contra un Postgres REAL en contenedor Docker.
 *
 * <p><b>REQUIERE Docker Desktop en ejecución.</b> Por eso está marcado con
 * <code>@Disabled</code>. Para ACTIVARLO:
 * <ol>
 *   <li>Instala e inicia Docker Desktop.</li>
 *   <li>Comenta o borra la anotación <code>@Disabled</code>.</li>
 *   <li>Ejecuta <code>mvn test</code>.</li>
 * </ol></p>
 *
 * <p><b>¿Por qué @Disabled y no Assumptions.assumeTrue?</b>
 *   Porque <code>@Testcontainers</code> arranca el <code>@Container static</code>
 *   ANTES de <code>@BeforeAll</code>. Si Docker no está, revienta con excepción
 *   antes de que un <code>assumeTrue</code> pueda saltar los tests.
 *   Lección aprendida en el módulo 25 (ver MEMORY.md).</p>
 *
 * <p>Analogía: es como abrir una tienda pop-up de Postgres solo para probar,
 * y cerrarla al terminar. Todo automatizado por Testcontainers.</p>
 */
@Disabled("Requiere Docker Desktop en ejecución. Para activar: elimina @Disabled y ejecuta 'mvn test'.")
@SpringBootTest
@Testcontainers
class ProductRepositoryPostgresTest {

    /**
     * Contenedor Postgres 16 alpine — se arranca UNA vez para toda la clase.
     * <code>static</code> hace que dure toda la vida de la clase de test.
     * <code>@Container</code> integra el ciclo de vida con JUnit 5.
     */
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("productsdb")
            .withUsername("test")
            .withPassword("test");

    /**
     * Registra las propiedades del contenedor DINÁMICAMENTE en el Environment
     * de Spring, ANTES de que se construyan los beans de datasource.
     * Sobreescribe la URL H2 del application.yml.
     */
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ProductRepository repository;

    @Test
    void savesAndReadsProductInRealPostgres() {
        // Arrange: crea un producto de dominio.
        Product product = new Product("Café Premium", new BigDecimal("9.99"));

        // Act: guarda en Postgres real.
        Product saved = repository.save(product);
        Optional<Product> found = repository.findById(saved.getId());

        // Assert: el producto sobrevivió el viaje ida-vuelta.
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Café Premium");
        assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("9.99"));
    }
}

/*
 * ============================================================================
 * ANTES (Spring Test clásico) vs AHORA (Testcontainers)
 * ============================================================================
 * ANTES: base de datos "fake" en memoria (H2) que no se parece a producción.
 *   - Diferencias sutiles de SQL entre H2 y Postgres pasaban desapercibidas.
 *   - "En mi máquina funciona" clásico.
 *
 * AHORA: Postgres REAL en contenedor.
 *   - Mismo motor que producción, misma versión, mismos casts SQL.
 *   - El contenedor nace y muere junto con el test.
 * ============================================================================
 */
