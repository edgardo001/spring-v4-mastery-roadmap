package com.springroadmap.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GatewayApplicationTests — tests de integración del gateway.
 *
 * NOTA (MEMORY.md): en Boot 4.1.0 NO existen TestRestTemplate ni las
 * test-slices. Usamos RestClient + @LocalServerPort.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient restClient;

    /** Verifica que el contexto de Spring arranque correctamente. */
    @Test
    void contextLoads() {
        assertThat(restClient).isNotNull();
    }

    /**
     * Verifica que /gateway/health responde 200 con status=UP.
     * Este endpoint NO es un proxy (empieza por /gateway/).
     */
    @Test
    void healthEndpointRespondsOk() {
        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        @SuppressWarnings("unchecked")
        Map<String, String> body = client.get()
                .uri("/gateway/health")
                .retrieve()
                .body(Map.class);

        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo("UP");
        assertThat(body.get("service")).isEqualTo("spring-cloud-gateway");
    }
}
