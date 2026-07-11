package com.springroadmap.eventdriven.controller;

import com.springroadmap.eventdriven.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del controlador con MockMvc en modo standalone.
 *
 * En Spring Boot 4.1.0 se ELIMINARON @WebMvcTest y @AutoConfigureMockMvc.
 * Patrón portable OBLIGATORIO: MockMvcBuilders.standaloneSetup(controller).build().
 *
 * Aquí construimos manualmente el servicio con un ApplicationEventPublisher mock
 * porque en standalone no hay contexto Spring completo.
 */
class PaymentControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Publisher falso: en este test no verificamos listeners, solo el controller.
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        PaymentService service = new PaymentService(publisher);
        PaymentController controller = new PaymentController(service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void postPaymentReturnsIdAndAmount() throws Exception {
        mockMvc.perform(post("/api/payments").param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100));
    }
}
