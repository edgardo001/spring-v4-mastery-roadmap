package com.springroadmap.async;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: si el pool o el @EnableAsync está mal configurado, el
 * ApplicationContext no arranca y este test falla. Es la primera línea de
 * defensa contra errores de cableado.
 */
@SpringBootTest
class AsyncApplicationTests {

    @Test
    void contextLoads() {
        // Vacío intencionalmente. El propio arranque del contexto ES la prueba.
    }
}
