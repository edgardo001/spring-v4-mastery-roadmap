package com.springroadmap.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integracion HTTP reales usando {@link RestClient} contra un Tomcat en puerto aleatorio.
 *
 * NOTA IMPORTANTE (MEMORY.md): en Spring Boot 4.1.0 se elimino {@code TestRestTemplate}.
 * Usamos {@code RestClient} (parte de Spring Framework 7) + {@code @LocalServerPort}.
 *
 * ANTES (Boot 2.x/3.x):
 *   {@code @Autowired TestRestTemplate restTemplate;}
 * AHORA (Boot 4.1.0):
 *   {@code RestClient client = RestClient.builder().baseUrl("http://localhost:" + port).build();}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorEndpointsIT {

    // @LocalServerPort inyecta el puerto aleatorio elegido por Boot al arrancar Tomcat.
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
        // show-details=always agrega mas campos, pero el status:UP siempre esta presente.
        assertThat(resp.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void prometheusEndpointExponeMetricaOrdersCreated() {
        ResponseEntity<String> resp = client().get()
                .uri("/actuator/prometheus")
                .retrieve()
                .toEntity(String.class);

        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        // Prometheus usa text/plain con version en el content-type.
        String contentType = resp.getHeaders().getFirst("Content-Type");
        assertThat(contentType).isNotNull().startsWith("text/plain");
        // El nombre con puntos "app.orders.created" se convierte a "app_orders_created_total".
        assertThat(resp.getBody()).contains("app_orders_created_total");
    }

    @Test
    void postOrderIncrementaCounterYSeReflejaEnPrometheus() {
        // 1) Ejecutar POST /api/orders.
        ResponseEntity<String> post = client().post()
                .uri("/api/orders")
                .retrieve()
                .toEntity(String.class);

        assertThat(post.getStatusCode().value()).isEqualTo(200);
        assertThat(post.getBody()).contains("CREATED");

        // 2) Leer /actuator/prometheus y verificar que el counter subio a 1.0.
        String metrics = client().get()
                .uri("/actuator/prometheus")
                .retrieve()
                .body(String.class);

        assertThat(metrics).isNotNull();
        // Micrometer imprime doubles: "app_orders_created_total{application=\"actuator-demo\",} 1.0"
        assertThat(metrics).contains("app_orders_created_total");
        assertThat(metrics).containsPattern("app_orders_created_total\\{[^}]*\\}\\s+1\\.0");
    }
}
