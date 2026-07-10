package com.springroadmap.cache.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Servicio "lento" que simula una consulta pesada (500 ms).
 *
 * - getItem(Long) está anotado con @Cacheable("items"): el resultado se guarda
 *   en el caché "items" usando el id como llave. La segunda llamada con el mismo
 *   id devuelve el valor cacheado sin ejecutar el método (contador NO incrementa).
 *
 * - invalidate(Long) está anotado con @CacheEvict("items"): borra la entrada
 *   del caché para forzar una nueva ejecución la próxima vez.
 *
 * Exponemos un {@link AtomicInteger} con el número de ejecuciones REALES del método
 * para poder verificar en tests que el caché funciona.
 */
@Service
public class SlowService {

    /** Contador de invocaciones REALES (no cuenta cache-hits). */
    private final AtomicInteger callCount = new AtomicInteger(0);

    /**
     * Devuelve un item por id. Si no está en caché, simula 500 ms de latencia
     * y guarda el resultado en el caché "items". Las siguientes llamadas con
     * el mismo id se resuelven desde el caché.
     */
    @Cacheable("items")
    public String getItem(Long id) {
        callCount.incrementAndGet();
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "ITEM_" + id;
    }

    /**
     * Invalida la entrada del caché para ese id. La próxima llamada a getItem(id)
     * volverá a ejecutar el método real (incrementando el contador).
     */
    @CacheEvict("items")
    public void invalidate(Long id) {
        // El cuerpo puede estar vacío: el efecto es la eliminación del caché
        // (gestionada por el aspecto de Spring Cache).
    }

    /** Solo para tests: número de veces que el método real se ejecutó. */
    public int getCallCount() {
        return callCount.get();
    }

    /** Solo para tests: reinicia el contador entre casos. */
    public void resetCallCount() {
        callCount.set(0);
    }
}
