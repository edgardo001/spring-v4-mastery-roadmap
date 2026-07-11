package com.springroadmap.ddd.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitarios PUROS del aggregate Order (sin Spring, sin BD).
 * Validan invariantes del dominio y las reglas de negocio del aggregate.
 */
class OrderTest {

    private Order sampleOrder() {
        OrderItem item = new OrderItem("Pizza", 2, new BigDecimal("10.00"), "USD");
        Money total = new Money(new BigDecimal("20.00"), "USD");
        return new Order(OrderId.newId(), "Juan", List.of(item), total);
    }

    @Test
    void createOrder_ok() {
        Order order = sampleOrder();
        assertEquals("Juan", order.getCustomer());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertTrue(order.getDomainEvents().isEmpty());
    }

    @Test
    void approve_changesStatusAndRegistersEvent() {
        Order order = sampleOrder();
        order.approve();
        assertEquals(OrderStatus.APPROVED, order.getStatus());
        assertFalse(order.getDomainEvents().isEmpty(), "debe registrar OrderApprovedEvent");
        assertTrue(order.getDomainEvents().get(0) instanceof OrderApprovedEvent);
    }

    @Test
    void approveTwice_throwsIllegalState() {
        Order order = sampleOrder();
        order.approve();
        assertThrows(IllegalStateException.class, order::approve);
    }

    @Test
    void createOrder_emptyItems_throws() {
        assertThrows(IllegalArgumentException.class, () ->
            new Order(OrderId.newId(), "Juan", List.of(),
                new Money(BigDecimal.ZERO, "USD")));
    }
}
