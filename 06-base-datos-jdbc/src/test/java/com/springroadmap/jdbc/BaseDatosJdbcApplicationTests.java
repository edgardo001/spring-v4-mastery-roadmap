package com.springroadmap.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test mínimo: verifica que el contexto de Spring arranca correctamente
 * (incluyendo el DataSource H2, la ejecución de schema.sql/data.sql y la
 * creación del JdbcTemplate y de los beans de repositorio/controller).
 */
@SpringBootTest
class BaseDatosJdbcApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no arrancara, este test fallaría antes de llegar aquí.
    }
}
