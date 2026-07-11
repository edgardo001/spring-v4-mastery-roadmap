package com.springroadmap.docs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el ApplicationContext arranca correctamente
 * incluyendo el auto-configuración de springdoc-openapi y el @Bean
 * customOpenAPI() de OpenApiConfig.
 */
@SpringBootTest
class DocumentacionApiApplicationTests {

    @Test
    void contextLoads() {
        // Sin cuerpo: si el contexto no arranca, Spring lanza excepción y el test falla.
    }
}
