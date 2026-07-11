package com.springroadmap.testadv;

// Import de la anotación mágica que arranca Spring Boot completo:
// - habilita autoconfiguración (starter-web, starter-data-jpa)
// - hace component-scan del paquete actual y subpaquetes
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo 25 — Testing Avanzado.
 *
 * Analogía del mundo real:
 *   Esta clase es el "interruptor general" del edificio. Al pulsarlo (main),
 *   Spring enciende luces (beans), climatización (JPA), y el portero (web server).
 *
 * Palabras clave explicadas:
 *   - {@code @SpringBootApplication}: combo de tres anotaciones:
 *       {@code @Configuration} + {@code @EnableAutoConfiguration} + {@code @ComponentScan}.
 *   - {@code public static void main}: firma estándar de Java para arrancar un proceso JVM.
 *   - {@code SpringApplication.run(...)}: bootstrap; construye el contexto e inicia Tomcat.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   No hay diferencia sintáctica en esta clase; la firma de main es igual desde Java 1.0.
 *   Lo "moderno" está en el ecosistema Spring 4 / Boot 4 que consume por debajo.
 */
@SpringBootApplication
public class TestingAvanzadoApplication {

    /**
     * Método main: la JVM lo llama al ejecutar {@code java -jar testing-avanzado-1.0.0.jar}.
     *
     * @param args argumentos de línea de comandos (no los usamos, pero Spring los propaga
     *             a {@code Environment} por si quieres pasar {@code --server.port=9090}).
     */
    public static void main(String[] args) {
        // PREGUNTA DE ALUMNO — "¿por qué le paso .class y no una instancia?"
        //   Spring usa reflexión para leer las anotaciones de la clase; no necesita un objeto.
        SpringApplication.run(TestingAvanzadoApplication.class, args);
    }
}
