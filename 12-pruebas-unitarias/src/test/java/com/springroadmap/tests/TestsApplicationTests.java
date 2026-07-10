package com.springroadmap.tests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto de Spring arranca sin errores.
 *
 * @SpringBootTest arranca TODO el contexto (equivalente a levantar la app,
 *   pero sin abrir el puerto HTTP). Si algún bean está mal configurado,
 *   este test FALLA aquí mismo, antes de que se descubra en producción.
 *
 * Es la mínima red de seguridad que debe existir en TODO proyecto Spring Boot.
 */
@SpringBootTest
class TestsApplicationTests {

    @Test
    void contextLoads() {
        // Sin aserciones: si el contexto se carga, el test pasa.
        // Si hay un bean roto (ciclo, dependencia faltante, etc.), la
        // anotación @SpringBootTest lanza excepción y el test falla.
    }
}
