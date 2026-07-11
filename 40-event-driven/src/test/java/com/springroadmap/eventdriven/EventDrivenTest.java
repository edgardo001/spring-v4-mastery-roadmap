package com.springroadmap.eventdriven;

import com.springroadmap.eventdriven.listener.AnalyticsListener;
import com.springroadmap.eventdriven.listener.EmailListener;
import com.springroadmap.eventdriven.listener.InvoiceListener;
import com.springroadmap.eventdriven.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba el comportamiento sync vs async de los listeners.
 *
 * Flujo esperado:
 *   1) processPayment() publica el evento.
 *   2) AnalyticsListener (SÍNCRONO) se incrementa ANTES de que retorne processPayment.
 *   3) Email/Invoice (ASÍNCRONOS) se incrementan poco después en hilos del pool.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué Thread.sleep(500) en el test?"
 *   Porque los listeners async corren en otros hilos. Para chequearlos, hay que
 *   darles tiempo. En producción se prefiere Awaitility (ver README).
 */
@SpringBootTest
class EventDrivenTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EmailListener emailListener;

    @Autowired
    private InvoiceListener invoiceListener;

    @Autowired
    private AnalyticsListener analyticsListener;

    @Test
    void publishesEventAndAllListenersReact() throws InterruptedException {
        // Cuando: procesamos un pago.
        paymentService.processPayment(new BigDecimal("100"));

        // Entonces (INMEDIATO): el listener síncrono ya reaccionó.
        assertThat(analyticsListener.count()).isEqualTo(1);

        // Esperamos que los async terminen (pool tiene hilos suficientes).
        Thread.sleep(500);

        assertThat(emailListener.count()).isEqualTo(1);
        assertThat(invoiceListener.count()).isEqualTo(1);
    }
}
