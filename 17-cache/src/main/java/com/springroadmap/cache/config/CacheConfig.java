package com.springroadmap.cache.config;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de caché con Caffeine.
 *
 * - @EnableCaching activa el AOP proxy que intercepta @Cacheable/@CacheEvict/@CachePut.
 *   Sin esta anotación, esos aspectos son ignorados silenciosamente.
 *
 * - Definimos un {@link CacheManager} basado en Caffeine con:
 *     * maximumSize = 100         -> evita OOM (política LRU/W-TinyLFU cuando se supera).
 *     * expireAfterWrite = 60s    -> los datos "cadúcan" 60s después de haberse guardado
 *                                   (evita stale data indefinido).
 *
 * En producción típicamente se declararían múltiples cachés con specs distintas;
 * aquí basta con uno para el nombre "items".
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("items");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(60, TimeUnit.SECONDS));
        return manager;
    }
}
