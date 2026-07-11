package com.springroadmap.cicd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase de arranque de la aplicación del módulo 27 (CI/CD).
 *
 * ¿Por qué una app tan pequeña?
 *   El foco de este módulo es la pipeline, no la aplicación. Necesitamos
 *   un ejecutable real para que `mvn verify` compile, testee y empaquete
 *   un JAR verificable por GitHub Actions.
 */
@SpringBootApplication
public class CiCdApplication {

    public static void main(String[] args) {
        SpringApplication.run(CiCdApplication.class, args);
    }
}
