package com.springroadmap.eventdriven.listener;

import com.springroadmap.eventdriven.event.PaymentSuccessEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener ASÍNCRONO que simula la generación de la factura.
 *
 * Nota: usa @Async sin nombre → toma el executor por defecto de Spring,
 * PERO como declaramos un bean llamado "eventExecutor" y no hay otro, Spring
 * seguirá usando el pool disponible (Spring Boot 4 respeta el TaskExecutor bean).
 *
 * En este caso, para dejar claro el patrón mixto que suele aparecer en proyectos
 * reales, dejamos @Async "pelón" (sin nombre) para demostrar que también funciona.
 */
@Component
public class InvoiceListener {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Async
    @EventListener
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        // Simulación: en producción aquí armarías el PDF/registro fiscal.
        counter.incrementAndGet();
    }

    public int count() {
        return counter.get();
    }
}
