package com.springroadmap.k8s;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Modulo 48 - Test MockMvc en modo STANDALONE.
 *
 * "Standalone" = no levanta el ApplicationContext completo de Spring;
 * solo instancia el controlador y lo envuelve en un MockMvc. Es mucho
 * mas rapido que @WebMvcTest y suficiente para validar el mapping y
 * el cuerpo de la respuesta.
 */
class HelloControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Instanciamos el controlador a mano y lo pasamos al builder.
        this.mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();
    }

    @Test
    void helloEndpointReturnsExpectedText() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from K8s pod"));
    }
}
