package com.springroadmap.springangular.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TaskControllerTest: tests MockMvc standalone (patron portable de MEMORY.md).
 *
 * En Spring Boot 4.1.0 NO existen @WebMvcTest ni @AutoConfigureMockMvc en modo
 * estable con el ecosistema Angular; por eso construimos MockMvc con
 * MockMvcBuilders.standaloneSetup(...) y anadimos un CorsFilter y un filtro
 * de Basic Auth minimal a mano.
 *
 * Tests:
 *   - GET /api/tasks -> 200 OK, con header Access-Control-Allow-Origin.
 *   - POST /api/tasks sin auth -> 401 Unauthorized.
 */
class TaskControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // CORS: permite el origen de Angular CLI (4200) en dev.
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOrigin("http://localhost:4200");
        cors.addAllowedMethod("*");
        cors.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cors);
        CorsFilter corsFilter = new CorsFilter(src);

        // Filtro que simula la regla "POST requiere auth" sin levantar Spring Security completo.
        // En standalone MockMvc no se activa la SecurityFilterChain automaticamente.
        var authFilter = new jakarta.servlet.Filter() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req,
                                 jakarta.servlet.ServletResponse res,
                                 jakarta.servlet.FilterChain chain)
                    throws java.io.IOException, jakarta.servlet.ServletException {
                var http = (jakarta.servlet.http.HttpServletRequest) req;
                var httpRes = (jakarta.servlet.http.HttpServletResponse) res;
                if ("POST".equalsIgnoreCase(http.getMethod())
                        && http.getHeader("Authorization") == null) {
                    httpRes.setStatus(401);
                    return;
                }
                chain.doFilter(req, res);
            }
        };

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new TaskController())
                .addFilters(corsFilter, authFilter)
                .build();
    }

    @Test
    void get_listaTareas_devuelve200YHeaderCors() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void post_sinAuth_devuelve401() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"nueva\",\"done\":false}"))
                .andExpect(status().isUnauthorized());
    }
}
