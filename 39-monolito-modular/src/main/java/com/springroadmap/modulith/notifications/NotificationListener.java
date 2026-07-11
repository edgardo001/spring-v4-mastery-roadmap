package com.springroadmap.modulith.notifications;

import com.springroadmap.modulith.notifications.internal.NotificationHistory;
import com.springroadmap.modulith.orders.internal.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * API publica del modulo notifications.
 *
 * Antes: OrderService llamaba a NotificationService.send(...). Acoplamiento fuerte.
 * Ahora: notifications escucha eventos de forma pasiva. Se puede eliminar el
 * modulo entero y orders sigue funcionando.
 */
@Component
public class NotificationListener {

    private final NotificationHistory history;
    private final AtomicInteger notified = new AtomicInteger(0);

    public NotificationListener(NotificationHistory history) {
        this.history = history;
    }

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        history.record("Notificado cliente " + event.customer() + " orden " + event.orderId());
        notified.incrementAndGet();
    }

    public int getNotified() {
        return notified.get();
    }
}
