package com.springroadmap.scheduling.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.springroadmap.scheduling.service.HeartbeatService;

// @GetMapping: atajo de @RequestMapping(method = GET).
import org.springframework.web.bind.annotation.GetMapping;
// @RestController = @Controller + @ResponseBody (todos los métodos devuelven JSON).
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint REST que expone los contadores del HeartbeatService.
 *
 * Analogía del mundo real:
 *   Es el pantallita LED en la máquina del marcapasos: te muestra el
 *   número de latidos que lleva. La máquina late sola; el LED solo
 *   informa el estado actual cuando alguien lo mira.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué constructor injection y no @Autowired en campo?"
 *   Constructor injection es la mejor práctica desde Spring 4.3+:
 *     - Deja los campos `final` (inmutables, seguros ante concurrencia).
 *     - Hace explícitas las dependencias (obvias al leer el constructor).
 *     - Facilita el testing (pasás los mocks al constructor sin reflexión).
 *
 * ANTES (Java 8, field injection — desaconsejado hoy):
 *   @Autowired private HeartbeatService service;   // no final, oculto
 *
 * AHORA (Java 21, constructor injection):
 *   private final HeartbeatService service;
 *   public HeartbeatController(HeartbeatService service) { ... }
 */
@RestController
public class HeartbeatController {

    /** Dependencia inmutable, inyectada por constructor. */
    private final HeartbeatService service;

    /**
     * Constructor único → Spring lo detecta y lo usa para inyectar
     * automáticamente el bean HeartbeatService (no requiere @Autowired
     * explícito desde Spring 4.3+).
     */
    public HeartbeatController(HeartbeatService service) {
        this.service = service;
    }

    /**
     * GET /api/heartbeat → { "tick": N, "cron": M }
     *
     * Retornar un Map<String,Object> es la forma más simple de generar
     * un JSON en Spring sin crear un DTO específico. Jackson (incluido
     * en spring-boot-starter-web) lo serializa automáticamente.
     *
     * Usamos LinkedHashMap para preservar el orden de inserción de las
     * claves en el JSON (tick antes que cron), aunque no es funcional.
     */
    @GetMapping("/api/heartbeat")
    public Map<String, Object> heartbeat() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tick", service.getTickCount());
        response.put("cron", service.getCronCount());
        return response;
    }
}
