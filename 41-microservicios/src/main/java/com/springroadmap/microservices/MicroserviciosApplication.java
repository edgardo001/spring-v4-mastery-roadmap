package com.springroadmap.microservices;

// 'import' trae clases de otros paquetes para poder usarlas sin escribir el nombre completo.
import org.springframework.boot.SpringApplication;                 // Arranca la app Spring Boot.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Meta-anotacion "todo-en-uno".

/**
 * Modulo 41 - Microservicios (DEMO simplificado).
 *
 * PROPOSITO:
 *   Enseniar los conceptos de un ecosistema de microservicios (Service Discovery,
 *   Load Balancing Round-Robin y API Gateway) SIN depender de Spring Cloud, porque
 *   Spring Cloud (2024.x/2025.x) todavia no publica release compatible con
 *   Spring Boot 4.1.0 (ver MEMORY.md, modulo 29).
 *
 * ANALOGIA DEL MUNDO REAL:
 *   Imagina un centro comercial (Gateway) en cuya entrada hay un mapa (Service
 *   Registry) que dice donde queda cada tienda. Cuando un cliente entra pidiendo
 *   "quiero comprar zapatos", el guardia mira el mapa, ve que hay 3 sucursales de
 *   la zapateria, y lo manda a una distinta cada vez (Round-Robin) para que
 *   ninguna sucursal se sature.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   En este archivo no hay sintaxis moderna: {@code public static void main}
 *   se escribe igual en Java 8 y Java 21. Los archivos siguientes si contienen
 *   'var', method references, streams y bloques de comentario "ANTES vs AHORA".
 */
@SpringBootApplication // = @Configuration + @EnableAutoConfiguration + @ComponentScan.
public class MicroserviciosApplication {

    /**
     * Metodo main = punto de entrada de la JVM.
     *   'public'  -> visible desde cualquier paquete (la JVM lo necesita).
     *   'static'  -> pertenece a la clase, no a una instancia (no hace 'new').
     *   'void'    -> no devuelve nada.
     *   String[] args -> argumentos de linea de comandos (ej: --server.port=9090).
     */
    public static void main(String[] args) {
        SpringApplication.run(MicroserviciosApplication.class, args);
    }
}
