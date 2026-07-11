package com.springroadmap.featureflags;

// 'import' trae clases externas al alcance de este archivo.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Clase principal (entry point) del módulo 46 — Feature Flags.
 *
 * ANALOGÍA DEL MUNDO REAL:
 * Un "Feature Flag" es como el interruptor de la luz del pasillo. La instalación
 * eléctrica (código) siempre está lista, pero decides si la luz está prendida o
 * apagada sin necesidad de rehacer el cableado (redeploy).
 *
 * PROPÓSITO:
 * - Levanta el contexto de Spring Boot.
 * - Activa el escaneo de @ConfigurationProperties (evita anotar cada POJO con
 *   @Component).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   // Antes: main clásico
 *   public static void main(String[] args) {
 *       SpringApplication.run(FeatureFlagsApplication.class, args);
 *   }
 *   // Ahora: mismo main, pero Boot 4 usa Java 21 (records, var, pattern matching disponibles).
 */
@SpringBootApplication // Combo de @Configuration + @EnableAutoConfiguration + @ComponentScan.
@ConfigurationPropertiesScan // Habilita autodiscovery de clases @ConfigurationProperties.
public class FeatureFlagsApplication {

    /**
     * main(): método estático que la JVM invoca al arrancar el JAR.
     * 'static' significa que pertenece a la clase, no a una instancia.
     */
    public static void main(String[] args) {
        SpringApplication.run(FeatureFlagsApplication.class, args);
    }
}
