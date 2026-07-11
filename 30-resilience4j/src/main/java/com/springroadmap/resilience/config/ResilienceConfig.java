package com.springroadmap.resilience.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuración de Resilience4j vía API PROGRAMÁTICA (no usamos
 * {@code resilience4j-spring-boot3} porque en Boot 4.1.0 su autoconfig no está
 * homologada).
 *
 * <p><b>Analogía del mundo real:</b> este archivo es el "cuadro eléctrico" de
 * la casa — configura los disyuntores (CircuitBreaker) y las políticas de
 * "vuelvo a intentar el interruptor" (Retry) antes de arrancar el sistema.</p>
 *
 * <p><b>Dos beans expuestos:</b>
 * <ul>
 *   <li>{@link CircuitBreaker} llamado {@code flakyServiceCB} — abre el circuito
 *       cuando &gt;= 50 % de las últimas 4 llamadas fallaron.</li>
 *   <li>{@link Retry} llamado {@code flakyServiceRetry} — reintenta hasta 3 veces
 *       con 100 ms entre intentos.</li>
 * </ul></p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES: reintentos manuales con for + try-catch + Thread.sleep.
 * for (int i = 0; i &lt; 3; i++) {
 *   try { return svc.call(); }
 *   catch (Exception e) { Thread.sleep(100); }
 * }
 *
 * // AHORA: declarativo con Retry.of + CircuitBreaker.of.
 * </pre>
 */
@Configuration  // Le dice a Spring: "esta clase produce beans que debes registrar".
public class ResilienceConfig {

    /**
     * CircuitBreaker con ventana deslizante de 4 llamadas y umbral 50 % de fallos.
     *
     * <p>Cuando 2 de 4 llamadas fallen, el circuito se ABRE y las próximas
     * llamadas fallan rápido con {@code CallNotPermittedException} sin siquiera
     * invocar al FlakyService. Tras 5 s en OPEN, pasa a HALF_OPEN y permite
     * llamadas de prueba.</p>
     */
    @Bean  // @Bean = "el objeto que retorno debe registrarse en el ApplicationContext".
    public CircuitBreaker flakyServiceCB() {
        // Builder de configuración. Cada método fluye para ir armando el objeto.
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50f)                       // % de fallos que abre el circuito.
                .slidingWindowSize(4)                            // tamaño de la ventana observada.
                .minimumNumberOfCalls(4)                         // mínimo antes de evaluar el %.
                .waitDurationInOpenState(Duration.ofSeconds(5))  // cuánto queda "abierto" antes de probar.
                .permittedNumberOfCallsInHalfOpenState(2)        // llamadas de prueba en HALF_OPEN.
                .build();
        // "flakyServiceCB" es el nombre lógico del breaker (útil para métricas / logs).
        return CircuitBreaker.of("flakyServiceCB", config);
    }

    /**
     * Retry con máximo 3 intentos y espera fija de 100 ms entre reintentos.
     *
     * <p>Solo reintenta ante {@link RuntimeException}. Si tras el 3er intento
     * sigue fallando, propaga la última excepción al llamador.</p>
     */
    @Bean
    public Retry flakyServiceRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)                                  // 1 llamada + 2 reintentos = 3 intentos totales.
                .waitDuration(Duration.ofMillis(100))            // pausa entre reintentos.
                .retryOnException(ex -> ex instanceof RuntimeException) // lambda: qué excepciones reintentar.
                .build();
        return Retry.of("flakyServiceRetry", config);
    }
}
