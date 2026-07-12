package com.springroadmap.modulith2.orders.internal;

import com.springroadmap.modulith2.orders.Order;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Detalle interno del modulo orders. Convencion 'internal' = no accesible
 * desde otros modulos (validado por Modulith real con ApplicationModules.verify()).
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
