package com.springroadmap.cicd.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador mínimo utilizado como "smoke test" en la pipeline CI.
 *
 * En CI/CD conviene tener un endpoint trivial (a menudo llamado /ping,
 * /health o /status). Sirve para:
 *   1. Validar que el contexto de Spring arrancó tras el build.
 *   2. Probar despliegues canarios (¿responde el pod nuevo?).
 *   3. Health checks de Kubernetes / Docker Compose.
 */
@RestController
@RequestMapping("/api")
public class PingController {

    /**
     * Devuelve "pong" con HTTP 200.
     * Es la respuesta más simple que un test de integración puede verificar.
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
