package com.springroadmap.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * HealthController — endpoints internos del gateway (NO se proxean).
 * El GatewayFilter ignora todo path que empiece por /gateway/.
 *
 * Analogía:
 *   La recepcionista tiene su propio teléfono interno. Si llamas al
 *   /gateway/health, hablas directo con ella, no con las oficinas.
 */
@RestController
@RequestMapping("/gateway")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        // Map.of es Java 9+, thread-safe e inmutable.
        // ANTES (Java 8): new HashMap<>() { put("status","UP"); }
        return Map.of("status", "UP", "service", "spring-cloud-gateway");
    }
}
