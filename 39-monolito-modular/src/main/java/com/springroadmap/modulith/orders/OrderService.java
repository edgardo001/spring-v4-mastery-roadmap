package com.springroadmap.modulith.orders;

import com.springroadmap.modulith.orders.internal.OrderCreatedEvent;
import com.springroadmap.modulith.orders.internal.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * API publica del modulo orders.
 *
 * Antes: OrderService llamaba directamente a NotificationService (acoplamiento).
 * Ahora: publica un evento; el modulo de notifications se suscribe si quiere.
 * Permite eliminar/agregar consumidores sin tocar orders.
 */
@Service
public class OrderService {

    private final OrderRepository repository;
    private final ApplicationEventPublisher publisher;

    public OrderService(OrderRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Order createOrder(String customer) {
        Order saved = repository.save(customer);
        publisher.publishEvent(new OrderCreatedEvent(saved.id(), saved.customer()));
        return saved;
    }
}
