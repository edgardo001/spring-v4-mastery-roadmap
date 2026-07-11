package com.springroadmap.vslice.features.listorders;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.springroadmap.vslice.shared.OrderStore;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListOrdersHandlerTest {

    @Test
    void filtraPorStatus() {
        OrderStore store = new OrderStore();
        store.save("Ana", new BigDecimal("1"), "CREATED");
        store.save("Beto", new BigDecimal("2"), "CREATED");
        store.save("Carla", new BigDecimal("3"), "SHIPPED");

        ListOrdersHandler handler = new ListOrdersHandler(store);

        List<OrderSummary> creadas = handler.handle("CREATED");
        List<OrderSummary> enviadas = handler.handle("SHIPPED");
        List<OrderSummary> todas = handler.handle(null);

        assertEquals(2, creadas.size());
        assertEquals(1, enviadas.size());
        assertEquals(3, todas.size());
    }
}
