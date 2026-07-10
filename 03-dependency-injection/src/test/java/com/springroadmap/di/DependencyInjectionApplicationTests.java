package com.springroadmap.di;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto Spring carga con todos los
 * @Service, @Repository, @Controller y @Bean del módulo.
 *
 * Si algún bean está mal configurado (p. ej. constructor con dependencia
 * inexistente), este test falla al cargar el contexto.
 */
@SpringBootTest
class DependencyInjectionApplicationTests {

    @Test
    void contextLoads() {
        // Vacío intencionalmente. El propio arranque del contexto ES la prueba.
    }
}
