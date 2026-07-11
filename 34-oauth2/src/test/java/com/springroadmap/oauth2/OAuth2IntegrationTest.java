package com.springroadmap.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Test de integración end-to-end:
 *   1) POST /api/auth/token retorna un JWT no vacío.
 *   2) GET /api/me sin token responde 401.
 *   3) GET /api/me con token válido responde 200 y el username en el body.
 *
 * Convenciones críticas (ver MEMORY.md):
 *   - NO usar `TestRestTemplate` (eliminado en Boot 4.1.0).
 *   - Usar `RestClient` (Spring Framework 7) + `@LocalServerPort`.
 *   - Import correcto de LocalServerPort: `org.springframework.boot.test.web.server.LocalServerPort`.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OAuth2IntegrationTest {

    // Spring inyecta el puerto real donde arrancó el servidor embebido.
    @LocalServerPort
    private int port;

    // Autowired opcional: solo lo declaramos para verificar que Spring inyecta beans.
    @Autowired
    @SuppressWarnings("unused")
    private com.springroadmap.oauth2.auth.TokenService tokenService;

    private RestClient client;

    @BeforeEach
    void setUp() {
        // baseUrl apunta al servidor recién arrancado.
        this.client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    /** POST /api/auth/token?username=alice -> devuelve access_token no vacío. */
    @Test
    void issuesTokenForUsername() {
        Map<String, String> body = client.post()
                .uri("/api/auth/token?username=alice")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {});

        assertThat(body).isNotNull();
        assertThat(body.get("token_type")).isEqualTo("Bearer");
        assertThat(body.get("access_token"))
                .isNotBlank()
                // Un JWT tiene tres segmentos separados por '.' (header.payload.signature).
                .matches("^[^.]+\\.[^.]+\\.[^.]+$");
    }

    /** GET /api/me sin token -> 401 Unauthorized. */
    @Test
    void meWithoutTokenReturns401() {
        assertThatThrownBy(() ->
                client.get()
                        .uri("/api/me")
                        .retrieve()
                        .toBodilessEntity()
        )
        .isInstanceOf(RestClientResponseException.class)
        .satisfies(ex -> {
            RestClientResponseException rex = (RestClientResponseException) ex;
            assertThat(rex.getStatusCode().value()).isEqualTo(401);
        });
    }

    /** GET /api/me con token válido -> 200 y username=alice en el body. */
    @Test
    void meWithValidTokenReturnsUsername() {
        // 1) Pedir un token para "alice".
        Map<String, String> tokenBody = client.post()
                .uri("/api/auth/token?username=alice")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, String>>() {});
        String token = tokenBody.get("access_token");

        // 2) Llamar /api/me con Authorization: Bearer <token>.
        Map<String, Object> me = client.get()
                .uri("/api/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        assertThat(me).isNotNull();
        assertThat(me.get("username")).isEqualTo("alice");
        assertThat(me.get("scope")).isEqualTo("read");
        assertThat(me.get("issuer")).isEqualTo("spring-roadmap-oauth2-demo");
    }
}
