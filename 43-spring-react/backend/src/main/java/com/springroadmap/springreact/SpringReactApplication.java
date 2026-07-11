package com.springroadmap.springreact;

// Import de la anotacion @SpringBootApplication que combina @Configuration,
// @EnableAutoConfiguration y @ComponentScan en una sola.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (entry point) del backend del modulo 43.
 *
 * ANALOGIA: Es el "arranque del motor" de la aplicacion. Como girar la llave
 * de un auto: enciende todo lo demas (servidor web embebido, beans, seguridad).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES: public static void main(String[] args) { SpringApplication.run(App.class, args); }
 *   AHORA: identico (main sigue igual). Lo moderno esta en el resto del codigo (records,
 *          switch expressions, etc.).
 *
 * PREGUNTA DE ALUMNO — "por que la clase se llama Application?"
 *   Es convencion Spring Boot: la clase que tiene @SpringBootApplication vive
 *   en el paquete raiz y suele llamarse <Nombre>Application. Spring escanea
 *   desde ese paquete hacia abajo buscando @Component, @Service, etc.
 */
@SpringBootApplication
public class SpringReactApplication {

    /**
     * Metodo main: primer metodo que ejecuta la JVM.
     *
     * - `public`   = accesible desde fuera (la JVM lo llama).
     * - `static`   = no requiere instanciar la clase para invocarlo.
     * - `void`     = no retorna nada.
     * - `String[]` = argumentos de linea de comandos (ej. --server.port=9090).
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) arranca el contexto de Spring:
        //   1. Crea el ApplicationContext.
        //   2. Escanea @Component/@Service/@Controller.
        //   3. Levanta Tomcat embebido en el puerto 8080.
        SpringApplication.run(SpringReactApplication.class, args);
    }
}
