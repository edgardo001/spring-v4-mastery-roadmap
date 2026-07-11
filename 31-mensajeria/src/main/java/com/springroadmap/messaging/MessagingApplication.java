package com.springroadmap.messaging;

// 'import' trae clases de otros paquetes para poder usarlas sin escribir la ruta completa.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (bootstrap) del modulo 31 - Mensajeria.
 *
 * Analogia del mundo real:
 *   Piensa en esta clase como el INTERRUPTOR GENERAL de un edificio de oficinas.
 *   Cuando lo enciendes (main), se prenden todas las luces, ascensores, cafeteras
 *   y sistemas de aire acondicionado. Spring Boot hace lo mismo: al arrancar,
 *   detecta y prepara automaticamente el servidor web, los @Controller, los
 *   @Service, y el BUS DE EVENTOS interno (ApplicationEventPublisher).
 *
 * ANTES (Java 8, Spring 4 clasico):
 *   Tenias que declarar @Configuration + @EnableAutoConfiguration + @ComponentScan
 *   por separado, y ademas un web.xml o applicationContext.xml. Mucho boilerplate.
 *
 * AHORA (Java 21, Spring Boot 4.1.0):
 *   Una sola anotacion @SpringBootApplication agrupa las 3 anteriores. Ni XML.
 */
@SpringBootApplication
// PREGUNTA DE ALUMNO - "¿Que hace @SpringBootApplication exactamente?"
// R: Es un meta-anotacion que combina:
//    - @Configuration        (esta clase define beans).
//    - @EnableAutoConfiguration (Spring configura por defecto lo que detecta en el classpath).
//    - @ComponentScan        (busca @Component/@Service/@Controller en este paquete y sub-paquetes).
public class MessagingApplication {

    /**
     * Metodo main - punto de entrada de cualquier programa Java.
     *
     * Palabras clave:
     *   - public : cualquiera puede invocarlo (la JVM lo necesita).
     *   - static : NO hace falta crear un objeto MessagingApplication para llamarlo.
     *   - void   : no retorna nada.
     *   - String[] args : arreglo de argumentos que pasa la linea de comandos.
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) arranca el contenedor Spring:
        //   1. Escanea el classpath.
        //   2. Levanta Tomcat embebido en el puerto 8080.
        //   3. Registra el bus de eventos (ApplicationEventPublisher) como bean.
        SpringApplication.run(MessagingApplication.class, args);
    }
}
