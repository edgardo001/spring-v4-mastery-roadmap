package com.springroadmap.observability.controller;

import com.springroadmap.observability.service.SlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller instrumentado automaticamente por Micrometer Tracing.
 *
 * Cada request a este endpoint crea un span raiz cuyo traceId/spanId aparecen
 * en cada linea de log (patron de Boot 4: %mdc{traceId} %mdc{spanId} en el pattern por defecto,
 * o campos "traceId"/"spanId" en el JSON emitido por LogstashEncoder + logbackMdcAdapter).
 */
@RestController
@RequestMapping("/api/orders")
public class TracedController {

    private static final Logger log = LoggerFactory.getLogger(TracedController.class);

    private final SlowService slowService;

    // Sin Lombok: constructor explicito para inyeccion.
    public TracedController(SlowService slowService) {
        this.slowService = slowService;
    }

    /**
     * GET /api/orders/{id} - devuelve una orden simulada.
     *
     * Antes (Boot 2.x logging texto plano):
     *   2024-01-01 12:00:00 INFO  Fetching order id=1
     * Ahora (Boot 4.1 + Micrometer Tracing + JSON):
     *   {"@timestamp":"...","message":"Fetching order id=1","traceId":"abc","spanId":"def",...}
     */
    @GetMapping("/{id}")
    public Map<String, Object> getOrder(@PathVariable Long id) {
        log.info("Fetching order id={}", id);
        String detail = slowService.slowCall();
        log.info("Order id={} obtenida con detail={}", id, detail);
        return Map.of(
                "id", id,
                "status", "OK",
                "detail", detail
        );
    }
}
