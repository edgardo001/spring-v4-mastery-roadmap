package com.springroadmap.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * GatewayApplication — punto de entrada del API Gateway simulado.
 *
 * Analogía del mundo real:
 *   Un gateway es como la recepción de un edificio corporativo. Todo el
 *   que quiera entrar (request HTTP) pasa primero por recepción, muestra
 *   su identificación (IP), y recepción decide a qué oficina (backend)
 *   dirigirlo. Además, controla que nadie entre más de X veces por
 *   segundo (rate limit).
 *
 * ¿Por qué un gateway simulado?
 *   Spring Cloud Gateway (el real) todavía vive en Spring Boot 3.x. Boot
 *   4.1.0 aún no tiene release de Cloud. Implementamos el patrón manualmente
 *   con Spring MVC + OncePerRequestFilter + RestClient.
 *
 * PREGUNTA DE ALUMNO — "¿Qué es @SpringBootApplication?"
 *   Es 3 anotaciones en una: @Configuration + @EnableAutoConfiguration
 *   + @ComponentScan. Le dice a Spring: "arranca el contexto, escanea
 *   este paquete y todos sus subpaquetes buscando @Component/@Service/@RestController".
 *
 * PREGUNTA DE ALUMNO — "¿Qué es @ConfigurationPropertiesScan?"
 *   Escanea el classpath buscando clases anotadas con
 *   @ConfigurationProperties y las registra como beans (sin necesidad
 *   de anotar cada una con @Component).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - ANTES: public static void main(String[] args) { ... } (idéntico).
 *   - AHORA: la firma no cambió. Java 21 permite `void main()` sin args
 *     como preview, pero Spring Boot aún requiere la firma clásica.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class GatewayApplication {

    /**
     * main — arranca el contexto de Spring Boot.
     * SpringApplication.run levanta Tomcat embebido en el puerto 8080.
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
