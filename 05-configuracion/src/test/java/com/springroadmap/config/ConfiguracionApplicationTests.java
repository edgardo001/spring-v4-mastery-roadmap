package com.springroadmap.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto arranca con AppProperties bindeado
 * y todos los @Service/@RestController cableados.
 */
@SpringBootTest
class ConfiguracionApplicationTests {

    @Test
    void contextLoads() {
        // Vacío intencionalmente: el arranque del contexto ES la prueba.
    }
}
