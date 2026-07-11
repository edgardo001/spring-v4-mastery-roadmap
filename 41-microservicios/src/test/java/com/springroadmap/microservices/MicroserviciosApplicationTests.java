package com.springroadmap.microservices;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test minimo que arranca el ApplicationContext completo. Si algun bean falla
 * (falta dependencia, mal cableado), este test lo detecta antes que cualquier
 * otro. Regla del roadmap: TODO modulo debe tener 'contextLoads'.
 */
@SpringBootTest
class MicroserviciosApplicationTests {

    @Test
    void contextLoads() {
        // Intencionalmente vacio: el mero arranque del contexto es la validacion.
    }
}
