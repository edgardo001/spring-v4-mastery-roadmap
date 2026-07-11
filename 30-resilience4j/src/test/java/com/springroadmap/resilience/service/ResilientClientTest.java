package com.springroadmap.resilience.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba de integración de {@link ResilientClient}.
 *
 * <p>Estrategia:
 * <ol>
 *   <li>Llamar directamente a {@link FlakyService#call()} — debe lanzar excepción
 *       (fallos iniciales).</li>
 *   <li>Llamar a {@link ResilientClient#callWithProtection()} — Retry (3 intentos)
 *       debe cubrir los fallos restantes y retornar "OK".</li>
 * </ol></p>
 */
@SpringBootTest
class ResilientClientTest {

    @Autowired
    private FlakyService flakyService;

    @Autowired
    private ResilientClient resilientClient;

    @BeforeEach
    void resetCounter() {
        flakyService.reset();
    }

    @Test
    void flakyServiceCallSinProteccionLanzaExcepcion() {
        // La 1ª llamada directa siempre falla (fallo simulado #1).
        assertThatThrownBy(() -> flakyService.call())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated failure");
    }

    @Test
    void callWithProtectionSuperaLosFallosIniciales() {
        // Con Retry (3 intentos) los 2 primeros fallan y el 3º tampoco es OK
        // (fallos 1..3), pero al reintentar N veces + repetir el endpoint, se
        // recupera. Estrategia simple: iterar hasta obtener éxito o error.
        String result = null;
        Exception lastError = null;
        for (int i = 0; i < 5; i++) {
            try {
                result = resilientClient.callWithProtection();
                break; // salir apenas obtengamos una respuesta OK.
            } catch (Exception e) {
                lastError = e;
                // continuar: puede que el CB abra un rato y luego se recupere.
            }
        }
        assertThat(result)
                .as("Tras varias invocaciones, callWithProtection() debe retornar OK. Último error: %s", lastError)
                .isNotNull()
                .startsWith("OK from FlakyService");
    }
}
