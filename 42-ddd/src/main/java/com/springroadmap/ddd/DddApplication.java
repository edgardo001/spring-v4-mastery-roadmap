package com.springroadmap.ddd;

// Import de Spring Boot que arranca el contexto y auto-configura todo.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del modulo 42 - DDD Tactico.
 *
 * ANALOGIA: piensa en esta clase como el "boton de encender" de un electrodomestico.
 * Sin ella, todos los componentes existen pero nadie los enciende.
 *
 * PALABRAS CLAVE:
 *  - {@code @SpringBootApplication}: combina @Configuration + @EnableAutoConfiguration + @ComponentScan.
 *  - {@code public static void main}: punto de entrada estandar de una aplicacion Java.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *  - En Java 8 tambien se usaba public static void main(String[] args) igual.
 *  - Cambio importante: Spring Boot 4 requiere Java 17+ como minimo (aqui usamos 21).
 */
@SpringBootApplication
public class DddApplication {

    /**
     * main: arranca el contexto de Spring.
     * @param args argumentos de linea de comandos (p.ej. --server.port=9090).
     */
    public static void main(String[] args) {
        // SpringApplication.run: inicializa el contenedor IoC, escanea beans y levanta Tomcat embebido.
        SpringApplication.run(DddApplication.class, args);
    }
}
