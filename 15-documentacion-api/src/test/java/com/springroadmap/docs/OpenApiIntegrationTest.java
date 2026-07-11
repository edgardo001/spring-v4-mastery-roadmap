package com.springroadmap.docs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion que arranca el servidor real y golpea los endpoints
 * de springdoc.
 *
 * NOTA (MEMORY.md 2026-07-10): en Boot 4.1.0 se elimino TestRestTemplate;
 * usamos RestClient (Spring Framework 7).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient client() {
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void apiDocsEndpointReturnsOpenApiSpec() {
        String body = client().get().uri("/v3/api-docs").retrieve().body(String.class);
        assertThat(body).isNotNull();
        assertThat(body).contains("openapi");
        assertThat(body).contains("Books API");
    }

    @Test
    void swaggerUiHtmlIsAvailable() {
        String body = client().get().uri("/swagger-ui/index.html").retrieve().body(String.class);
        assertThat(body).isNotNull();
    }
}
