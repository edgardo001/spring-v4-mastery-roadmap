package com.springroadmap.dtos;

// PREGUNTA DE ALUMNO - "que es 'import'?"
//   Java exige declarar antes de usar. 'import' es como decir "voy a usar
//   esta clase de otra carpeta (paquete)". Sin import habria que escribir
//   el nombre completo: org.springframework.boot.SpringApplication.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (entry point) del modulo 09.
 *
 * Analogia del mundo real:
 *   Esta clase es la "puerta de entrada del edificio". SpringApplication.run(...)
 *   enciende las luces, abre las oficinas (beans), configura la recepcion
 *   (controllers) y deja el edificio funcionando en el puerto 8080.
 *
 * ANTES (Java 8 + Spring 4.x):
 *   - Se necesitaba web.xml + varias clases XML de configuracion.
 *   - El servidor Tomcat era externo (WAR desplegado en una carpeta 'webapps').
 *
 * AHORA (Java 21 + Spring Boot 4.1):
 *   - Un unico @SpringBootApplication reemplaza toda la config XML.
 *   - Tomcat viene EMBEBIDO dentro del JAR: 'java -jar' basta para arrancar.
 */
@SpringBootApplication
public class MapeoDtosMapstructApplication {

    // PREGUNTA DE ALUMNO - "que es 'public static void main(String[] args)'?"
    //   Es el metodo que la JVM ejecuta primero al arrancar el programa.
    //   'static' = no hace falta crear un objeto, se llama desde la clase.
    //   'String[] args' = argumentos pasados por linea de comandos.
    public static void main(String[] args) {
        SpringApplication.run(MapeoDtosMapstructApplication.class, args);
    }
}
