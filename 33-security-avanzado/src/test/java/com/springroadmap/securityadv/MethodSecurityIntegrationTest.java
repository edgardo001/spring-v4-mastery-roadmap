package com.springroadmap.securityadv;

import com.springroadmap.securityadv.domain.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integracion del modulo 33 — Method Security.
 *
 * <p>NOTA (MEMORY.md 2026-07-10): en Spring Boot 4.1.0 se elimino
 * {@code TestRestTemplate}. Usamos {@link RestClient} sobre {@code @LocalServerPort}
 * (paquete {@code org.springframework.boot.test.web.server}) para probar el
 * flujo real de filtros + method security AOP.</p>
 *
 * <p>Cobertura:
 * <ul>
 *   <li>admin puede DELETE → 200.</li>
 *   <li>user NO puede DELETE → 403.</li>
 *   <li>user puede GET all (rol USER autorizado).</li>
 *   <li>user solicita doc propio (owner=user) → 200.</li>
 *   <li>user solicita doc ajeno (owner=admin) → 403 (object-level).</li>
 * </ul>
 * </p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MethodSecurityIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient client() {
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    /**
     * Construye la cabecera "Authorization: Basic base64(user:pass)".
     * <p>PREGUNTA DE ALUMNO — "¿por que Base64 y no encriptado?"</p>
     * <p>Basic Auth solo <i>codifica</i> (no cifra) las credenciales.
     * Por eso SIEMPRE debe ir sobre HTTPS en produccion.</p>
     */
    private static String basic(String user, String pass) {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
    }

    @Test
    void adminPuedeBorrar() {
        // Doc id=2 (owner=user) → admin puede borrarlo por su rol ADMIN.
        var response = client().delete().uri("/api/docs/2")
                .header(HttpHeaders.AUTHORIZATION, basic("admin", "admin123"))
                .retrieve().toBodilessEntity();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void userNoPuedeBorrar() {
        try {
            client().delete().uri("/api/docs/1")
                    .header(HttpHeaders.AUTHORIZATION, basic("user", "user123"))
                    .retrieve().toBodilessEntity();
            throw new AssertionError("Se esperaba 403");
        } catch (RestClientResponseException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }

    @Test
    void userPuedeListarPorRol() {
        List<Document> body = client().get().uri("/api/docs")
                .header(HttpHeaders.AUTHORIZATION, basic("user", "user123"))
                .retrieve()
                // ParameterizedTypeReference conserva el tipo generico List<Document>.
                .body(new ParameterizedTypeReference<List<Document>>() {});
        assertThat(body).isNotNull();
        assertThat(body).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void userPuedeVerDocPropio() {
        // Doc id=2 pertenece a "user" → @PostAuthorize permite.
        Document doc = client().get().uri("/api/docs/2")
                .header(HttpHeaders.AUTHORIZATION, basic("user", "user123"))
                .retrieve().body(Document.class);
        assertThat(doc).isNotNull();
        assertThat(doc.owner()).isEqualTo("user");
    }

    @Test
    void userNoPuedeVerDocAjeno() {
        try {
            // Doc id=1 pertenece a "admin" → @PostAuthorize bloquea a "user".
            client().get().uri("/api/docs/1")
                    .header(HttpHeaders.AUTHORIZATION, basic("user", "user123"))
                    .retrieve().body(Document.class);
            throw new AssertionError("Se esperaba 403");
        } catch (RestClientResponseException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }
    }
}
