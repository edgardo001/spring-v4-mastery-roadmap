package com.springroadmap.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integracion de seguridad.
 *
 * NOTA (MEMORY.md 2026-07-10): en Spring Boot 4.1.0 se elimino
 * `TestRestTemplate` del paquete `org.springframework.boot.test.web.client`.
 * En su lugar usamos RestClient (parte de Spring Framework 7, siempre disponible)
 * apuntando al puerto aleatorio del servidor real.
 *
 * Usamos @SpringBootTest(RANDOM_PORT) porque el standaloneSetup de MockMvc
 * NO aplica los filtros de Spring Security. Aqui necesitamos el flujo real.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient client() {
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    private static String basic(String user, String pass) {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
    }

    @Test
    void publicoAccesibleSinAutenticacion() {
        String body = client().get().uri("/api/public/hello").retrieve().body(String.class);
        assertThat(body).isEqualTo("public");
    }

    @Test
    void privadoSinAutenticacionRetorna401() {
        try {
            client().get().uri("/api/private/hello").retrieve().body(String.class);
            throw new AssertionError("Se esperaba 401");
        } catch (RestClientResponseException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }

    @Test
    void privadoConCredencialesValidasRetorna200() {
        String body = client().get().uri("/api/private/hello")
                .header(HttpHeaders.AUTHORIZATION, basic("admin", "admin123"))
                .retrieve().body(String.class);
        assertThat(body).contains("private");
    }

    @Test
    void privadoConCredencialesInvalidasRetorna401() {
        try {
            client().get().uri("/api/private/hello")
                    .header(HttpHeaders.AUTHORIZATION, basic("admin", "wrong"))
                    .retrieve().body(String.class);
            throw new AssertionError("Se esperaba 401");
        } catch (RestClientResponseException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
