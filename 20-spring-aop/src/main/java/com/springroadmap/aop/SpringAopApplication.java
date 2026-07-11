package com.springroadmap.aop;

// Importes de Spring Boot. "import" en Java permite usar clases de otros paquetes sin
// escribir su nombre completo cada vez.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringAopApplication — Punto de arranque del módulo 20.
 *
 * <p><b>Analogía:</b> imagina un teatro. La lógica de negocio (CalculatorService) es
 * el actor en escena. El aspecto (LoggingAspect) es el técnico de iluminación que
 * enciende reflectores (logs + cronómetro) cuando ve el guión marcado con la
 * anotación {@code @Loggable}. El actor NUNCA sabe que hay un técnico: su código está
 * limpio. Esa es la magia de AOP.
 *
 * <p><b>Propósito del módulo:</b> demostrar que con una anotación custom
 * ({@code @Loggable}) y un aspecto ({@code @Around}) se puede medir tiempo y contar
 * llamadas SIN modificar el servicio.
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b>
 * <pre>
 *   // ANTES: SpringApplication.run recibía un array de String creado con {}
 *   SpringApplication.run(App.class, new String[]{});
 *
 *   // AHORA (idéntico, varargs): args ya es String[]; Java 21 lo pasa como varargs.
 *   SpringApplication.run(App.class, args);
 * </pre>
 */
// PREGUNTA DE ALUMNO — "¿qué es la arroba '@' en Java?"
//   Se llama "anotación": una marca que Spring lee en tiempo de ejecución para
//   decidir cómo tratar la clase (crear un bean, escanear el paquete, etc.).
@SpringBootApplication
public class SpringAopApplication {

    /**
     * Método main: entrada de la JVM. "public static void main(String[] args)" es la
     * firma estándar que la JVM busca al arrancar el .jar.
     *
     * @param args argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        // SpringApplication.run: arranca el contexto Spring, registra los beans
        // (incluido LoggingAspect gracias a @Component), y publica el servidor Tomcat
        // embebido en el puerto 8080.
        SpringApplication.run(SpringAopApplication.class, args);
    }
}
