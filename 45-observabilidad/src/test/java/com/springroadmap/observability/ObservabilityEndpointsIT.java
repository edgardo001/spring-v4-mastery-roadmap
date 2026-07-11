package com.springroadmap.observability;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integracion HTTP reales usando {@link RestClient} contra Tomcat en puerto aleatorio.
 *
 * NOTA (MEMORY.md): Spring Boot 4.1.0 elimino {@code TestRestTemplate}.
 * Usamos {@code RestClient} + {@code @LocalServerPort}.
 *
 * ANTES (Boot 2.x/3.x):
 *   {@code @Autowired TestRestTemplate restTemplate;}
 * AHORA (Boot 4.1.0):
 *   {@code RestClient client = RestClient.builder().baseUrl("http://localhost:"+port).build();}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ObservabilityEndpointsIT {

    @LocalServerPort
    int port;

    private RestClient client() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void healthEndpointDevuelveUP() {
        ResponseEntity<String> resp = client().get()
                .uri("/actuator/health")
                .retrieve()
                .toEntity(String.class);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void prometheusEndpointExponeMetricasPorDefectoJvm() {
        ResponseEntity<String> resp = client().get()
                .uri("/actuator/prometheus")
                .retrieve()
                .toEntity(String.class);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        String contentType = resp.getHeaders().getFirst("Content-Type");
        assertThat(contentType).isNotNull().startsWith("text/plain");
        // Metrica por defecto expuesta por Micrometer + JVM binder.
        assertThat(resp.getBody()).contains("jvm_memory");
    }

    @Test
    void getOrderDevuelve200YCuerpoValido() {
        ResponseEntity<String> resp = client().get()
                .uri("/api/orders/1")
                .retrieve()
                .toEntity(String.class);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).contains("\"id\":1");
        assertThat(resp.getBody()).contains("\"status\":\"OK\"");
    }
}
