package com.springroadmap.graphql;

// 'import' trae clases de otros paquetes. Sin esta linea tendriamos que escribir
// 'org.springframework.boot.SpringApplication' cada vez.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot del modulo 32 (Spring GraphQL).
 *
 * Analogia: es como el conserje de un edificio. Al arrancar, abre todas las oficinas
 * (beans), enciende las luces (autoconfiguracion) y deja la puerta lista para recibir
 * clientes (peticiones HTTP en el puerto 8080, endpoint /graphql).
 *
 * PREGUNTA DE ALUMNO - "Que hace @SpringBootApplication?"
 *   Es un atajo. Equivale a poner tres anotaciones juntas:
 *     @Configuration           -> "esta clase define beans"
 *     @EnableAutoConfiguration -> "detecta librerias del classpath y configura por defecto"
 *     @ComponentScan           -> "busca @Component/@Service/@Controller en este paquete y subpaquetes"
 *
 * ANTES (Java 8, Spring Boot 1.x) vs AHORA (Java 21, Spring Boot 4.1.0)
 * ---------------------------------------------------------------
 *   ANTES:
 *     public static void main(String[] args) {
 *         SpringApplication app = new SpringApplication(GraphqlApplication.class);
 *         app.run(args);
 *     }
 *   AHORA:
 *     SpringApplication.run(GraphqlApplication.class, args);
 *
 *   (misma idea, menos ceremonia; el metodo estatico 'run' hace el 'new' por ti).
 */
@SpringBootApplication
public class GraphqlApplication {

    /**
     * Metodo main: la JVM lo invoca al ejecutar 'java -jar graphql-1.0.0.jar'.
     * - 'public'  -> visible desde fuera de la clase (obligatorio para main).
     * - 'static'  -> no necesita instancia de la clase para ejecutarse.
     * - 'void'    -> no retorna nada.
     * - String[]  -> arreglo de argumentos de linea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(GraphqlApplication.class, args);
    }
}
