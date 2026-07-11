package com.springroadmap.modulith.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test MockMvc STANDALONE (convencion del roadmap: sin @WebMvcTest, sin @SpringBootTest).
 * Service mockeado con Mockito manual (sin @MockBean).
 */
class OrderControllerTest {

    private MockMvc mockMvc;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService)).build();
    }

    @Test
    void postOrders_devuelveOrdenCreada() throws Exception {
        when(orderService.createOrder(eq("Luis"))).thenReturn(new Order(42L, "Luis"));

        mockMvc.perform(post("/api/orders").param("customer", "Luis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.customer").value("Luis"));
    }
}
