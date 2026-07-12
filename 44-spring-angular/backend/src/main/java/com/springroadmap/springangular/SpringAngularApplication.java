package com.springroadmap.springangular;

// Import de la anotacion @SpringBootApplication que combina @Configuration,
// @EnableAutoConfiguration y @ComponentScan en una sola.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (entry point) del backend del modulo 44.
 *
 * ANALOGIA: Es el "arranque del motor" de la aplicacion. Como girar la llave
 * de un auto: enciende todo (servidor web embebido, beans, seguridad) para
 * que Angular pueda hablar con Spring via HTTP.
 *
 * ANTES (Java 8 + Spring 4 + Angular 1.x / AngularJS):
 *   - Se desplegaba un WAR con backend + templates JSP + AngularJS embebido.
 *   - `web.xml` gigante, `applicationContext.xml`, DispatcherServlet manual.
 *   - Un unico artefacto pesado.
 *
 * AHORA (Java 21 + Spring Boot 4.1 + Angular v22):
 *   - Backend JAR autonomo con Tomcat embebido.
 *   - Frontend independiente compilado a estaticos, servido por nginx.
 *   - Contrato REST + JSON entre ambos.
 *
 * PREGUNTA DE ALUMNO — "por que la clase se llama Application?"
 *   Es convencion Spring Boot: la clase con @SpringBootApplication vive en el
 *   paquete raiz y suele llamarse <Nombre>Application. Spring escanea desde
 *   ese paquete hacia abajo buscando @Component, @Service, @RestController, etc.
 */
@SpringBootApplication
public class SpringAngularApplication {

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
        //   2. Escanea @Component/@Service/@RestController.
        //   3. Levanta Tomcat embebido en el puerto 8080.
        SpringApplication.run(SpringAngularApplication.class, args);
    }
}
