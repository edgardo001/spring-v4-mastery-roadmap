package com.springroadmap.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integracion GraphQL sin WebFlux.
 *
 * Nota (MEMORY.md): en Boot 4.1.0 las test-slices como @WebMvcTest y
 * @GraphQlTest no estan homologadas. Ademas HttpGraphQlTester requiere
 * WebClient (WebFlux). Usamos RestClient (Spring Framework 7) que ya viene
 * con spring-web y hacemos un POST manual con el query GraphQL.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerGraphQlTest {

    @LocalServerPort
    private int port;

    private RestClient client() {
        return RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void queryBooks_devuelveLosLibrosPrecargados() {
        String query = "{\"query\":\"{ books { title author } }\"}";

        String body = client().post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body(query)
                .retrieve()
                .body(String.class);

        assertThat(body).isNotNull();
        assertThat(body).contains("Clean Code");
        assertThat(body).contains("Effective Java");
    }

    @Test
    void mutationAddBook_persisteYRetornaEnQuery() {
        String mutation = "{\"query\":\"mutation { addBook(title: \\\"Test Book\\\", author: \\\"Test Author\\\") { id title } }\"}";
        client().post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body(mutation)
                .retrieve()
                .body(String.class);

        String body = client().post().uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"query\":\"{ books { title } }\"}")
                .retrieve()
                .body(String.class);

        assertThat(body).contains("Test Book");
    }
}
