package com.springroadmap.validation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el ApplicationContext arranca sin errores.
 *
 * Si este test falla, casi seguro es por:
 *   - Falta de dependencia (starter-validation, starter-web).
 *   - Error de configuración en application.yml.
 *   - Un bean con dependencias imposibles de resolver.
 */
@SpringBootTest
class ValidationApplicationTests {

    @Test
    void contextLoads() {
        // Vacío a propósito. Si el contexto no cargara,
        // @SpringBootTest habría hecho fallar el test antes de llegar aquí.
    }
}
