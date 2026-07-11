package com.springroadmap.vslice.features.createorder;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.springroadmap.vslice.shared.OrderStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test UNITARIO puro del handler (sin Spring, sin MockMvc).
 * Instanciamos OrderStore y CreateOrderHandler a mano con {@code new}.
 * Esto es posible porque no hay {@code @Autowired} en campos: todo va por
 * constructor.
 */
class CreateOrderHandlerTest {

    @Test
    void creaOrdenOk() {
        OrderStore store = new OrderStore();
        CreateOrderHandler handler = new CreateOrderHandler(store);

        CreateOrderResponse resp = handler.handle(new CreateOrderCommand("Ana", new BigDecimal("10.50")));

        assertNotNull(resp.id());
        assertEquals("CREATED", resp.status());
    }

    @Test
    void customerBlancoLanzaIllegalArgumentException() {
        OrderStore store = new OrderStore();
        CreateOrderHandler handler = new CreateOrderHandler(store);

        assertThrows(IllegalArgumentException.class,
                () -> handler.handle(new CreateOrderCommand("   ", new BigDecimal("5"))));
    }

    @Test
    void amountCeroLanzaIllegalArgumentException() {
        OrderStore store = new OrderStore();
        CreateOrderHandler handler = new CreateOrderHandler(store);

        assertThrows(IllegalArgumentException.class,
                () -> handler.handle(new CreateOrderCommand("Ana", BigDecimal.ZERO)));
    }
}
