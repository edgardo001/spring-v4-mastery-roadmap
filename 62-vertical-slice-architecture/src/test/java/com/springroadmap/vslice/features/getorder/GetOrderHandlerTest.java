package com.springroadmap.vslice.features.getorder;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.springroadmap.vslice.shared.OrderStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetOrderHandlerTest {

    @Test
    void devuelveOptionalConContenidoCuandoExiste() {
        OrderStore store = new OrderStore();
        OrderStore.StoredOrder saved = store.save("Ana", new BigDecimal("9.99"), "CREATED");

        GetOrderHandler handler = new GetOrderHandler(store);
        Optional<GetOrderResponse> resp = handler.handle(saved.id());

        assertTrue(resp.isPresent());
        assertEquals("Ana", resp.get().customer());
    }

    @Test
    void devuelveOptionalEmptyCuandoNoExiste() {
        GetOrderHandler handler = new GetOrderHandler(new OrderStore());

        Optional<GetOrderResponse> resp = handler.handle(999L);

        assertFalse(resp.isPresent());
    }
}
