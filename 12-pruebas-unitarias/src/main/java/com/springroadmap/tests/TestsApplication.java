package com.springroadmap.tests;

// Import de la anotación que marca esta clase como una aplicación Spring Boot.
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (punto de entrada) del módulo 12 - Pruebas Unitarias.
 *
 * ANALOGÍA: piensa en esta clase como el "encendido" de un auto. Al llamar a
 * {@code SpringApplication.run}, Spring "arranca el motor": escanea las
 * clases anotadas ({@code @Service}, {@code @RestController}, etc.), las
 * instancia y las conecta entre sí.
 *
 * Este módulo NO enseña Spring en sí (eso ya lo vimos en 02 y 03), sino
 * cómo probar (unit tests) los componentes del proyecto sin arrancar el
 * contexto completo. Por eso la aplicación es minimalista.
 */
@SpringBootApplication
public class TestsApplication {

    /**
     * {@code public static void main(String[] args)} es el método que la JVM
     * ejecuta al lanzar {@code java -jar}. Es la firma estándar de Java desde
     * 1.0 y NO ha cambiado en Java 21.
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) hace todo el trabajo de arranque:
        //  1. Detecta el classpath.
        //  2. Auto-configura beans (DataSource, MVC, etc.).
        //  3. Lanza el servidor embebido Tomcat en el puerto 8080.
        SpringApplication.run(TestsApplication.class, args);
    }
}
