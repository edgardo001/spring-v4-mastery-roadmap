package com.springroadmap.eventdriven.service;

import com.springroadmap.eventdriven.event.PaymentSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servicio de pagos.
 *
 * Analogía:
 *   Es el cajero. Cobra el pago y grita "¡PAGO OK!" al altavoz de la oficina.
 *   No sabe (ni le importa) quién escucha: email, facturas, analítica, etc.
 *
 * @Service → marca la clase como componente de la capa de servicio (lógica de negocio).
 *   Spring la instancia una vez y la inyecta donde se pida.
 *
 * ANTES vs AHORA:
 *   ANTES: EmailService, InvoiceService y AnalyticsService eran llamados
 *          en secuencia dentro de este mismo método → alto acoplamiento.
 *   AHORA: Este servicio publica UN evento. Los listeners son independientes.
 */
@Service
public class PaymentService {

    /** Contador simple para simular la generación de IDs de pago. */
    private final AtomicLong idGenerator = new AtomicLong(0);

    /** El publicador de eventos que Spring nos inyecta. */
    private final ApplicationEventPublisher publisher;

    // Constructor injection: forma recomendada. Inmutable y testeable sin Spring.
    // 'final' garantiza que el campo se asigna una sola vez.
    public PaymentService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Procesa un pago y publica el evento.
     *
     * @param amount monto del pago (BigDecimal evita errores de precisión con float/double).
     * @return el ID generado del pago.
     */
    public Long processPayment(BigDecimal amount) {
        // Simulamos persistir el pago y generar un ID único.
        Long paymentId = idGenerator.incrementAndGet();

        // Aquí iría el guardado en BD (fuera del alcance del módulo 40).

        // Publicamos el evento. Cualquier @EventListener suscrito se disparará.
        // Los @Async correrán en hilos del pool "eventExecutor";
        // los síncronos correrán en ESTE mismo hilo antes de retornar.
        publisher.publishEvent(new PaymentSuccessEvent(paymentId, amount));

        return paymentId;
    }
}
