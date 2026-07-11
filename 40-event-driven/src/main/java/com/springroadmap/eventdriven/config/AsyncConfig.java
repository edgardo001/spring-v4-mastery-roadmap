package com.springroadmap.eventdriven.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de ejecución asíncrona.
 *
 * @Configuration → esta clase define beans (objetos gestionados por Spring).
 * @EnableAsync   → activa el soporte de @Async en toda la aplicación.
 *
 * Analogía:
 *   `eventExecutor` es la "cuadrilla de trabajadores" (hilos) que corren en segundo plano.
 *   Cuando un método marcado @Async se invoca, Spring lo lanza a esta cuadrilla en lugar
 *   de bloquear al que llama.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES: new Thread(() -> { ... }).start();            // manual, sin pool ni límites
 *   AHORA: ThreadPoolTaskExecutor gestionado por Spring   // pool con tamaño, cola y nombre de hilos
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Define un pool de hilos llamado "eventExecutor".
     * Los listeners marcados @Async("eventExecutor") correrán aquí.
     *
     * corePoolSize=2     → 2 hilos siempre activos.
     * maxPoolSize=4      → puede escalar hasta 4 bajo carga.
     * queueCapacity=50   → si todos están ocupados, encola hasta 50 tareas.
     * threadNamePrefix   → hace legibles los logs (event-1, event-2, ...).
     */
    @Bean(name = "eventExecutor")
    public Executor eventExecutor() {
        // 'var' (Java 10+) infiere el tipo; en Java 8 escribirías: ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("event-");
        executor.initialize();
        return executor;
    }
}
