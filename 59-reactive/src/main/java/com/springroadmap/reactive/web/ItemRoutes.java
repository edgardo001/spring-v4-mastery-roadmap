package com.springroadmap.reactive.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Declaracion funcional de rutas (estilo alternativo a @RestController).
 *
 * Ventaja: las rutas viven en un solo lugar, se componen como funciones y
 * son mas testeables aisladamente.
 */
@Configuration
public class ItemRoutes {

    @Bean
    public RouterFunction<ServerResponse> itemRouterFunction(final ItemHandler handler) {
        return RouterFunctions.route()
                .GET("/api/items", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::findAll)
                .GET("/api/items/{id}", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::findById)
                .POST("/api/items", RequestPredicates.contentType(MediaType.APPLICATION_JSON), handler::create)
                .build();
    }
}
