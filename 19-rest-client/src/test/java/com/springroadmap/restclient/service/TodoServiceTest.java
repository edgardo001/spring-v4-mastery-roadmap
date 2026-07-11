package com.springroadmap.restclient.service;

import com.springroadmap.restclient.dto.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpMethod;

/**
 * Test unitario del TodoService usando MockRestServiceServer (spring-test).
 *
 * Antes vs Ahora:
 *  - Antes: se usaba MockRestServiceServer.createServer(restTemplate).
 *  - Ahora: RestClient tambien se integra con MockRestServiceServer via
 *    `bindTo(RestClient.Builder)`. Simulamos el back-end HTTP sin arrancar
 *    servidores reales ni WireMock.
 */
class TodoServiceTest {

    private RestClient restClient;
    private MockRestServiceServer mockServer;
    private TodoService service;

    @BeforeEach
    void setUp() {
        // Bind del servidor mock al builder ANTES de construir el RestClient.
        RestClient.Builder builder = RestClient.builder().baseUrl("https://jsonplaceholder.typicode.com");
        mockServer = MockRestServiceServer.bindTo(builder).build();
        restClient = builder.build();
        service = new TodoService(restClient);
    }

    @Test
    void fetch_devuelveTodoEsperado() {
        // GIVEN: la API externa respondera con este JSON para GET /todos/1
        String json = """
                { "id": 1, "title": "delectus aut autem", "completed": false }
                """;

        mockServer.expect(requestTo("https://jsonplaceholder.typicode.com/todos/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        // WHEN
        Todo result = service.fetch(1);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("delectus aut autem");
        assertThat(result.completed()).isFalse();

        mockServer.verify();
    }
}
