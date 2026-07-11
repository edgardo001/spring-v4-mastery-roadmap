package com.springroadmap.cicd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba clásica "contextLoads": si el contexto de Spring no arranca,
 * el CI fallará inmediatamente evitando desplegar una app rota.
 */
@SpringBootTest
class CiCdApplicationTests {

    @Test
    void contextLoads() {
        // Vacío a propósito: el simple hecho de que @SpringBootTest levante el
        // contexto sin excepciones ya es la verificación.
    }
}
