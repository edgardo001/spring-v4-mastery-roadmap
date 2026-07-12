package com.springroadmap.batch;

// 'import' trae clases desde otros paquetes. Sin esto no podríamos referenciarlas.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la app - Módulo 50 (Spring Batch).
 *
 * Analogía del mundo real:
 *   Piensa en una "planta de correo nocturna": llegan miles de sobres (filas del CSV),
 *   un lector abre uno a uno, un validador descarta los rotos, y un escritor los mete
 *   en cajas de 10 (el "chunk") antes de sellarlas y despacharlas a la BD.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - Antes: una clase Main + XML applicationContext.xml enorme.
 *   - Ahora: una sola clase con @SpringBootApplication levanta TODO
 *            (autoconfig de web, JPA, batch, JobRepository, JobLauncher).
 *
 * PREGUNTA DE ALUMNO — "¿por qué la clase se llama 'Application'?"
 *   Convención de Spring Boot: la clase con @SpringBootApplication es el
 *   punto de entrada del programa. El nombre es solo convención, podría ser
 *   cualquier otro, pero todos los proyectos Spring lo llaman así.
 */
@SpringBootApplication // = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class SpringBatchApplication {

    /**
     * Método main: es la puerta de entrada de cualquier programa Java.
     * 'public static void main(String[] args)' es la firma OBLIGATORIA que la JVM
     * busca al invocar `java -jar spring-batch-1.0.0.jar`.
     *
     * @param args argumentos de línea de comandos (no los usamos aquí).
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) hace 3 cosas:
        //   1) crea el ApplicationContext,
        //   2) escanea beans (@Component, @Service, @Configuration, ...),
        //   3) arranca el servidor Tomcat embebido en el puerto 8080.
        SpringApplication.run(SpringBatchApplication.class, args);
    }
}
