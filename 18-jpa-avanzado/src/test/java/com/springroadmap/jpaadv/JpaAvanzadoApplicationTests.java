package com.springroadmap.jpaadv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto de Spring arranca sin errores.
 * Si hay un @Bean mal configurado, un @Entity roto o un YAML inválido, este
 * test falla antes que cualquier otro.
 */
@SpringBootTest
class JpaAvanzadoApplicationTests {

    @Test
    void contextLoads() {
        // Cuerpo vacío intencional. Si `@SpringBootTest` no puede levantar
        // el contexto, el test falla automáticamente.
    }
}
