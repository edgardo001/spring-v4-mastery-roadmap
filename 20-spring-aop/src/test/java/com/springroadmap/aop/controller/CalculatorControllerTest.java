package com.springroadmap.aop.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.springroadmap.aop.service.CalculatorService;

/**
 * CalculatorControllerTest — MockMvc en modo standalone.
 *
 * <p><b>Nota importante:</b> {@code standaloneSetup} NO levanta el contexto Spring
 * completo. En consecuencia, los {@code @Aspect} NO se aplican en este test. Aquí
 * sólo verificamos el <b>routing</b> HTTP (método + URL + status + body). Para
 * validar el aspecto, ver {@link com.springroadmap.aop.aspect.LoggingAspectTest}.
 */
class CalculatorControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Instanciamos el service directamente (sin Spring, sin AOP).
        CalculatorService service = new CalculatorService();
        // standaloneSetup registra sólo el controller que le pasamos.
        mockMvc = standaloneSetup(new CalculatorController(service)).build();
    }

    @Test
    void getAdd_returns200AndSum() throws Exception {
        mockMvc.perform(get("/api/calc/add").param("a", "2").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getSub_returns200AndDifference() throws Exception {
        mockMvc.perform(get("/api/calc/sub").param("a", "10").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(content().string("6"));
    }
}
