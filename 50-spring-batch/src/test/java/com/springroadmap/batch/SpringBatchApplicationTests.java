package com.springroadmap.batch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test 'contextLoads': verifica que el ApplicationContext arranca sin fallar.
 * Es el "smoke test" mínimo: si esto pasa, todos los @Bean se resolvieron.
 */
@SpringBootTest
class SpringBatchApplicationTests {

    @Test
    void contextLoads() {
        // Sin cuerpo: si @SpringBootTest levanta el contexto sin excepción,
        // el test pasa. Sirve para detectar Beans mal configurados.
    }
}
