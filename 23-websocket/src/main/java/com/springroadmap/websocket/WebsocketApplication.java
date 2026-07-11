package com.springroadmap.websocket;

// Import de la clase que arranca todo el contenedor Spring Boot.
// SpringApplication es el "motor de arranque": lee la configuración,
// crea el ApplicationContext, registra los beans y levanta Tomcat.
import org.springframework.boot.SpringApplication;

// @SpringBootApplication es un "combo" de tres anotaciones:
//   - @Configuration          -> la clase puede declarar @Bean
//   - @EnableAutoConfiguration -> Spring intenta configurar todo por convención
//   - @ComponentScan          -> escanea este paquete y subpaquetes buscando @Component/@Service/@Controller/@Configuration
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del módulo 23 — WebSocket con STOMP.
 *
 * ANALOGÍA DEL MUNDO REAL:
 *   Imagina una radio ciudadana (walkie-talkie).
 *   - HTTP tradicional: llamas por teléfono, preguntas, cuelgas. Cada mensaje = una llamada nueva.
 *   - WebSocket: abres el walkie una vez y ambos se hablan cuando quieran, sin marcar de nuevo.
 *   - STOMP: es el "protocolo de radio" (10-4, cambio y fuera) que se usa DENTRO del walkie
 *            para saber a qué canal (/topic/messages) va cada mensaje y quién lo envía.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES:
 *     public static void main(String[] args) {
 *         SpringApplication.run(WebsocketApplication.class, args);
 *     }
 *   AHORA (idéntico — el método main no cambió con Java 21).
 *   Los cambios modernos de este módulo están en el record ChatMessage.
 */
@SpringBootApplication
public class WebsocketApplication {

    /**
     * Método main — punto de entrada de toda aplicación Java.
     *
     * PREGUNTA DE ALUMNO — "¿qué es 'public static void main(String[] args)'?"
     *   Es la firma FIJA que la JVM (Java Virtual Machine) busca para arrancar un programa.
     *     - public : accesible desde fuera de la clase.
     *     - static : no necesita instancia (no hay que hacer 'new WebsocketApplication()').
     *     - void   : no devuelve nada.
     *     - String[] args : arreglo con los argumentos que pasas por consola.
     *
     * @param args argumentos de línea de comandos (p.ej. --server.port=9090).
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) hace todo el trabajo pesado:
        //   1) Crea el ApplicationContext.
        //   2) Detecta que hay spring-boot-starter-websocket en el classpath.
        //   3) Levanta el broker STOMP in-memory declarado en WebSocketConfig.
        //   4) Arranca Tomcat en el puerto 8080.
        SpringApplication.run(WebsocketApplication.class, args);
    }
}
