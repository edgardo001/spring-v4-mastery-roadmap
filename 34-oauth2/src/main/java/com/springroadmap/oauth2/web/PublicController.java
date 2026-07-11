package com.springroadmap.oauth2.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints abiertos: no requieren JWT.
 * Corresponden al patrón `/api/public/**` permitAll en SecurityConfig.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    /**
     * GET /api/public/hello -> saludo sin autenticación.
     * `Map.of(...)` crea un mapa inmutable (lanza si intentas modificar).
     */
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hola desde endpoint público (sin JWT)");
    }
}
