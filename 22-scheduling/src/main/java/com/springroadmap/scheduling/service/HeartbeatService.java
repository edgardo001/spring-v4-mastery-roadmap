package com.springroadmap.scheduling.service;

// AtomicInteger: contador seguro para uso concurrente (multi-hilo).
// Como las tareas @Scheduled pueden ejecutarse en un hilo separado, y el
// endpoint HTTP consulta el valor desde OTRO hilo, un `int` normal podría
// leerse "a medias". AtomicInteger garantiza operaciones atómicas.
import java.util.concurrent.atomic.AtomicInteger;

// @Scheduled es la anotación que convierte un método en tarea programada.
import org.springframework.scheduling.annotation.Scheduled;
// @Service marca esta clase como un componente de servicio (bean singleton).
import org.springframework.stereotype.Service;

/**
 * Servicio que emite "latidos" (heartbeats) automáticos para demostrar
 * las dos formas más comunes de programar tareas en Spring:
 *   - fixedRate: cada X milisegundos exactos.
 *   - cron: usando una expresión estilo Unix cron.
 *
 * Analogía del mundo real:
 *   Un marcapasos cardiaco. No le pides nada; late solo cada X
 *   milisegundos y lleva la cuenta de cuántos latidos ha dado. Aquí
 *   tenemos DOS marcapasos independientes: uno mide latidos por
 *   fixedRate y otro por cron.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué AtomicInteger y no un `int`?"
 *   Porque el hilo del scheduler escribe (tickCount++) al mismo tiempo
 *   que el hilo HTTP lee (getTickCount()). Con un `int` normal podrías
 *   ver un valor intermedio o perder incrementos. AtomicInteger hace
 *   la operación "incrementa y retorna" en un solo paso indivisible.
 *
 * ANTES (Java 8 / clásico):
 *   private int tickCount = 0;
 *   public synchronized void heartbeat() { tickCount++; }
 *   public synchronized int getTickCount() { return tickCount; }
 *
 * AHORA (Java 21 / preferido):
 *   private final AtomicInteger tickCount = new AtomicInteger(0);
 *   public void heartbeat() { tickCount.incrementAndGet(); }
 *   public int getTickCount() { return tickCount.get(); }
 *
 * Ambas formas funcionan. La segunda es más idiomática, no requiere
 * `synchronized` (menos overhead) y expresa la intención de forma clara.
 */
@Service
public class HeartbeatService {

    /**
     * Contador de "ticks" por fixedRate.
     * `final`: la REFERENCIA al AtomicInteger no cambia; su VALOR interno sí.
     */
    private final AtomicInteger tickCount = new AtomicInteger(0);

    /**
     * Contador de "cron ticks" por expresión cron.
     */
    private final AtomicInteger cronCount = new AtomicInteger(0);

    /**
     * Latido rápido: cada 5000 ms = 5 segundos.
     *
     * `fixedRate = 5000` significa "arrancame cada 5s medidos DESDE el
     * inicio de la ejecución anterior". Si el método tardara 6s en
     * correr, la próxima ejecución arrancaría inmediatamente después
     * (queda encolada).
     *
     * PREGUNTA DE ALUMNO — "¿Por qué no hay `return`?"
     *   @Scheduled exige métodos con retorno `void` y sin argumentos.
     *   Spring no sabría qué hacer con un valor devuelto ni cómo
     *   proveer parámetros a algo que él mismo dispara.
     */
    @Scheduled(fixedRate = 5000)
    public void heartbeat() {
        // incrementAndGet() suma 1 y devuelve el nuevo valor, todo atómico.
        int current = tickCount.incrementAndGet();
        // System.out.println es intencional: sin logger para no añadir dep.
        System.out.println("[fixedRate] tick #" + current);
    }

    /**
     * Latido cron: expresión de 6 campos = "*\/2 * * * * *" (cada 2 segundos).
     *
     * Formato Spring cron (6 campos):
     *   Segundo Minuto Hora DíaMes Mes DíaSemana
     *   `*\/2` en el primer campo = "cada 2 segundos".
     *
     * PREGUNTA DE ALUMNO — "¿Cron de Unix estándar no era de 5 campos?"
     *   Sí, Unix usa 5 campos (sin segundos). Spring extiende a 6 para
     *   permitir precisión de segundos, que es común en apps.
     */
    @Scheduled(cron = "*/2 * * * * *")
    public void cronTick() {
        int current = cronCount.incrementAndGet();
        System.out.println("[cron */2s] cronTick #" + current);
    }

    /** Getter simple para consulta desde el controller o tests. */
    public int getTickCount() {
        return tickCount.get();
    }

    /** Getter simple para el contador cron. */
    public int getCronCount() {
        return cronCount.get();
    }
}
