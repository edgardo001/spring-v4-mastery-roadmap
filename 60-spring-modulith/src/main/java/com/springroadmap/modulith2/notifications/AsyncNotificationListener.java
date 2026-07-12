package com.springroadmap.modulith2.notifications;

import com.springroadmap.modulith2.notifications.internal.NotificationHistory;
import com.springroadmap.modulith2.orders.OrderCreatedEvent;
import com.springroadmap.modulith2.registry.EventPublicationRegistry;
import com.springroadmap.modulith2.registry.RegisteredEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener asincrono transaccional. Simula @ApplicationModuleListener.
 *
 * Comportamiento:
 * - Espera al commit de la transaccion del publisher (AFTER_COMMIT).
 * - Se ejecuta en un pool async (no bloquea al publisher).
 * - Cuando termina, marca la publicacion como completada en el registry.
 *
 * Si este listener falla o el proceso se cae ANTES del markCompleted, el
 * registro queda con completed_at = null y puede ser reprocesado.
 */
@Component
public class AsyncNotificationListener {

    private final NotificationHistory history;
    private final EventPublicationRegistry registry;

    public AsyncNotificationListener(NotificationHistory history, EventPublicationRegistry registry) {
        this.history = history;
        this.registry = registry;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(RegisteredEvent wrapper) {
        if (!(wrapper.payload() instanceof OrderCreatedEvent event)) {
            return;
        }
        history.record("Notificado cliente " + event.customer() + " orden " + event.orderId());
        registry.markCompleted(wrapper.publicationId());
    }
}
