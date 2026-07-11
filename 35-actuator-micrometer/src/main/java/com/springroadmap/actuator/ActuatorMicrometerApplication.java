package com.springroadmap.actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion Spring Boot.
 *
 * ANALOGIA: piensa en esta clase como el "boton de encendido" de un electrodomestico.
 * Al pulsar {@code main(...)}, Spring Boot arranca el contenedor de beans, levanta el
 * servidor web embebido (Tomcat) y activa Actuator + Micrometer.
 *
 * PREGUNTA DE ALUMNO — "que hace @SpringBootApplication?"
 *   Es una meta-anotacion que combina tres cosas:
 *     - @Configuration      -> la clase puede declarar @Bean
 *     - @EnableAutoConfiguration -> Spring configura solo lo que detecta en classpath
 *     - @ComponentScan      -> escanea este paquete (y sub-paquetes) buscando @Component, @Service, etc.
 *
 * ANTES (Java 8 / Spring Boot 1.x) vs AHORA (Java 21 / Spring Boot 4.1):
 *   ANTES: se declaraba XML + web.xml + servlet container externo.
 *   AHORA: una sola clase con main() y Tomcat embebido. Cero XML.
 */
@SpringBootApplication
public class ActuatorMicrometerApplication {

    /**
     * Metodo principal — JVM lo invoca al ejecutar {@code java -jar app.jar}.
     * <p>
     * PALABRAS CLAVE:
     * - {@code public static void main(String[] args)}: convencion Java para arrancar programas.
     * - {@code static}: no requiere instancia de la clase para invocarse.
     * - {@code String[] args}: argumentos de linea de comandos (por ejemplo --server.port=9090).
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) arranca el contexto, levanta Tomcat y registra endpoints.
        SpringApplication.run(ActuatorMicrometerApplication.class, args);
    }
}
