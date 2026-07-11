package com.springroadmap.async.config;

// Executor: contrato Java estándar de "algo que ejecuta tareas Runnable".
import java.util.concurrent.Executor;
// ThreadPoolExecutor: pool de hilos JDK; nos interesa su CallerRunsPolicy.
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// @EnableAsync: enciende el soporte de @Async en todo el ApplicationContext.
import org.springframework.scheduling.annotation.EnableAsync;
// ThreadPoolTaskExecutor: envoltorio Spring del ThreadPoolExecutor JDK con
// integración con el ciclo de vida del contexto (initialize, shutdown limpios).
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuración del pool de hilos para ejecución asíncrona.
 *
 * <p><b>¿Por qué una clase aparte y no @EnableAsync en la App?</b> Porque
 * "separation of concerns": la App arranca todo; los detalles del pool
 * (core/max/queue) se cambian aquí sin tocar el main.
 *
 * <p><b>Analogía</b>: el pool es un equipo de meseros. {@code corePoolSize=2}
 * son los meseros fijos siempre en turno; {@code maxPoolSize=5} son los que
 * entran si hay avalancha; {@code queueCapacity=10} es la cola de tickets en
 * la cocina esperando a que un mesero libere. Si la cola se llena, el mesero
 * jefe (Tomcat, "caller") se pone el delantal él mismo (CallerRunsPolicy).
 *
 * <hr>
 * <b>ANTES (Java 8) vs AHORA (Java 21)</b>
 * <pre>
 * // ANTES: pool armado con clases del JDK y sin integración con Spring.
 * ExecutorService pool = Executors.newFixedThreadPool(5);
 * pool.submit(() -&gt; enviarCorreo("ada@x.com"));
 *
 * // AHORA: el pool es un @Bean, y Spring lo inyecta donde diga
 * // @Async("taskExecutor"). El shutdown ordenado es automático.
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué el bean se llama 'taskExecutor'?"
 * Porque {@code @EnableAsync} busca por defecto un bean con ese nombre. Si
 * lo llamáramos "miPool", habría que escribir {@code @Async("miPool")} en
 * cada método asíncrono. Usar el nombre convencional evita ambigüedades.
 */
@Configuration      // Marca la clase como fuente de definiciones de beans.
@EnableAsync        // Sin esto, @Async es completamente ignorado por Spring.
public class AsyncConfig {

    /**
     * Bean del pool de hilos con nombre "taskExecutor".
     *
     * <p>Retorna {@link Executor} (interfaz genérica) para que otros
     * componentes NO dependan del tipo concreto. Es el principio de
     * "programa contra interfaces, no contra implementaciones".
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        // Construimos el envoltorio Spring del pool.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // corePoolSize=2: hilos "fijos" que siempre viven, incluso ociosos.
        executor.setCorePoolSize(2);

        // maxPoolSize=5: techo de hilos si la cola se llena y hace falta más
        // capacidad temporal. Nunca superará 5 hilos concurrentes.
        executor.setMaxPoolSize(5);

        // queueCapacity=10: cola de tareas pendientes cuando los hilos activos
        // están ocupados. Recién cuando ESTA cola se llena, el pool crea nuevos
        // hilos hasta llegar a maxPoolSize.
        executor.setQueueCapacity(10);

        // Prefijo del nombre de hilo (aparece en logs: "async-task-1", "async-task-2"...).
        // Ayuda a identificar en producción qué hilo hizo qué.
        executor.setThreadNamePrefix("async-task-");

        // Política si el pool está saturado (5 hilos + 10 en cola = 15 tareas):
        // CallerRunsPolicy → el hilo que envió la tarea (Tomcat) la ejecuta él
        // mismo. Ralentiza al llamador en vez de perder trabajo (mejor que
        // AbortPolicy que lanza excepción, o DiscardPolicy que la tira).
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Prepara internamente el ThreadPoolExecutor JDK subyacente.
        executor.initialize();

        return executor;
    }
}
