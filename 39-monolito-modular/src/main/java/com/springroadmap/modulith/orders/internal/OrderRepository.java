package com.springroadmap.modulith.orders.internal;

import com.springroadmap.modulith.orders.Order;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Detalle interno del modulo orders.
 *
 * IMPORTANTE: convencion 'internal' = no debe ser usado desde otros modulos.
 * Spring Modulith (cuando publiquen version Boot 4) valida esto en tiempo
 * de test con ApplicationModules.verify(). Aqui lo hacemos por convencion.
 */
@Repository
public class OrderRepository {

    private final ConcurrentHashMap<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public Order save(String customer) {
        long id = sequence.incrementAndGet();
        Order order = new Order(id, customer);
        store.put(id, order);
        return order;
    }

    public int count() {
        return store.size();
    }
}
