package com.springroadmap.resilience.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * Cliente que envuelve las llamadas al {@link FlakyService} con Retry +
 * CircuitBreaker, aplicando el patrón "decoradores en cadena" de Resilience4j.
 *
 * <p><b>Analogía del mundo real:</b> un "guardia de seguridad + un ayudante
 * paciente". El ayudante (Retry) reintenta la llamada varias veces. El guardia
 * (CircuitBreaker) observa: si el servicio remoto sigue en llamas, corta el
 * paso para no seguir gastando recursos.</p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES: mucho boilerplate manual — for, try/catch, sleep, contador de fallos, timeout.
 *
 * // AHORA: componer Supplier con dos decoradores de Resilience4j y ejecutar.
 * Supplier&lt;String&gt; decorated =
 *     Retry.decorateSupplier(retry, CircuitBreaker.decorateSupplier(cb, flaky::call));
 * return decorated.get();
 * </pre>
 */
@Service
public class ResilientClient {

    // final = referencias inmovibles tras el constructor (inyección por constructor).
    // Constructor injection > field injection: testeable, inmutable, sin proxies raros.
    private final FlakyService flakyService;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    /**
     * Constructor injection: Spring detecta los tipos y pasa los beans registrados.
     * @param flakyService   servicio inestable que queremos proteger.
     * @param circuitBreaker el CB configurado en {@code ResilienceConfig}.
     * @param retry          la política de retry configurada en {@code ResilienceConfig}.
     */
    public ResilientClient(FlakyService flakyService,
                           CircuitBreaker circuitBreaker,
                           Retry retry) {
        this.flakyService = flakyService;
        this.circuitBreaker = circuitBreaker;
        this.retry = retry;
    }

    /**
     * Ejecuta {@link FlakyService#call()} envuelto en Retry + CircuitBreaker.
     *
     * <p><b>Orden de decoración importa:</b> primero decoramos con
     * CircuitBreaker (más interno) y sobre eso Retry (más externo). Así, cada
     * reintento del Retry pasa por el breaker; si el CB está abierto, el Retry
     * verá esa excepción también.</p>
     *
     * @return el string que retorna el FlakyService cuando por fin tiene éxito.
     */
    public String callWithProtection() {
        // Supplier<String> = interfaz funcional Java 8. flakyService::call es method reference.
        // Equivale a la lambda: () -> flakyService.call()
        Supplier<String> base = flakyService::call;

        // Decoramos con CircuitBreaker: si el CB está abierto, lanza CallNotPermittedException.
        Supplier<String> withCb = CircuitBreaker.decorateSupplier(circuitBreaker, base);

        // Decoramos con Retry por fuera: reintenta hasta 3 veces si withCb lanza excepción.
        Supplier<String> withRetryAndCb = Retry.decorateSupplier(retry, withCb);

        // get() dispara la ejecución real. Todo el pipeline se activa aquí.
        return withRetryAndCb.get();
    }
}
