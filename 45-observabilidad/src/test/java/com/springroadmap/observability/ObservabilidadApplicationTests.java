package com.springroadmap.observability;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el ApplicationContext se levanta correctamente
 * con todas las auto-configuraciones (actuator, prometheus, tracing, logback JSON).
 */
@SpringBootTest
class ObservabilidadApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no arranca, este test falla automaticamente.
    }
}
