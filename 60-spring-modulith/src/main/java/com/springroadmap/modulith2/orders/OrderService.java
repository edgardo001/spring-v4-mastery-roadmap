package com.springroadmap.modulith2.orders;

import com.springroadmap.modulith2.orders.internal.OrderRepository;
import com.springroadmap.modulith2.registry.RegisteredEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * API publica del modulo orders.
 *
 * Usa RegisteredEventPublisher (registry persistente) en vez del
 * ApplicationEventPublisher plano. Todo pasa dentro de UNA transaccion:
 * si algo falla, ni el aggregate ni la publicacion quedan a medias.
 */
@Service
public class OrderService {

    private final OrderRepository repository;
    private final RegisteredEventPublisher publisher;

    public OrderService(OrderRepository repository, RegisteredEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Transactional
    public Order createOrder(String customer) {
        Order saved = repository.save(customer);
        publisher.publish("Order", new OrderCreatedEvent(saved.id(), saved.customer()));
        return saved;
    }
}
