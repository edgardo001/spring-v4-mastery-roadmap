package com.springroadmap.rsocket.controller;

import com.springroadmap.rsocket.domain.Greeting;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Bridge HTTP -&gt; RSocket. Expone {@code GET /api/hello?name=X} sobre HTTP,
 * y por dentro invoca el metodo RSocket {@code hello.request} en {@code localhost:7000}.
 *
 * <p>Sirve para demostrar que un frontend clasico (curl, navegador) puede hablar
 * con un backend RSocket usando esta capa de bridge.</p>
 */
@RestController
@RequestMapping("/api")
public class RestBridge {

    private final RSocketRequester rsocketRequester;

    public RestBridge(final RSocketRequester rsocketRequester) {
        this.rsocketRequester = rsocketRequester;
    }

    /**
     * Reenvia la llamada HTTP hacia la ruta RSocket "hello.request" y devuelve el resultado.
     * WebFlux serializa el {@link Mono} como JSON en la respuesta HTTP.
     */
    @GetMapping("/hello")
    public Mono<Greeting> hello(@RequestParam(defaultValue = "world") final String name) {
        return rsocketRequester
                .route("hello.request")
                .data(name)
                .retrieveMono(Greeting.class);
    }
}
