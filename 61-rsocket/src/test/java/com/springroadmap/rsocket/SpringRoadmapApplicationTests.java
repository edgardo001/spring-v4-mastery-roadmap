package com.springroadmap.rsocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test humo: comprueba que el ApplicationContext arranca (incluyendo el servidor RSocket).
 */
@SpringBootTest
class SpringRoadmapApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto no arranca, este test falla por si solo.
    }
}
