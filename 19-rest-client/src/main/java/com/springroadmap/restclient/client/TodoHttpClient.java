package com.springroadmap.restclient.client;

import com.springroadmap.restclient.dto.Todo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

/**
 * Cliente HTTP declarativo (interface-based) al estilo Feign, pero nativo de Spring 6+.
 *
 * Antes vs Ahora:
 *  - Antes: se usaba `spring-cloud-openfeign` con @FeignClient para tener clientes
 *    declarativos. Requeria dependencia adicional y arranque de Spring Cloud.
 *  - Ahora: @HttpExchange / @GetExchange viene con Spring Framework 6. Sin
 *    dependencias extra. Solo se necesita un HttpServiceProxyFactory sobre un
 *    RestClient (o WebClient) como adaptador.
 */
public interface TodoHttpClient {

    /**
     * GET /todos/{id} en la API externa.
     * El binding de {id} lo hace la anotacion @PathVariable de Spring Web.
     */
    @GetExchange("/todos/{id}")
    Todo getById(@PathVariable long id);
}
