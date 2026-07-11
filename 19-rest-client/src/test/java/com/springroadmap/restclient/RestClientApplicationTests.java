package com.springroadmap.restclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el ApplicationContext arranca correctamente,
 * incluyendo el bean RestClient y el proxy TodoHttpClient.
 */
@SpringBootTest
class RestClientApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no puede levantarse, este test falla automaticamente.
    }
}
