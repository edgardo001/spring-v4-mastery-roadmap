// "package" indica en qué carpeta lógica vive la clase.
// Convención: dominio invertido (com.springroadmap) + módulo (intro).
// La carpeta física DEBE coincidir con el package.
package com.springroadmap.intro;

// "import" hace disponibles clases de otros paquetes por su nombre corto.
// Sin import tendríamos que escribir "org.springframework.boot.SpringApplication..."
// cada vez que la usamos.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 *
 * PREGUNTA DE ALUMNO — "¿Qué es la arroba '@' en Java?"
 *   Se llama "anotación" (annotation). Es una etiqueta que agregas a
 *   clases, métodos o campos para darles un significado especial. No
 *   ejecuta código por sí sola: son OTROS componentes (Spring, JUnit,
 *   Lombok…) los que las LEEN y actúan en consecuencia.
 *
 * PREGUNTA DE ALUMNO — "¿Qué hace @SpringBootApplication?"
 *   Es una "meta-anotación" — una sola etiqueta que equivale a poner tres:
 *     1. @Configuration       -> esta clase puede declarar "beans" (objetos gestionados por Spring).
 *     2. @EnableAutoConfiguration -> Spring mira el classpath (las librerías
 *        que hay disponibles) y configura automáticamente lo necesario
 *        (aquí ve Tomcat en el classpath → arma un servidor web).
 *     3. @ComponentScan       -> Spring busca automáticamente en este
 *        paquete y subpaquetes clases marcadas como @Component, @Service,
 *        @RestController, etc., y las registra.
 *
 * PREGUNTA DE ALUMNO — "¿Qué es un 'bean'?"
 *   Un bean es simplemente un objeto Java cuya vida controla Spring
 *   (creación, inyección, destrucción). El Controller de más abajo, por
 *   ejemplo, es un bean: Spring lo instancia una sola vez y lo comparte.
 *
 * PREGUNTA DE ALUMNO — "¿Qué es 'classpath'?"
 *   Es la lista de carpetas y JARs donde Java busca clases al cargar. Si
 *   una librería está en el classpath, tu código puede usarla. Spring Boot
 *   se apoya en esto para "adivinar" configuraciones (si ve H2 en el
 *   classpath, configura una BD H2 automáticamente).
 */
@SpringBootApplication
public class IntroSpringApplication {

    /**
     * "main" es el método que la JVM ejecuta al lanzar la aplicación con
     * `java -jar intro-spring-1.0.0.jar`.
     *
     * Cada palabra clave del método:
     *   public       -> visible desde cualquier lugar.
     *   static       -> no necesita instancia de la clase para invocarse
     *                   (la JVM llama a main SIN hacer `new IntroSpringApplication()`).
     *   void         -> no devuelve ningún valor.
     *   String[] args -> arreglo con los argumentos que se le pasan al
     *                    programa por línea de comandos.
     *                    Ejemplo: `java -jar app.jar --server.port=9090`
     *                    → args = ["--server.port=9090"]
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) hace TODO el trabajo pesado:
        //   1. Crea el "ApplicationContext" (el gran contenedor de beans).
        //   2. Escanea @Component / @RestController / @Service en este paquete y subpaquetes.
        //   3. Aplica la autoconfiguración basada en el classpath.
        //   4. Levanta Tomcat embebido en el puerto configurado.
        //   5. Deja la app escuchando peticiones HTTP hasta que la mates con Ctrl+C.
        //
        // ¿Qué es "IntroSpringApplication.class"?
        //   Es una referencia a la propia clase (no una instancia). Spring la usa
        //   como "punto de anclaje" para saber desde qué paquete arrancar el
        //   escaneo de componentes.
        SpringApplication.run(IntroSpringApplication.class, args);
    }
}
