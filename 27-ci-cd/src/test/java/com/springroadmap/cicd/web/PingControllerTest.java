package com.springroadmap.cicd.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test standalone de MockMvc: no arranca el contexto completo,
 * es rápido y perfecto para la pipeline CI.
 *
 * ¿Por qué standalone en vez de @WebMvcTest?
 *   - Standalone es aún más liviano: sólo instancia el controlador.
 *   - Ideal para "smoke tests" que se ejecutan en cada push.
 */
class PingControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new PingController())
                .build();
    }

    @Test
    void ping_returns200AndPong() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }
}
