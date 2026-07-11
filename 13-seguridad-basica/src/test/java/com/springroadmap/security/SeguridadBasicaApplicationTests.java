package com.springroadmap.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de smoke: verifica que el contexto de Spring arranque sin errores.
 *
 * <p>Si esta prueba falla es porque falta un bean, hay un conflicto de
 * versiones, o la configuración de Security está mal declarada.</p>
 */
@SpringBootTest
class SeguridadBasicaApplicationTests {

    @Test
    void contextLoads() {
        // Vacío a propósito: @SpringBootTest ya intenta cargar el contexto.
    }
}
