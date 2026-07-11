package com.springroadmap.restclient.service;

import com.springroadmap.restclient.dto.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Servicio de integracion que consume la API externa de Todos usando RestClient
 * directamente (no la interfaz declarativa) para ilustrar la API fluida.
 *
 * Incluye retry manual simple para tolerar fallos transitorios de red.
 *
 * Antes vs Ahora:
 *  - Antes: `restTemplate.getForObject(url, Todo.class)` -- API rigida, sin
 *    fluidez, y difícil de personalizar por request.
 *  - Ahora: `restClient.get().uri(...).retrieve().body(Todo.class)` -- fluida,
 *    permite encadenar onStatus(), headers, etc.
 */
@Service
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);
    private static final int MAX_ATTEMPTS = 3;

    private final RestClient restClient;

    public TodoService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Recupera un Todo por id con retry manual (hasta 3 intentos).
     *
     * NOTA: en produccion se recomienda Resilience4j (modulo 30) para retries,
     * pero aqui se muestra el patron basico.
     */
    public Todo fetch(long id) {
        RestClientException last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                log.debug("Fetching todo id={} (attempt {}/{})", id, attempt, MAX_ATTEMPTS);
                return restClient.get()
                        .uri("/todos/{id}", id)
                        .retrieve()
                        .body(Todo.class);
            } catch (RestClientException ex) {
                last = ex;
                log.warn("Attempt {} failed for todo id={}: {}", attempt, id, ex.getMessage());
            }
        }
        throw new IllegalStateException("No se pudo obtener el Todo id=" + id + " tras " + MAX_ATTEMPTS + " intentos", last);
    }
}
