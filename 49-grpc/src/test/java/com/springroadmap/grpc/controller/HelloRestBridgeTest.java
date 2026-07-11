package com.springroadmap.grpc.controller;

import com.springroadmap.grpc.grpc.HelloGrpcServer;
import com.springroadmap.grpc.service.HelloServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test MockMvc standalone del REST bridge.
 *
 * <p>Recordatorio: en Spring Boot 4.1.0 NO existen &#64;WebMvcTest ni
 * &#64;AutoConfigureMockMvc (ver MEMORY.md). Por eso usamos el patron portable
 * {@link MockMvcBuilders#standaloneSetup(Object...)}.
 */
class HelloRestBridgeTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Construimos manualmente el grafo de dependencias: sin Spring, sin Mockito.
        final HelloGrpcServer grpc = new HelloGrpcServer(new HelloServiceImpl(), 9090);
        final HelloRestBridge bridge = new HelloRestBridge(grpc);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bridge).build();
    }

    @Test
    void getHelloReturnsGreetingJson() throws Exception {
        mockMvc.perform(get("/api/hello").param("name", "Ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hola, Ana! (via gRPC-demo)"));
    }

    @Test
    void getHelloUsesDefaultWhenNameMissing() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hola, mundo! (via gRPC-demo)"));
    }
}
