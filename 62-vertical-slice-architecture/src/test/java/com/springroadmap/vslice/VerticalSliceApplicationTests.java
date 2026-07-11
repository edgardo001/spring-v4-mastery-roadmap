package com.springroadmap.vslice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de integracion minimo: verifica que el contexto Spring carga
 * sin errores (todos los beans se instancian y se cablean).
 */
@SpringBootTest
class VerticalSliceApplicationTests {

    @Test
    void contextLoads() {
        // Si llegamos aqui, el ApplicationContext levanto OK. No hace falta assert.
    }
}
