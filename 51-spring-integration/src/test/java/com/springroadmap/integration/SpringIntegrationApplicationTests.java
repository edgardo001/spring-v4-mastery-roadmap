package com.springroadmap.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test más básico: verifica que el ApplicationContext arranca sin errores.
 *
 * <p>Si falla, es que algún @Bean no puede ser creado (dependencias faltantes,
 * configuración incorrecta de Spring Integration, canal mal declarado, etc.).</p>
 *
 * <h2>ANTES vs AHORA</h2>
 * <pre>
 * // ANTES (Boot 2/3): también existía @WebMvcTest, @DataJpaTest, etc.
 * // AHORA (Boot 4.1.0): esas anotaciones fueron ELIMINADAS.
 * //   El único slice test disponible es @SpringBootTest.
 * </pre>
 */
@SpringBootTest
class SpringIntegrationApplicationTests {

    @Test
    void contextLoads() {
        // Si Spring pudo levantar el contexto, la prueba pasa. No hay assertions.
    }
}
