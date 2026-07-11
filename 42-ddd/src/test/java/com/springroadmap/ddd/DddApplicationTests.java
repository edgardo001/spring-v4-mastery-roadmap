package com.springroadmap.ddd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de humo: verifica que el contexto de Spring arranca sin errores.
 * Si este test falla, hay un problema de configuracion (beans, JPA, etc.).
 */
@SpringBootTest
class DddApplicationTests {

    @Test
    void contextLoads() {
        // Solo importa que no lance excepcion al arrancar el contexto.
    }
}
