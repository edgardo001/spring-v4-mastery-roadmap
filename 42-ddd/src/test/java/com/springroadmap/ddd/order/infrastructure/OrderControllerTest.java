package com.springroadmap.ddd.order.infrastructure;

import com.springroadmap.ddd.order.application.OrderService;
import com.springroadmap.ddd.order.domain.Money;
import com.springroadmap.ddd.order.domain.Order;
import com.springroadmap.ddd.order.domain.OrderId;
import com.springroadmap.ddd.order.domain.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del OrderController con MockMvc STANDALONE (patron obligatorio del roadmap).
 * En Spring Boot 4.1.0 NO existen @WebMvcTest ni @AutoConfigureMockMvc; standalone es portable.
 */
class OrderControllerTest {

    private OrderService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(OrderService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(service)).build();
    }

    private Order buildOrder() {
        OrderItem item = new OrderItem("Pizza", 2, new BigDecimal("10.00"), "USD");
        return new Order(new OrderId("abc-123"), "Juan", List.of(item),
            new Money(new BigDecimal("20.00"), "USD"));
    }

    @Test
    void post_createOrder_returns200() throws Exception {
        when(service.create(anyString(), any())).thenReturn(buildOrder());

        String body = """
            {
              "customer": "Juan",
              "items": [ { "productName":"Pizza", "quantity":2, "unitPrice":10.00, "currency":"USD" } ]
            }
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("abc-123"))
            .andExpect(jsonPath("$.customer").value("Juan"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void put_approve_returns200() throws Exception {
        Order order = buildOrder();
        order.approve();
        when(service.approve(any(OrderId.class))).thenReturn(order);

        mockMvc.perform(put("/api/orders/abc-123/approve"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
