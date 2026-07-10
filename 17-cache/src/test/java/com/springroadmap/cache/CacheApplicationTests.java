package com.springroadmap.cache;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto de Spring arranca sin errores
 * (incluye el CacheManager de Caffeine y el aspecto @EnableCaching).
 */
@SpringBootTest
class CacheApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no arranca, este test falla automáticamente.
    }
}
