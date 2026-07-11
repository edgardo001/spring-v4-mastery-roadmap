package com.springroadmap.k8s;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Modulo 48 - Smoke test: verifica que el contexto de Spring arranca sin
 * errores. Si este test falla, no tiene sentido intentar desplegar en K8s:
 * el contenedor no llegaria a pasar el livenessProbe.
 */
@SpringBootTest
class KubernetesApplicationTests {

    @Test
    void contextLoads() {
        // El propio arranque del contexto es la asercion.
    }
}
