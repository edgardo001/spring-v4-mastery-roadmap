package com.springroadmap.resilience;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test mínimo: verifica que el ApplicationContext se levante sin errores.
 * Si algún bean (FlakyService, ResilientClient, CircuitBreaker, Retry) está
 * mal cableado, este test falla al arrancar.
 */
@SpringBootTest  // Carga TODO el contexto (equivalente a arrancar la app pero sin el server HTTP).
class ResilienceApplicationTests {

    @Test
    void contextLoads() {
        // Sin asserts: el test pasa si el contexto se levanta.
    }
}
