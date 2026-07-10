package com.springroadmap.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto Spring carga correctamente con
 * el @RestController, el @RestControllerAdvice y todos los @Bean del módulo.
 *
 * Si algún bean está mal configurado (por ejemplo, un constructor con una
 * dependencia inexistente), este test falla al cargar el contexto.
 */
@SpringBootTest
class ExceptionsApplicationTests {

    @Test
    void contextLoads() {
        // Vacío intencionalmente. El propio arranque del contexto ES la prueba.
    }
}
