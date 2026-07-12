package com.springroadmap.reactive.web;

import com.springroadmap.reactive.domain.Item;
import com.springroadmap.reactive.repository.ItemRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler funcional para operaciones sobre Item.
 *
 * Cada metodo retorna Mono&lt;ServerResponse&gt;: WebFlux se encarga de
 * suscribirse a la cadena reactiva sin bloquear el event loop.
 */
@Component
public class ItemHandler {

    private final ItemRepository repository;

    public ItemHandler(final ItemRepository repository) {
        this.repository = repository;
    }

    /** GET /api/items -> devuelve un Flux&lt;Item&gt; serializado como JSON array. */
    public Mono<ServerResponse> findAll(final ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repository.findAll(), Item.class);
    }

    /** GET /api/items/{id} -> 200 con el Item o 404 si no existe. */
    public Mono<ServerResponse> findById(final ServerRequest request) {
        final Long id = Long.parseLong(request.pathVariable("id"));
        return repository.findById(id)
                .flatMap(item -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(item))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    /** POST /api/items -> guarda el body y devuelve el Item persistido con id. */
    public Mono<ServerResponse> create(final ServerRequest request) {
        return request.bodyToMono(Item.class)
                .flatMap(repository::save)
                .flatMap(saved -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }
}
