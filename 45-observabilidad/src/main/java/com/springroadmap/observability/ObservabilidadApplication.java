package com.springroadmap.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del modulo 45 - Observabilidad.
 *
 * Este modulo demuestra las 3 patas clasicas de la observabilidad:
 *  - LOGS estructurados en JSON (via Logback + LogstashEncoder).
 *  - TRAZAS distribuidas (Micrometer Tracing + Brave). Cada request obtiene traceId/spanId
 *    que se propagan automaticamente a los logs (MDC) y a llamadas HTTP salientes.
 *  - METRICAS en formato Prometheus (Micrometer Registry Prometheus).
 *
 * Antes vs Ahora:
 *  - ANTES: logs de texto plano, medir tiempos con System.currentTimeMillis(), sin correlacion.
 *  - AHORA: logs JSON con traceId/spanId, metricas expuestas en /actuator/prometheus,
 *          spans generados automaticamente por Micrometer.
 */
@SpringBootApplication
public class ObservabilidadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservabilidadApplication.class, args);
    }
}
