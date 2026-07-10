package com.springroadmap.exceptions.controller;

import com.springroadmap.exceptions.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del OrderController + GlobalExceptionHandler usando MockMvc en
 * modo "standalone".
 *
 * Recordatorio Spring Boot 4.1.0 (ver MEMORY.md):
 *   - NO existe @WebMvcTest en Boot 4.1.0.
 *   - Construimos MockMvc manualmente y REGISTRAMOS el @RestControllerAdvice
 *     con .setControllerAdvice(new GlobalExceptionHandler()) para que las
 *     excepciones lanzadas por el controller sean interceptadas y traducidas
 *     a los códigos HTTP esperados.
 *
 * Casos cubiertos:
 *   - id=1 (impar)  → 422 UNPROCESSABLE_ENTITY, code="BUSINESS_RULE".
 *   - id=0          → 404 NOT_FOUND, code="NOT_FOUND".
 *   - id=2 (par)    → 200 OK.
 */
class OrderControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                // Sin esta línea, las excepciones NO serían interceptadas y
                // MockMvc devolvería 500 sin llamar al handler.
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getOrder_conIdImpar_devuelve422ConCodeBusinessRule() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE"))
                .andExpect(jsonPath("$.path").value("/api/orders/1"));
    }

    @Test
    void getOrder_conIdCero_devuelve404ConCodeNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/orders/0"));
    }

    @Test
    void getOrder_conIdPar_devuelve200() throws Exception {
        mockMvc.perform(get("/api/orders/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("OK"));
    }
}
