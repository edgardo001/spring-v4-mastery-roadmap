package com.springroadmap.microservices.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del controller usando MockMvc en modo STANDALONE.
 *
 * IMPORTANTE (regla del roadmap - MEMORY.md):
 *   En Spring Boot 4.1.0 fueron ELIMINADOS @WebMvcTest, @AutoConfigureMockMvc y
 *   TestRestTemplate. El patron portable es:
 *     MockMvcBuilders.standaloneSetup(new ControllerXxx(deps)).build();
 *   Sin autoconfig, sin classpath scanning, super rapido y determinista.
 */
class ServiceDiscoveryControllerTest {

    private MockMvc mockMvc;
    private ServiceRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ServiceRegistry();
        ServiceDiscoveryController controller = new ServiceDiscoveryController(registry);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void postRegistersServiceAndReturnsOk() throws Exception {
        mockMvc.perform(post("/registry")
                .param("name", "pagos")
                .param("url", "http://localhost:8081"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.registered").value(true))
            .andExpect(jsonPath("$.service").value("pagos"));
    }

    @Test
    void getListsPreviouslyRegisteredServices() throws Exception {
        registry.register("inventario", "http://localhost:8082");

        mockMvc.perform(get("/registry"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.inventario[0]").value("http://localhost:8082"));
    }
}
