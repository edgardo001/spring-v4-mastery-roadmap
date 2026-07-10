package com.springroadmap.validation.controller;

import com.springroadmap.validation.exception.ValidationExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del UserController usando MockMvc en modo standalone.
 *
 * Recordatorio Spring Boot 4.1.0 (usado en toda esta roadmap):
 *   NO usamos @WebMvcTest ni @AutoConfigureMockMvc; construimos MockMvc a mano.
 *
 * DETALLE CRÍTICO PARA ESTE MÓDULO:
 *   En modo standalone, Spring NO escanea @RestControllerAdvice. Es decir,
 *   por defecto NUESTRO ValidationExceptionHandler NO se aplica.
 *   Si no lo registramos, un body inválido se traduce en un 400 GENÉRICO
 *   (sin body), y los asserts sobre "name" o "email" en el body fallarían.
 *
 *   Solución: registrar el advice manualmente:
 *      MockMvcBuilders.standaloneSetup(controller)
 *                     .setControllerAdvice(new ValidationExceptionHandler())
 *                     .build();
 */
class UserControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserController controller = new UserController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                // <-- IMPRESCINDIBLE en standalone para capturar los errores de @Valid
                .setControllerAdvice(new ValidationExceptionHandler())
                .build();
    }

    /**
     * Camino feliz: body válido -> 201 Created.
     */
    @Test
    void create_conBodyValido_devuelve201() throws Exception {
        String body = """
                {
                  "name": "Ada Lovelace",
                  "email": "ada@example.com",
                  "age": 30,
                  "pin": "1234"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    /**
     * name vacío -> viola @NotBlank -> 400 y el body de error contiene "name".
     */
    @Test
    void create_conNameVacio_devuelve400ConCampoName() throws Exception {
        String body = """
                {
                  "name": "",
                  "email": "ada@example.com",
                  "age": 30,
                  "pin": "1234"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                // Además del jsonPath, comprobamos que la palabra "name" aparece
                // literalmente en el body serializado.
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name")))
                .andExpect(jsonPath("$.name").exists());
    }

    /**
     * email inválido -> viola @Email -> 400 y el body reporta el campo "email".
     */
    @Test
    void create_conEmailInvalido_devuelve400ConCampoEmail() throws Exception {
        String body = """
                {
                  "name": "Ada Lovelace",
                  "email": "no-es-un-email",
                  "age": 30,
                  "pin": "1234"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }
}
