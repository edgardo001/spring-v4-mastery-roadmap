package com.springroadmap.testcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test — verifica que el contexto de Spring arranca correctamente con H2.
 *
 * <p>Este test SIEMPRE es verde, incluso sin Docker. Sirve como red de seguridad
 * mínima: si este test falla, es porque el pom o el application.yml están rotos.</p>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué el método está vacío?"
 *   Porque el mero hecho de que <code>@SpringBootTest</code> logre construir el
 *   contexto YA es la aserción. Si algún bean fallara, el test fallaría al cargar.</p>
 */
@SpringBootTest
class TestcontainersApplicationTests {

    @Test
    void contextLoads() {
        // Si llegamos aquí, el contexto arrancó OK con H2 en memoria.
    }
}
