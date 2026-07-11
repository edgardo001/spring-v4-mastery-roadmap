package com.springroadmap.docs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo 15.
 *
 * Al arrancar, springdoc-openapi detecta automáticamente los @RestController
 * y expone:
 *   - GET /v3/api-docs        -> especificación OpenAPI 3.1 en JSON
 *   - GET /swagger-ui.html    -> UI interactiva (redirige a /swagger-ui/index.html)
 */
@SpringBootApplication
public class DocumentacionApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentacionApiApplication.class, args);
    }
}
