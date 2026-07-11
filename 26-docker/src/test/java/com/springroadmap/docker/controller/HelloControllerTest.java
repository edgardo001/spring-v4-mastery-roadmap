package com.springroadmap.docker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// static imports: nos permiten escribir get(...) en vez de
// MockMvcRequestBuilders.get(...). Es azucar sintactico estandar
// en tests de Spring MVC.
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del HelloController usando MockMvc en modo "standalone".
 *
 * NOTA CRITICA (registrada en MEMORY.md - 2026-07-10):
 *   En Spring Boot 4.1.0 se ELIMINARON las anotaciones @WebMvcTest y
 *   @AutoConfigureMockMvc. El patron portable para todos los modulos
 *   del roadmap es MockMvcBuilders.standaloneSetup(new Controller()).build().
 *
 * Ventajas del enfoque standalone:
 *   - Ultra-rapido (no arranca Spring Boot completo).
 *   - Portable entre Spring Boot 3.x y 4.x.
 *   - No depende de anotaciones removidas.
 *
 * ============================================================
 * ANTES (JUnit 4 + Spring 4) vs AHORA (JUnit 5 + Spring Boot 4)
 * ============================================================
 * ANTES:
 *   @RunWith(SpringRunner.class)
 *   @WebMvcTest(HelloController.class)
 *   public class HelloControllerTest {
 *       @Autowired private MockMvc mockMvc;
 *       @Test public void test() throws Exception { ... }
 *   }
 *
 * AHORA:
 *   class HelloControllerTest {
 *       private MockMvc mockMvc;
 *       @BeforeEach void setup() {
 *           mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();
 *       }
 *       @Test void test() throws Exception { ... }
 *   }
 */
class HelloControllerTest {

    private MockMvc mockMvc;

    /**
     * @BeforeEach: JUnit 5 lo ejecuta ANTES de cada @Test. Construimos
     * un MockMvc fresco para asegurar aislamiento entre tests.
     */
    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();
    }

    /**
     * Caso feliz: GET /api/hello devuelve 200 OK y el cuerpo contiene
     * el texto "Docker container".
     *
     * containsString(...) evita romper el test si en el futuro se
     * anade algo antes o despues del texto principal (por ejemplo un
     * signo de exclamacion o el nombre del host).
     */
    @Test
    void getHello_debeRetornar200ConTextoDockerContainer() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Docker container")));
    }
}
