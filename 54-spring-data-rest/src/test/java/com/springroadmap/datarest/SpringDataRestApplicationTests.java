package com.springroadmap.datarest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test minimo: verifica que el contexto de Spring arranca sin errores.
 * Si esta clase pasa, todos los beans (repositorios, auto-config REST) estan bien conectados.
 */
@SpringBootTest
class SpringDataRestApplicationTests {

    @Test
    void contextLoads() {
        // Vacio a proposito: el mero hecho de arrancar el contexto es la prueba.
    }
}
