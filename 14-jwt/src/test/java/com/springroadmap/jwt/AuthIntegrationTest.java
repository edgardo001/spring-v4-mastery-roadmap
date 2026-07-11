package com.springroadmap.jwt;

import com.springroadmap.jwt.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests de INTEGRACION del flujo JWT.
 *
 * NOTA (MEMORY.md 2026-07-10): en Spring Boot 4.1.0 se elimino
 * TestRestTemplate. Usamos RestClient (Spring Framework 7) directamente
 * contra el puerto aleatorio del servidor.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient client() {
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void login_withValidCredentials_returns200AndToken() {
        String body = client().post().uri("/api/auth/login")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(new LoginRequest("admin", "admin123"))
                .retrieve().body(String.class);
        assertNotNull(body);
        assertTrue(body.contains("token"), "body debe contener 'token'");
    }

    @Test
    void login_withInvalidCredentials_returns401() {
        try {
            client().post().uri("/api/auth/login")
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(new LoginRequest("admin", "wrong"))
                    .retrieve().body(String.class);
            fail("Se esperaba 401");
        } catch (RestClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    void me_withoutAuth_returns401Or403() {
        try {
            client().get().uri("/api/me").retrieve().body(String.class);
            fail("Se esperaba error de autenticacion");
        } catch (RestClientResponseException e) {
            // Spring Security en stateless devuelve 403 cuando no hay
            // AuthenticationEntryPoint custom, o 401 si lo hay. Ambos indican
            // "no autenticado" y son aceptables para el propósito del test.
            HttpStatus s = HttpStatus.valueOf(e.getStatusCode().value());
            assertTrue(s == HttpStatus.UNAUTHORIZED || s == HttpStatus.FORBIDDEN,
                    "Se esperaba 401 o 403, fue " + s);
        }
    }

    @Test
    void me_withValidBearer_returns200AndUsername() {
        // 1) Login para obtener token.
        TokenBody login = client().post().uri("/api/auth/login")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(new LoginRequest("admin", "admin123"))
                .retrieve().body(TokenBody.class);
        assertNotNull(login);
        String token = login.token();

        // 2) Llamar /api/me con el token.
        String me = client().get().uri("/api/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve().body(String.class);
        assertEquals("admin", me);
    }

    private record TokenBody(String token) {}
}
