package com.springroadmap.messaging.controller;

import com.springroadmap.messaging.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del controller usando el PATRON PORTABLE OBLIGATORIO del proyecto:
 *
 *   MockMvcBuilders.standaloneSetup(controller).build()
 *
 * ¿Por que este patron?
 *   - En Spring Boot 4.1.0 se ELIMINARON @WebMvcTest, @AutoConfigureMockMvc,
 *     TestRestTemplate y las demas "test slices". Ya no compilan.
 *   - standaloneSetup levanta un MockMvc SIN necesidad de arrancar el contexto,
 *     usando solo el controller (con sus dependencias mockeadas por Mockito).
 *   - Es rapido, portable y no depende del classpath.
 */
class OrderControllerTest {

    private OrderService orderService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 1. Mock del service. Mockito.mock() crea un doble de pruebas.
        orderService = mock(OrderService.class);

        // 2. Instanciamos el controller pasando el mock por constructor.
        OrderController controller = new OrderController(orderService);

        // 3. Construimos el MockMvc en modo standalone (sin contexto Spring).
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void postOrdersReturns201AndInvokesService() throws Exception {
        // Programamos el mock: cuando alguien llame createOrder(cualquier String)
        // debe devolver 42L.
        when(orderService.createOrder(anyString())).thenReturn(42L);

        mockMvc.perform(post("/api/orders").param("customer", "Ana"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(42))
                .andExpect(jsonPath("$.customer").value("Ana"))
                .andExpect(jsonPath("$.status").value("CREATED"));

        // Verificamos que el controller efectivamente llamo al service con "Ana".
        verify(orderService).createOrder("Ana");
    }

    @Test
    void postOrdersWithoutCustomerReturns400() throws Exception {
        // Sin @RequestParam obligatorio: Spring responde 400 Bad Request.
        mockMvc.perform(post("/api/orders"))
                .andExpect(status().isBadRequest());
    }
}
