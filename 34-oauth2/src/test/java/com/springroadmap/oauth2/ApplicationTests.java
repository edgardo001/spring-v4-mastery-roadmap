package com.springroadmap.oauth2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: el contexto Spring arranca sin errores.
 *
 * `@SpringBootTest` levanta TODA la aplicación (no test-slice, esas fueron
 * eliminadas en Boot 4.1.0 — ver MEMORY.md). `webEnvironment=NONE` evita abrir
 * puerto en este test para ahorrar tiempo.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no cargara, este test fallaría antes de este cuerpo.
    }
}
