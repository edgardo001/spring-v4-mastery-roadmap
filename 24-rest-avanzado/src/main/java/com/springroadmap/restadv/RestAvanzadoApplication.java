package com.springroadmap.restadv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap del módulo 24 — REST avanzado.
 *
 * Enseña Pageable, ETag y versionado por header en un controller REST puro
 * (sin JPA — el repositorio es un ConcurrentHashMap en memoria).
 */
@SpringBootApplication
public class RestAvanzadoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestAvanzadoApplication.class, args);
    }
}
