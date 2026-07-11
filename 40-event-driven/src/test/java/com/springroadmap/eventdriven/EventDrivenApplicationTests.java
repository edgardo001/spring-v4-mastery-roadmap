package com.springroadmap.eventdriven;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto de Spring arranca sin errores.
 * Si algún @Bean está mal configurado, este test falla al levantar.
 */
@SpringBootTest
class EventDrivenApplicationTests {

    @Test
    void contextLoads() {
        // Sin aserciones: si Spring levanta, pasa.
    }
}
