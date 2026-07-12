package com.springroadmap.reactive.web;

import com.springroadmap.reactive.domain.Item;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integracion sobre el RouterFunction usando WebTestClient.
 *
 * NOTA: NO se usa TestRestTemplate (bloqueante). WebTestClient forma parte
 * de spring-test y se integra nativamente con el servidor Netty aleatorio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemRoutesTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        // Nota (MEMORY.md): @AutoConfigureWebTestClient no existe en Boot 4.1.0.
        // Creamos el cliente manualmente apuntando al puerto aleatorio.
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void getAllReturnsList() {
        webTestClient.get()
                .uri("/api/items")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .value(list -> assertThat(list).isNotNull());
    }

    @Test
    void postCreatesItemAndReturnsBody() {
        final Item nuevo = new Item(null, "Teclado", new BigDecimal("49.90"));

        webTestClient.post()
                .uri("/api/items")
                .bodyValue(nuevo)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .value(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getName()).isEqualTo("Teclado");
                    assertThat(saved.getPrice()).isEqualByComparingTo("49.90");
                });
    }

    @Test
    void getByIdReturns404WhenMissing() {
        webTestClient.get()
                .uri("/api/items/{id}", 9_999_999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void postThenGetAllContainsItem() {
        final Item creado = webTestClient.post()
                .uri("/api/items")
                .bodyValue(new Item(null, "Mouse", new BigDecimal("19.90")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .returnResult()
                .getResponseBody();

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isNotNull();

        webTestClient.get()
                .uri("/api/items")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .value((List<Item> list) -> assertThat(list)
                        .extracting(Item::getName)
                        .contains("Mouse"));
    }
}
