package com.springroadmap.springangular;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto de Spring arranca sin errores.
 * Si algo esta mal cableado (bean faltante, config invalida) este test lo detecta.
 */
@SpringBootTest
class SpringAngularApplicationTests {

    @Test
    void contextLoads() {
        // No assertions: si el contexto arranca, pasa. Si algo falla, revienta antes.
    }
}
