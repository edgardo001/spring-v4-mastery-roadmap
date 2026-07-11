package com.springroadmap.eventdriven.listener;

import com.springroadmap.eventdriven.event.PaymentSuccessEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Listener SÍNCRONO — corre en el MISMO hilo que publica el evento.
 *
 * Como NO tiene @Async, `publisher.publishEvent(...)` no retorna hasta que
 * este método termine. Sirve para comparar el comportamiento vs los @Async.
 *
 * Analogía: el analista de datos que está sentado JUNTO al cajero. Cuando el
 * cajero grita "PAGO OK", el analista lo apunta en su libreta ANTES de que
 * el cajero atienda al siguiente cliente.
 *
 * Casos de uso empresariales:
 *   - Actualizar caché en memoria dentro de la misma transacción.
 *   - Validaciones que deben terminar antes de responder al usuario.
 */
@Component
public class AnalyticsListener {

    private final AtomicInteger counter = new AtomicInteger(0);

    @EventListener
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        counter.incrementAndGet();
    }

    public int count() {
        return counter.get();
    }
}
