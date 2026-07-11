package com.springroadmap.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de humo: verifica que el contexto de Spring arranca correctamente
 * (todos los beans se crean sin errores).
 */
@SpringBootTest
class JwtApplicationTests {

    @Test
    void contextLoads() {
        // Si Spring no puede armar el contexto, este test falla antes de este metodo.
    }
}
