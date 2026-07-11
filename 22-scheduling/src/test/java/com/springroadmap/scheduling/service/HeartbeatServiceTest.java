package com.springroadmap.scheduling.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test unitario puro del HeartbeatService.
 *
 * DECISIÓN de diseño (importante):
 *   NO usamos @SpringBootTest ni esperamos al scheduler. En su lugar
 *   llamamos DIRECTAMENTE los métodos heartbeat() y cronTick() como si
 *   fueran métodos normales, y verificamos que los contadores suban.
 *
 *   ¿Por qué?
 *     1. Rápido: <100 ms vs. 6+ segundos de Thread.sleep.
 *     2. Determinístico: no depende del reloj ni del scheduler.
 *     3. Sin dependencias extra (no requiere Awaitility).
 *     4. La responsabilidad del scheduler la valida Spring, no nosotros.
 *
 *   El comportamiento "cada X segundos" ya está probado por el equipo de
 *   Spring. Nosotros solo debemos probar QUE nuestro método hace lo
 *   correcto cuando se le llama, no que Spring sabe llamar a su hora.
 *
 * ANTES (Java 8, JUnit 4):
 *   import org.junit.Test;
 *   @Test public void heartbeat_incrementaTickCount() { ... }
 *
 * AHORA (Java 21, JUnit 5):
 *   import org.junit.jupiter.api.Test;
 *   @Test void heartbeat_incrementaTickCount() { ... }   // sin `public`
 */
class HeartbeatServiceTest {

    @Test
    void heartbeat_incrementaTickCount() {
        HeartbeatService service = new HeartbeatService();

        // Estado inicial: contador en cero.
        assertThat(service.getTickCount()).isZero();

        // Ejecutamos manualmente el método que Spring dispararía por fixedRate.
        service.heartbeat();
        service.heartbeat();

        assertThat(service.getTickCount()).isEqualTo(2);
    }

    @Test
    void cronTick_incrementaCronCount() {
        HeartbeatService service = new HeartbeatService();

        assertThat(service.getCronCount()).isZero();

        service.cronTick();
        service.cronTick();
        service.cronTick();

        assertThat(service.getCronCount()).isEqualTo(3);
    }

    @Test
    void contadores_sonIndependientes() {
        HeartbeatService service = new HeartbeatService();

        service.heartbeat();
        service.cronTick();
        service.cronTick();

        assertThat(service.getTickCount()).isEqualTo(1);
        assertThat(service.getCronCount()).isEqualTo(2);
    }
}
