package com.springroadmap.testadv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test — verifica que el contexto de Spring arranca con la configuración por defecto (H2).
 *
 * Este test NO requiere Docker. Es el "hola mundo" del testing: si el contexto no carga,
 * nada más funcionará. Cumple con la regla de MEMORY.md: "al menos contextLoads + 1 test".
 *
 * Palabras clave explicadas:
 *   - {@code @SpringBootTest}: arranca el ApplicationContext completo (como si fuera producción)
 *     pero SIN levantar el servidor web (por defecto {@code webEnvironment = MOCK}).
 *   - {@code @Test}: JUnit 5 marca el método como caso de prueba.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES — {@code @RunWith(SpringJUnit4ClassRunner.class)} + {@code @SpringApplicationConfiguration}.
 *   AHORA — Una sola anotación {@code @SpringBootTest} basta. JUnit 5 no usa @RunWith.
 */
@SpringBootTest
class TestingAvanzadoApplicationTests {

    @Test
    void contextLoads() {
        // El test pasa si el contexto se levanta sin excepción.
        // Es el chequeo de humo (smoke test) básico que TODO módulo Spring debe tener.
    }
}
