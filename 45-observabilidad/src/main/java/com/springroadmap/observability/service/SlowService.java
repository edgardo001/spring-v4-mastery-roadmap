package com.springroadmap.observability.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio "lento" para simular una operacion con latencia perceptible.
 *
 * Micrometer Tracing detecta esta llamada como parte del span padre del request HTTP.
 * En un escenario real, si esto llamara a otro microservicio via RestClient, Micrometer
 * propagaria los headers de tracing (traceparent) automaticamente.
 */
@Service
public class SlowService {

    private static final Logger log = LoggerFactory.getLogger(SlowService.class);

    /**
     * Simula una llamada lenta (100ms). El log emitido incluye automaticamente
     * traceId y spanId gracias a Micrometer Tracing + MDC.
     *
     * @return valor calculado (simulado)
     */
    public String slowCall() {
        log.info("SlowService.slowCall() inicio");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Restauramos el flag de interrupcion (buena practica).
            Thread.currentThread().interrupt();
            log.warn("SlowService.slowCall() fue interrumpido");
        }
        log.info("SlowService.slowCall() fin");
        return "slow-result-" + System.nanoTime();
    }
}
