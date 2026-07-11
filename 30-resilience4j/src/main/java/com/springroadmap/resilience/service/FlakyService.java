package com.springroadmap.resilience.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Servicio "flaky" (inestable) — simula un dependence externo que falla las
 * primeras N veces y luego se recupera.
 *
 * <p><b>Analogía del mundo real:</b> el WiFi de un café al abrir por la mañana.
 * Los primeros clientes reciben "no hay Internet" y, tras unos minutos de
 * calentamiento, empieza a funcionar de nuevo.</p>
 *
 * <p><b>Por qué existe esta clase:</b> necesitamos algo que falle de forma
 * predecible para demostrar Retry + CircuitBreaker. En producción, este rol
 * lo cumple una API remota, una BD, o una cola.</p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES: contador con "int" + synchronized.
 * private int counter = 0;
 * public synchronized String call() { counter++; ... }
 *
 * // AHORA: AtomicInteger — lock-free, thread-safe, más eficiente.
 * private final AtomicInteger counter = new AtomicInteger(0);
 * </pre>
 */
@Service  // Le dice a Spring: "esto es un servicio, gestióname el ciclo de vida como singleton".
public class FlakyService {

    /**
     * Cuántas veces falla antes de estabilizarse. Las 3 primeras llamadas
     * lanzan {@link RuntimeException}; a partir de la 4ª, retorna "OK".
     */
    private static final int FAILURES_BEFORE_SUCCESS = 3;

    /**
     * Contador atómico de invocaciones. {@code AtomicInteger} garantiza
     * incrementos seguros aún si múltiples hilos llaman a {@link #call()}
     * al mismo tiempo (sin necesidad de {@code synchronized}).
     * <p>{@code final} = la referencia al AtomicInteger no cambia, pero su
     * valor interno sí.</p>
     */
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Llamada al "servicio externo". Falla las primeras
     * {@value #FAILURES_BEFORE_SUCCESS} veces, después retorna "OK".
     *
     * @return la cadena {@code "OK from FlakyService (attempt=N)"} si tuvo éxito.
     * @throws RuntimeException si estamos dentro del rango de fallos iniciales.
     */
    public String call() {
        // incrementAndGet() suma 1 y devuelve el nuevo valor, atómicamente.
        int attempt = counter.incrementAndGet();

        // PREGUNTA DE ALUMNO — "¿por qué lanzo RuntimeException y no una checked?"
        //   Resilience4j intercepta excepciones no chequeadas por defecto. Además,
        //   así el llamador no tiene que declarar "throws" — flujo más limpio.
        if (attempt <= FAILURES_BEFORE_SUCCESS) {
            throw new RuntimeException("Simulated failure on attempt " + attempt);
        }
        return "OK from FlakyService (attempt=" + attempt + ")";
    }

    /**
     * Reinicia el contador. Útil sólo para tests.
     */
    public void reset() {
        counter.set(0);
    }

    /**
     * Getter del contador (útil para asserts en tests).
     * @return número de invocaciones a {@link #call()} desde el último {@link #reset()}.
     */
    public int getAttempts() {
        return counter.get();
    }
}
