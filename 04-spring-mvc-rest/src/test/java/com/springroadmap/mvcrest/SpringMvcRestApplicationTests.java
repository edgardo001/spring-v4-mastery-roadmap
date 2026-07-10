package com.springroadmap.mvcrest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto Spring carga con todos los
 * @RestController, @Repository y @Bean del módulo.
 *
 * Si algún bean está mal configurado (p. ej. constructor con dependencia
 * inexistente), este test falla al cargar el contexto.
 */
@SpringBootTest
class SpringMvcRestApplicationTests {

    @Test
    void contextLoads() {
        // Vacío intencionalmente. El propio arranque del contexto ES la prueba.
    }
}
