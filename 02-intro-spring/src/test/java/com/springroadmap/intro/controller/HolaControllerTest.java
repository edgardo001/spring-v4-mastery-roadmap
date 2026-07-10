package com.springroadmap.intro.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// "static imports" para escribir get(...) en vez de MockMvcRequestBuilders.get(...).
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del HolaController usando MockMvc en modo "standalone".
 *
 * NOTA IMPORTANTE (registrada en MEMORY.md — 2026-07-10):
 *   En Spring Boot 4.1.0 se ELIMINARON las anotaciones @WebMvcTest y
 *   @AutoConfigureMockMvc (no están en ningún JAR de Boot 4.1.0 verificado).
 *
 *   El patrón portable que usaremos en TODOS los módulos del roadmap con
 *   tests de controllers es:
 *     MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new HolaController()).build();
 *   Esto construye un MockMvc "aislado" que apunta directamente al controller,
 *   sin necesidad de arrancar el contexto Spring. Ventajas:
 *     - Ultrarrápido (no carga configuración ni escanea beans).
 *     - Portable entre Boot 3.x y Boot 4.x.
 *     - No depende de anotaciones que puedan cambiar entre versiones.
 *
 * ============================================================
 * ANTES (JUnit 4 + Spring 4 / Java 8) vs AHORA (JUnit 5 + Spring Boot 4)
 * ============================================================
 * ANTES:
 *   @RunWith(SpringRunner.class)
 *   @WebMvcTest(HolaController.class)
 *   public class HolaControllerTest {
 *       @Autowired private MockMvc mockMvc;
 *       @Test public void test() throws Exception { ... }
 *   }
 *
 * AHORA (JUnit 5 + patrón standalone):
 *   class HolaControllerTest {
 *       private MockMvc mockMvc;
 *       @BeforeEach void setup() {
 *           mockMvc = MockMvcBuilders.standaloneSetup(new HolaController()).build();
 *       }
 *       @Test void test() throws Exception { ... }
 *   }
 */
class HolaControllerTest {

    private MockMvc mockMvc;

    /**
     * @BeforeEach: JUnit 5 ejecuta este método ANTES de cada @Test.
     * Aquí construimos un MockMvc fresco por test para evitar interferencias.
     *
     * standaloneSetup(controller) crea un MockMvc que solo conoce al
     * controller que le pasamos. Perfecto para tests unitarios de web.
     */
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HolaController()).build();
    }

    /**
     * Caso feliz: GET /api/hola devuelve 200 OK y contiene "Hola Mundo".
     * containsString(...) evita romper el test si cambia el signo inicial.
     */
    @Test
    void getHola_debeRetornarOkYMensajeHolaMundo() throws Exception {
        mockMvc.perform(get("/api/hola"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hola Mundo")));
    }

    /**
     * Caso feliz alternativo: GET /api/hora devuelve 200 OK
     * y contiene "Hora del servidor:".
     */
    @Test
    void getHora_debeRetornarOkConTextoDeHora() throws Exception {
        mockMvc.perform(get("/api/hora"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hora del servidor:")));
    }

    /**
     * Edge case: ruta inexistente → 404 Not Found.
     * En modo standalone, MockMvc devuelve 404 al no encontrar mapping.
     */
    @Test
    void getRutaInexistente_debeRetornar404() throws Exception {
        mockMvc.perform(get("/api/no-existe"))
                .andExpect(status().isNotFound());
    }

    /**
     * Edge case: POST /api/hola → 405 Method Not Allowed.
     * @GetMapping solo permite GET, así que POST debe ser rechazado.
     */
    @Test
    void postHola_debeRetornar405() throws Exception {
        mockMvc.perform(post("/api/hola"))
                .andExpect(status().isMethodNotAllowed());
    }
}
