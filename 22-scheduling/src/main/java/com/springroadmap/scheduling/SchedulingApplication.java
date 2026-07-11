// Package raíz del módulo — coincide con la carpeta física.
package com.springroadmap.scheduling;

// SpringApplication.run(...) arranca el contexto (el "contenedor") de Spring.
import org.springframework.boot.SpringApplication;
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación del módulo 22.
 *
 * Analogía del mundo real:
 *   Piensa en Spring como una FÁBRICA de robots. Cuando llamas a
 *   SpringApplication.run(...), enciendes la fábrica: los robots (beans)
 *   se ensamblan solos y se ponen a trabajar. En este módulo, uno de esos
 *   robots (HeartbeatService) tiene un despertador incorporado: cada X
 *   segundos se enciende solo, sin que nadie le pida nada.
 *
 * Nota importante:
 *   El @EnableScheduling que activa las tareas programadas NO está aquí,
 *   está en {@link com.springroadmap.scheduling.config.SchedulingConfig}
 *   para separar responsabilidades (main solo arranca, config configura).
 *
 * PREGUNTA DE ALUMNO — "¿Podría poner @EnableScheduling directamente aquí?"
 *   Sí, funciona igual porque esta clase también es @Configuration (por
 *   estar anotada con @SpringBootApplication). Se separa por estilo y
 *   claridad: una clase = una responsabilidad.
 */
@SpringBootApplication
public class SchedulingApplication {

    /**
     * Método main — el JVM lo llama al ejecutar `java -jar scheduling-1.0.0.jar`.
     *
     * Palabras clave:
     *   - `public`: visible desde fuera del paquete (obligatorio en main).
     *   - `static`: pertenece a la clase, no a una instancia (no hay que
     *     hacer `new SchedulingApplication()` para llamarlo).
     *   - `void`: no retorna nada.
     *   - `String[] args`: argumentos pasados por línea de comandos.
     */
    public static void main(String[] args) {
        // Arranca el contexto Spring, escanea beans y activa el scheduler.
        SpringApplication.run(SchedulingApplication.class, args);
    }
}
