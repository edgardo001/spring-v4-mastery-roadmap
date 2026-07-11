package com.springroadmap.security;

// 'import' trae clases de otros paquetes al alcance del archivo actual.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (entry point) de la aplicación Spring Boot.
 *
 * <p><b>Propósito:</b> arrancar el contexto de Spring y dejar corriendo el
 * servidor Tomcat embebido con Spring Security habilitado.</p>
 *
 * <p><b>Analogía:</b> es como el interruptor general de un edificio: al
 * encenderlo, todos los circuitos (beans) se energizan y quedan listos
 * para atender visitas (peticiones HTTP). El guardia de seguridad
 * (Spring Security) toma su puesto en la entrada.</p>
 *
 * <p><b>Palabras clave:</b>
 * <ul>
 *   <li>{@code @SpringBootApplication} — anotación compuesta que activa
 *       autoconfiguración, escaneo de componentes y configuración.</li>
 *   <li>{@code public static void main} — método estándar de Java para
 *       arrancar cualquier programa desde la línea de comandos.</li>
 * </ul>
 * </p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES (Java 8 + Spring 3/4 con web.xml):
 * //   Se declaraban filtros de seguridad en web.xml y
 * //   se usaba XML para configurar Spring Security.
 * // AHORA (Java 21 + Spring Boot 4):
 * //   Una sola clase con @SpringBootApplication arranca todo,
 * //   y la seguridad se declara con un @Bean SecurityFilterChain.
 * </pre>
 */
@SpringBootApplication
public class SeguridadBasicaApplication {

    /**
     * Punto de entrada del JVM.
     *
     * @param args argumentos pasados por línea de comandos (no se usan aquí)
     */
    public static void main(String[] args) {
        // SpringApplication.run: crea el ApplicationContext, arranca Tomcat
        // embebido en el puerto 8080 y publica los beans (incluida la cadena
        // de filtros de Spring Security).
        SpringApplication.run(SeguridadBasicaApplication.class, args);
    }
}
