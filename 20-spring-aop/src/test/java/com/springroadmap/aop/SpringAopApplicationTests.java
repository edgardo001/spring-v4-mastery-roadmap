package com.springroadmap.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test mínimo: verifica que el contexto de Spring arranca sin errores.
 * Si algún bean está mal configurado (aspect roto, dependencia faltante) este test
 * fallará antes que los demás.
 */
@SpringBootTest
class SpringAopApplicationTests {

    @Test
    void contextLoads() {
        // Cuerpo vacío intencional. Spring intenta levantar el contexto; si algo
        // explota, el test falla.
    }
}
