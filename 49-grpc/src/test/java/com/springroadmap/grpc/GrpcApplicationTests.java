package com.springroadmap.grpc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de humo: verifica que el contexto Spring arranca sin errores.
 * Si un bean esta mal configurado, este test falla y avisa temprano.
 */
@SpringBootTest
class GrpcApplicationTests {

    @Test
    void contextLoads() {
        // Vacio a proposito: si @SpringBootTest logra levantar el contexto, pasa.
    }
}
