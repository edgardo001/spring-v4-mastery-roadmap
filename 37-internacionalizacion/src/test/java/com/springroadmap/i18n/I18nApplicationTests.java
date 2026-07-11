package com.springroadmap.i18n;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de carga del contexto. Valida que todos los beans se cablean bien:
 * MessageSource, LocaleResolver y GreetingController.
 */
@SpringBootTest
class I18nApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto arrancó sin lanzar excepciones, el test pasa.
    }
}
