package com.springroadmap.restadv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el ApplicationContext arranca sin errores.
 * Si un @Bean está mal configurado, este test lo detecta en el acto.
 */
@SpringBootTest
class RestAvanzadoApplicationTests {

    @Test
    void contextLoads() {
        // El solo hecho de que @SpringBootTest levante el contexto es la aserción.
    }
}
