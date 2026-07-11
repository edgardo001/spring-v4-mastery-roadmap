package com.springroadmap.testcontainers;

// Import: SpringApplication es la clase que arranca el contexto de Spring Boot.
// Analogía: es el "botón de encendido" de la aplicación.
import org.springframework.boot.SpringApplication;
// @SpringBootApplication combina @Configuration + @EnableAutoConfiguration + @ComponentScan.
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo 36 — Testcontainers avanzado.
 *
 * <p>Propósito: aplicación CRUD de Productos usada como sujeto de pruebas
 * para demostrar cómo Testcontainers levanta contenedores Docker REALES
 * (Postgres, Redis) durante los tests de integración.</p>
 *
 * <p>Analogía del mundo real: es como una fábrica de galletas que en producción
 * usa una máquina industrial (Postgres), pero para las pruebas de calidad
 * usa una réplica en miniatura (contenedor Docker) que se enciende y apaga
 * automáticamente.</p>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué H2 en tiempo de ejecución si el módulo va de Testcontainers?"
 *   Porque queremos que el JAR ejecutable arranque SIN necesidad de Docker.
 *   Los contenedores Docker sólo se usan cuando corres los tests marcados @Testcontainers.</p>
 */
@SpringBootApplication
public class TestcontainersApplication {

    /**
     * Método main — el "arranque" del programa Java.
     *
     * <p>Palabras clave:
     * <ul>
     *   <li><code>public</code>: visible desde cualquier parte, requerido por la JVM.</li>
     *   <li><code>static</code>: pertenece a la clase, no a una instancia — la JVM lo invoca sin crear un objeto.</li>
     *   <li><code>void</code>: no retorna nada.</li>
     *   <li><code>String[] args</code>: argumentos de línea de comandos (ej: <code>--server.port=9090</code>).</li>
     * </ul></p>
     */
    public static void main(String[] args) {
        // SpringApplication.run arranca el contexto: escanea @Component, arma beans,
        // levanta el servidor embebido (Tomcat) y publica los endpoints REST.
        SpringApplication.run(TestcontainersApplication.class, args);
    }
}

/*
 * ============================================================================
 * ANTES (Java 8) vs AHORA (Java 21)
 * ============================================================================
 * ANTES (Java 8): el main se veía IGUAL. Java 21 no cambió esta firma.
 *   public static void main(String[] args) { ... }
 *
 * ANTES (Spring Boot 1.x): había varias anotaciones separadas.
 *   @Configuration
 *   @EnableAutoConfiguration
 *   @ComponentScan
 *   public class Application { ... }
 *
 * AHORA (Spring Boot 4.x): una sola anotación agrupa todo.
 *   @SpringBootApplication
 *   public class Application { ... }
 * ============================================================================
 */
