package com.springroadmap.datarest;

// 'import' trae clases de otros paquetes. Sin esto habria que escribir el nombre completo cada vez.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 *
 * <p>Analogia: es como el interruptor general de una casa. Al encenderlo,
 * Spring "prende" todos los beans (controladores, repositorios, config)
 * de forma automatica.</p>
 *
 * <p>Modulo 54 — Spring Data REST: expone los repositorios JPA como
 * endpoints REST HAL sin escribir ni Controllers ni Services.</p>
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 * // Java 8 clasico:
 * public static void main(String[] args) {
 *     SpringApplication app = new SpringApplication(SpringDataRestApplication.class);
 *     app.run(args);
 * }
 *
 * // Java 21 idiomatico (una sola linea):
 * SpringApplication.run(SpringDataRestApplication.class, args);
 * </pre>
 */
// @SpringBootApplication combina 3 anotaciones: @Configuration + @EnableAutoConfiguration + @ComponentScan.
// Le dice a Spring: "arranca todo automaticamente y escanea este paquete".
@SpringBootApplication
public class SpringDataRestApplication {

    /**
     * Metodo main: punto de entrada estandar de cualquier programa Java.
     * 'public' = accesible desde afuera. 'static' = no necesita instancia.
     * 'void' = no retorna nada. 'String[] args' = argumentos de linea de comandos.
     */
    public static void main(String[] args) {
        // SpringApplication.run arranca el contexto de Spring, un servidor Tomcat embebido
        // en el puerto 8080, escanea @Entity, @Repository, @Controller, etc.
        SpringApplication.run(SpringDataRestApplication.class, args);
    }
}
