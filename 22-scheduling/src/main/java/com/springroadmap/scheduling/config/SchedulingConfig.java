package com.springroadmap.scheduling.config;

// @Configuration marca esta clase como una fuente de beans/configuración.
import org.springframework.context.annotation.Configuration;
// @EnableScheduling es LA anotación que activa el soporte @Scheduled.
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase de configuración que ACTIVA el motor de tareas programadas.
 *
 * Analogía del mundo real:
 *   Es el interruptor general del panel eléctrico. Aunque tengas focos
 *   (@Scheduled) instalados por toda la casa, si el interruptor central
 *   está apagado (sin @EnableScheduling) ninguno se enciende. Con esta
 *   línea, Spring crea un TaskScheduler interno y empieza a mirar todos
 *   los beans buscando métodos marcados con @Scheduled.
 *
 * Por qué existe una clase separada (y no lo ponemos en el main):
 *   Separación de responsabilidades. Si mañana necesitamos configurar
 *   un ThreadPoolTaskScheduler custom (ver módulo README, concepto #3),
 *   este archivo crecerá con esa lógica sin ensuciar el main.
 *
 * PREGUNTA DE ALUMNO — "¿Cuántos hilos usa por defecto?"
 *   UNO SOLO. Si tuvieras 3 tareas simultáneas, se ejecutarían en
 *   secuencia. Para producción real hay que configurar un pool con
 *   size > 1 (ver ejercicio 4 del README).
 *
 * ANTES (Java 8, XML-config):
 *   <task:annotation-driven scheduler="myScheduler"/>
 *   <task:scheduler id="myScheduler" pool-size="10"/>
 *
 * AHORA (Java 21, config por anotaciones):
 *   @Configuration + @EnableScheduling → 2 líneas y listo.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Cuerpo vacío a propósito: el poder está en las dos anotaciones de arriba.
    // Aquí es donde en el futuro pondríamos @Bean ThreadPoolTaskScheduler.
}
