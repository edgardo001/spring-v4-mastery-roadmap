package com.springroadmap.reactive.repository;

import com.springroadmap.reactive.domain.Item;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Repositorio reactivo: todos los metodos retornan Mono/Flux y NUNCA bloquean
 * el hilo del event loop de Netty.
 */
public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
}
