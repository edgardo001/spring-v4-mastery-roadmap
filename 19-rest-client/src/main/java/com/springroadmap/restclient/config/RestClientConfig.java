package com.springroadmap.restclient.config;

import com.springroadmap.restclient.client.TodoHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuracion de clientes HTTP.
 *
 * Registramos dos beans:
 *  1) `RestClient`: cliente fluido usado directamente por TodoService.
 *  2) `TodoHttpClient`: proxy generado a partir de una interfaz con @HttpExchange,
 *     al estilo Feign, pero nativo de Spring.
 *
 * Antes vs Ahora:
 *  - Antes (RestTemplate):
 *      @Bean RestTemplate rt() { return new RestTemplate(); }
 *      rt.getForObject(url, Todo.class);   // API rigida, sin base URL comoda
 *  - Ahora (RestClient + @HttpExchange):
 *      RestClient.builder().baseUrl(...).build();
 *      restClient.get().uri("/todos/{id}", id).retrieve().body(Todo.class);
 *      // O declarativo:
 *      interface TodoHttpClient { @GetExchange("/todos/{id}") Todo getById(...); }
 */
@Configuration
public class RestClientConfig {

    private final String baseUrl;

    public RestClientConfig(@Value("${external.api.url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Bean RestClient con base URL configurable via `external.api.url`.
     *
     * NOTA (MEMORY.md): en Spring Boot 4.1.0 la autoconfiguración de
     * `RestClient.Builder` NO viene por defecto — hay que construirlo con
     * `RestClient.builder()` directamente.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Cliente declarativo: envuelve el RestClient con un HttpServiceProxyFactory
     * y genera dinamicamente la implementacion de TodoHttpClient.
     *
     * Ventaja: no escribimos codigo de invocacion; solo declaramos el contrato.
     */
    @Bean
    public TodoHttpClient todoHttpClient(RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(TodoHttpClient.class);
    }
}
