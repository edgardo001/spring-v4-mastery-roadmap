package com.springroadmap.rsocket.controller;

import com.springroadmap.rsocket.domain.Greeting;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion contra el servidor RSocket real levantado por {@code @SpringBootTest}.
 *
 * <p>Usa {@link RSocketRequester.Builder} (autoconfigurado por Spring Boot) para abrir
 * un cliente TCP contra {@code localhost:7000} y llamar cada uno de los tres modelos.</p>
 */
@SpringBootTest
class HelloControllerRSocketTest {

    @Autowired
    private RSocketRequester.Builder builder;

    @Value("${spring.rsocket.server.port}")
    private int port;

    private RSocketRequester requester() {
        return builder.tcp("localhost", port);
    }

    @Test
    void requestResponse_returnsGreeting() {
        final Mono<Greeting> result = requester()
                .route("hello.request")
                .data("Ada")
                .retrieveMono(Greeting.class);

        final Greeting greeting = result.block(Duration.ofSeconds(5));
        assertThat(greeting).isNotNull();
        assertThat(greeting.message()).isEqualTo("Hello, Ada");
    }

    @Test
    void stream_returnsThreeGreetings() {
        StepVerifier.create(
                requester()
                        .route("hello.stream")
                        .data("Ada")
                        .retrieveFlux(Greeting.class)
        )
                .expectNextMatches(g -> g.message().equals("#0 Ada"))
                .expectNextMatches(g -> g.message().equals("#1 Ada"))
                .expectNextMatches(g -> g.message().equals("#2 Ada"))
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void fireAndForget_completesWithoutResponse() {
        StepVerifier.create(
                requester()
                        .route("hello.fire")
                        .data("Ada")
                        .send()
        )
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }
}
