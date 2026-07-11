package com.springroadmap.hexagonal.adapter.in.web;

import com.springroadmap.hexagonal.domain.model.Customer;
import com.springroadmap.hexagonal.domain.port.in.RegisterCustomerUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del CustomerController con MockMvc STANDALONE.
 *
 * IMPORTANTE (MEMORY.md 2026-07-10): en Spring Boot 4.1.0 fueron ELIMINADAS
 * las test-slices como @WebMvcTest y @AutoConfigureMockMvc. El patrón portable
 * es 'MockMvcBuilders.standaloneSetup(new Controller(deps)).build()'.
 *
 * El mock del use case es una implementación manual (POJO) de la interfaz.
 * No necesitamos Mockito para algo tan simple.
 */
class CustomerControllerTest {

    private MockMvc mockMvc;
    private AtomicReference<String> capturedName;
    private AtomicReference<String> capturedEmail;

    @BeforeEach
    void setUp() {
        capturedName = new AtomicReference<>();
        capturedEmail = new AtomicReference<>();

        // Mock manual del use case. Captura los argumentos y devuelve un Customer fijo.
        RegisterCustomerUseCase mockUseCase = new RegisterCustomerUseCase() {
            @Override
            public Customer register(String name, String email) {
                capturedName.set(name);
                capturedEmail.set(email);
                return new Customer(99L, name, email);
            }
        };

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CustomerController(mockUseCase))
                .build();
    }

    @Test
    void POST_api_customers_debe_registrar_y_devolver_201_con_el_customer() throws Exception {
        // Enviamos JSON literal como String (sin ObjectMapper) — patrón MEMORY.md.
        String body = "{\"name\":\"Juan\",\"email\":\"juan@x.com\"}";

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.name").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@x.com"));

        // Verificamos que el controller pasó bien los argumentos al puerto de entrada.
        assertNotNull(capturedName.get());
        assertEquals("Juan", capturedName.get());
        assertEquals("juan@x.com", capturedEmail.get());
    }
}
