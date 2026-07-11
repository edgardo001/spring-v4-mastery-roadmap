package com.springroadmap.vslice.features.createorder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.vslice.shared.OrderStore;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc en modo STANDALONE (obligatorio en Spring Boot 4.1.0 porque
 * @WebMvcTest y @AutoConfigureMockMvc fueron eliminados).
 * Se construye el controlador a mano con sus dependencias.
 * JSON se envia como String literal para evitar el issue de jackson-databind.
 */
class CreateOrderEndpointTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        OrderStore store = new OrderStore();
        CreateOrderHandler handler = new CreateOrderHandler(store);
        CreateOrderEndpoint endpoint = new CreateOrderEndpoint(handler);
        mockMvc = MockMvcBuilders.standaloneSetup(endpoint).build();
    }

    @Test
    void postValidoDevuelve201ConLocation() throws Exception {
        String json = "{\"customer\":\"Ana\",\"amount\":10.50}";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/orders/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void postInvalidoDevuelve400() throws Exception {
        String json = "{\"customer\":\"\",\"amount\":10.50}";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
