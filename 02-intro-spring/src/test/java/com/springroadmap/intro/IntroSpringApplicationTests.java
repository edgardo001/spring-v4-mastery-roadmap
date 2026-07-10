package com.springroadmap.intro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * "Smoke test" del contexto de Spring.
 *
 * @SpringBootTest arranca la aplicación completa (igual que el main),
 * pero SIN levantar el servidor web por defecto. Si algún bean está
 * mal configurado, este test falla al cargar el contexto.
 *
 * Es la primera verificación de sanidad de cualquier proyecto Spring:
 * "¿al menos arranca?". Si esto pasa, sabemos que la autoconfiguración,
 * el escaneo de componentes y las dependencias del pom están bien.
 */
@SpringBootTest
class IntroSpringApplicationTests {

    /**
     * @Test le dice a JUnit 5 que este método es un caso de prueba
     * ejecutable. El método está intencionalmente VACÍO: si el contexto
     * de Spring se carga sin lanzar excepción, el test pasa.
     */
    @Test
    void contextLoads() {
        // Sin assert. Sin excepción = éxito.
    }
}
