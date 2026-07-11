package com.springroadmap.eventdriven.listener;

import com.springroadmap.eventdriven.event.PaymentSuccessEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener ASÍNCRONO que simula el envío de email de confirmación.
 *
 * @Async("eventExecutor")  → corre en el pool "eventExecutor" (no bloquea al publicador).
 * @EventListener           → Spring invoca este método cuando se publique el evento.
 *
 * Analogía: la secretaria de emails. Al oír "PAGO OK", redacta el correo en su
 * escritorio SIN detener al cajero.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué usar AtomicInteger y no un int normal?"
 *   Porque varios hilos pueden llegar al listener a la vez. Un `int++` NO es
 *   atómico (lee, suma, escribe) y podría perder actualizaciones. `AtomicInteger`
 *   garantiza la operación con hardware.
 */
@Component
public class EmailListener {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Async("eventExecutor")
    @EventListener(PaymentSuccessEvent.class)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        // Simulación de "enviar email" (no bloquea de verdad, solo cuenta).
        counter.incrementAndGet();
    }

    /** Getter del contador para inspección en los tests. */
    public int count() {
        return counter.get();
    }
}
