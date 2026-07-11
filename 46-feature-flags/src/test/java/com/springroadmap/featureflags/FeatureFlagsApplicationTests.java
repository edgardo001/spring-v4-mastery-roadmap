package com.springroadmap.featureflags;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test mínimo de arranque del contexto Spring.
 * Si algún bean está mal configurado, esta prueba falla.
 */
@SpringBootTest
class FeatureFlagsApplicationTests {

    @Test
    void contextLoads() {
        // Sin aserciones: solo verificamos que el contexto arranca sin excepciones.
    }
}
