// Package raíz del módulo (debe coincidir con la carpeta física del proyecto).
// PREGUNTA DE ALUMNO — "¿qué es un 'package' en Java?"
//   Es como una carpeta lógica que agrupa clases relacionadas y evita
//   choques de nombres (dos clases 'Utils' pueden coexistir en packages distintos).
package com.springroadmap.async;

// 'import' trae clases desde otras librerías para poder usarlas por su nombre corto.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 *
 * <p>Módulo 21 — ejecución asíncrona: mostramos cómo un método marcado con
 * {@code @Async} corre en un hilo SEPARADO del hilo HTTP, evitando bloquear al
 * usuario. La anotación {@code @EnableAsync} vive en {@code AsyncConfig}
 * (separada por buena práctica de "una responsabilidad por clase").
 *
 * <p>Analogía: esta clase es el "interruptor general" del edificio. No hace
 * el trabajo, solo enciende la luz para que TODOS los demás componentes
 * (@Service, @RestController, @Configuration) puedan trabajar.
 *
 * <hr>
 * <b>ANTES (Java 8) vs AHORA (Java 21)</b>
 * <pre>
 * // ANTES: para lanzar una tarea en paralelo había que crear el Thread a mano.
 * new Thread(new Runnable() {
 *     public void run() { enviarCorreo("ada@x.com"); }
 * }).start();
 *
 * // AHORA: se declara el método con @Async y Spring lo despacha al pool.
 * emailService.sendEmail("ada@x.com");   // Retorna CompletableFuture&lt;String&gt;
 * </pre>
 */
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
public class AsyncApplication {

    // main: método que la JVM ejecuta al lanzar 'java -jar async-1.0.0.jar'.
    //   - 'public'  → visible desde afuera de la clase.
    //   - 'static'  → NO necesita instancia (this) para llamarse.
    //   - 'void'    → no retorna nada.
    //   - String[]  → arreglo de argumentos de línea de comando.
    public static void main(String[] args) {
        // Arranca el contenedor Spring: crea beans, inyecta dependencias,
        // levanta Tomcat en el puerto 8080, y queda escuchando peticiones.
        SpringApplication.run(AsyncApplication.class, args);
    }
}
