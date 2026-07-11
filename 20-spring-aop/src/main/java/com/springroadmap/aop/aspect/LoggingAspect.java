package com.springroadmap.aop.aspect;

// AtomicInteger: contador thread-safe (varios hilos pueden incrementarlo sin race
// conditions). Lo usamos para exponer callCount() a los tests.
import java.util.concurrent.atomic.AtomicInteger;

// AspectJ: la librería que define @Aspect, @Around, ProceedingJoinPoint. Spring AOP
// reutiliza sus anotaciones pero las ejecuta con proxies dinámicos (no con weaving).
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * LoggingAspect — Aspecto transversal que mide tiempo y cuenta llamadas.
 *
 * <p><b>Analogía:</b> es un torno en la puerta de un supermercado. Cada persona
 * (llamada al método) que pasa por la puerta con la etiqueta {@code @Loggable}
 * incrementa el contador y anota cuánto tardó en salir. La persona no sabe que
 * existe el torno.
 *
 * <p><b>Palabras clave explicadas:</b>
 * <ul>
 *   <li>{@code @Aspect} — declara que esta clase contiene "advices" (código
 *       transversal). AspectJ la reconoce al escanear el classpath.</li>
 *   <li>{@code @Component} — la registra como bean Spring, para que el auto-proxy
 *       de {@code spring-boot-starter-aop} la aplique a los beans elegibles.</li>
 *   <li>{@code @Around} — el advice más poderoso: rodea al método interceptado.
 *       DEBE llamar a {@code pjp.proceed()} para que el método real se ejecute.</li>
 *   <li>{@code ProceedingJoinPoint} — objeto que representa la ejecución que está
 *       siendo interceptada. Con {@code proceed()} devuelves el control al método
 *       original y recibes su valor de retorno.</li>
 *   <li>{@code final} en variables locales — indica que la referencia no cambiará.
 *       No es obligatorio pero es buena práctica.</li>
 * </ul>
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b>
 * <pre>
 *   // ANTES: cronómetro manual con System.currentTimeMillis()
 *   long start = System.currentTimeMillis();
 *   Object r = pjp.proceed();
 *   long ms = System.currentTimeMillis() - start;
 *
 *   // AHORA: sigue siendo el patrón idiomático. Java 21 no añadió un StopWatch
 *   // estándar; Spring ofrece org.springframework.util.StopWatch si lo prefieres.
 * </pre>
 *
 * <p><b>Edge case importante (self-invocation):</b> Spring AOP funciona con
 * <i>proxies</i>. Si {@code CalculatorService} llamara a otro método propio con
 * {@code this.otroMetodo()}, el proxy NO se enteraría y el aspecto NO se ejecutaría.
 * Sólo intercepta llamadas que entran desde afuera (p. ej. desde el controller).
 */
// PREGUNTA DE ALUMNO — "¿por qué el aspecto no se aplica en tests con MockMvc standalone?"
//   Porque standaloneSetup NO carga el contexto Spring completo (sin auto-proxy),
//   así que los @Aspect no envuelven a los beans. Para ver aspectos en tests usa
//   @SpringBootTest, como hace LoggingAspectTest.
@Aspect
@Component
public class LoggingAspect {

    // Logger SLF4J: reemplaza System.out.println. Incluye timestamp, hilo y clase.
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Contador atómico: los tests lo leen para verificar cuántas veces se disparó el
    // aspecto. "final" porque la referencia al AtomicInteger no cambia (su valor
    // interno sí).
    private final AtomicInteger callCount = new AtomicInteger(0);

    /**
     * Advice @Around aplicado a cualquier método anotado con @Loggable.
     *
     * <p>El pointcut {@code @annotation(com.springroadmap.aop.annotation.Loggable)}
     * significa: "intercepta la ejecución de cualquier método cuyo símbolo lleve la
     * anotación @Loggable, sin importar el paquete o el nombre del método".
     *
     * @param pjp punto de unión "en curso"; permite ejecutar el método real.
     * @return lo que devolvió el método original (obligatorio propagarlo).
     * @throws Throwable cualquier excepción del método original debe re-lanzarse
     *         intacta, si no las excepciones "desaparecen".
     */
    @Around("@annotation(com.springroadmap.aop.annotation.Loggable)")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {
        // Nombre corto del método interceptado, p.ej. "CalculatorService.add(..)".
        final String methodName = pjp.getSignature().toShortString();

        // Incrementamos el contador ANTES de proceder: así los tests pueden contar
        // incluso si el método original lanza una excepción.
        final int currentCount = callCount.incrementAndGet();

        // Cronómetro con nanoTime (más preciso que currentTimeMillis para métodos
        // rápidos como add/sub).
        final long start = System.nanoTime();

        try {
            log.info(">> [AOP] Entrando a {} (llamada #{})", methodName, currentCount);

            // proceed() ejecuta el método REAL. Si no lo llamas, el método nunca
            // corre (útil para caches, seguridad, etc., pero aquí SIEMPRE queremos
            // ejecutarlo).
            Object result = pjp.proceed();

            final long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
            log.info("<< [AOP] Saliendo de {} en {} ms — resultado={}",
                    methodName, elapsedMs, result);

            return result;
        } catch (Throwable ex) {
            // Re-lanzar para no "tragarse" la excepción original.
            final long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
            log.error("!! [AOP] {} falló tras {} ms: {}",
                    methodName, elapsedMs, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Expone el contador para los tests. En producción normalmente no expondrías
     * esto, pero para validar el aspecto es la forma más limpia.
     *
     * @return número de veces que el aspecto se ha ejecutado desde el arranque.
     */
    public int callCount() {
        return callCount.get();
    }

    /** Resetea el contador entre tests. */
    public void reset() {
        callCount.set(0);
    }
}
