package com.springroadmap.integration;

import com.springroadmap.integration.domain.Order;
import com.springroadmap.integration.gateway.OrderGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración del flujo completo: envía un Order al gateway y valida la respuesta.
 *
 * <p>Verifica que:</p>
 * <ol>
 *   <li>El @MessagingGateway crea el Message y lo envía a `orderInput`.</li>
 *   <li>El transformer convierte Order en String.</li>
 *   <li>El handler produce la respuesta final.</li>
 *   <li>El String regresa al gateway y es retornado.</li>
 * </ol>
 */
@SpringBootTest
class IntegrationFlowTest {

    // Inyección por campo SOLO en tests (aquí es aceptable). En producción: constructor injection.
    @Autowired
    private OrderGateway gateway;

    @Test
    void shouldProcessOrderThroughIntegrationFlow() {
        // Given: una orden de prueba.
        Order order = new Order("ORD-001", "Notebook", 3);

        // When: se envía al gateway (dispara el flujo completo).
        String result = gateway.process(order);

        // Then: la respuesta contiene los datos procesados por transformer + handler.
        assertThat(result).isNotNull();
        assertThat(result).startsWith("OK - Procesando orden ORD-001");
        assertThat(result).contains("Notebook");
        assertThat(result).contains("cantidad=3");
    }
}
