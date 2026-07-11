package com.springroadmap.featureflags.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.featureflags.config.FeatureFlags;
import com.springroadmap.featureflags.service.FeatureFlagsService;

/**
 * Test MockMvc "standalone" del CheckoutController.
 *
 * ¿Por qué standalone y no @WebMvcTest?
 * En Spring Boot 4.1.0 se ELIMINARON @WebMvcTest y @AutoConfigureMockMvc
 * (verificado en MEMORY.md). El patrón portable es instanciar el controller a
 * mano con sus dependencias reales y pasarlo a MockMvcBuilders.standaloneSetup.
 */
class CheckoutControllerTest {

    private MockMvc mockMvc;
    private FeatureFlagsService service;

    @BeforeEach
    void setUp() {
        // Construimos manualmente el grafo de dependencias sin arrancar Spring.
        final FeatureFlags flags = new FeatureFlags(); // defaults: todo false
        this.service = new FeatureFlagsService(flags);
        final CheckoutController controller = new CheckoutController(service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void checkout_flagOff_returnsLegacy() throws Exception {
        mockMvc.perform(get("/api/checkout"))
                .andExpect(status().isOk())
                .andExpect(content().string("legacy checkout"));
    }

    @Test
    void checkout_afterTogglingBetaOn_returnsBeta() throws Exception {
        // 1) Toggle vía endpoint admin (simula el panel de flags).
        mockMvc.perform(post("/admin/flags/betaCheckout").param("enabled", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("flag 'betaCheckout' set to true"));

        // 2) Ahora la respuesta debe cambiar sin reiniciar la app.
        mockMvc.perform(get("/api/checkout"))
                .andExpect(status().isOk())
                .andExpect(content().string("beta checkout"));
    }
}
