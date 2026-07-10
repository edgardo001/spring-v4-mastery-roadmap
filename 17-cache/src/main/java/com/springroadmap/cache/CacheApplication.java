package com.springroadmap.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del módulo 17 - Caché.
 *
 * Nota: @EnableCaching NO se pone aquí sino en {@link com.springroadmap.cache.config.CacheConfig},
 * para separar responsabilidades (la Main solo arranca el contexto).
 */
@SpringBootApplication
public class CacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheApplication.class, args);
    }
}
