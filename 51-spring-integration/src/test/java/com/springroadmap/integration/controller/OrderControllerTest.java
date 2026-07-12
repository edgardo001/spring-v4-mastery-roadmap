package com.springroadmap.integration.controller;

import com.springroadmap.integration.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del OrderController usando MockMvc en modo STANDALONE.
 *
 * <p>IMPORTANTE (Boot 4.1.0):</p>
 * <ul>
 *   <li>NO se usa @WebMvcTest (eliminado en Boot 4).</li>
 *   <li>NO se usa @AutoConfigureMockMvc (eliminado en Boot 4).</li>
 *   <li>Patrón portable: MockMvcBuilders.standaloneSetup(controller).build().</li>
 *   <li>Las dependencias del controller se mockean con Mockito.</li>
 * </ul>
 */
class OrderControllerTest {

    private MockMvc mockMvc;
    private OrderGateway gateway;

    @BeforeEach
    void setUp() {
        // Mockito genera un doble del OrderGateway (no arranca Spring Integration).
        gateway = mock(OrderGateway.class);
        // Standalone setup: sin contexto de Spring, solo el controller y sus dependencias mockeadas.
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(gateway)).build();
    }

    @Test
    void shouldReturnGatewayResponseWhenPostingOrder() throws Exception {
        // Given: cualquier Order enviado al gateway devuelve este String.
        when(gateway.process(any())).thenReturn("OK - Procesando orden ORD-42");

        // Cuerpo JSON construido como String literal (evita depender de Jackson en test classpath).
        String json = """
                {"id":"ORD-42","product":"Mouse","quantity":5}
                """;

        // When + Then: POST al endpoint devuelve 200 y el body esperado.
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("OK - Procesando orden ORD-42"));
    }
}
