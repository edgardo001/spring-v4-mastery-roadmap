package com.springroadmap.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto Spring se levante sin errores.
 * Si algun bean no se puede inicializar (por ejemplo, falta una dependencia),
 * este test fallara con el detalle del problema.
 */
@SpringBootTest
class ActuatorMicrometerApplicationTests {

    @Test
    void contextLoads() {
        // Vacio a proposito. Si el contexto carga, el test pasa.
    }
}
