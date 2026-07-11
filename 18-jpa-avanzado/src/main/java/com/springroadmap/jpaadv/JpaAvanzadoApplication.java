package com.springroadmap.jpaadv;

// `import` = trae la clase a este archivo para no escribir el paquete completo.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap Spring Boot del módulo 18 — JPA avanzado.
 *
 * Analogía: si el módulo 07 era abrir la biblioteca (CRUD básico), este es
 * darle al bibliotecario una LUPA (proyecciones), un CATÁLOGO CRUZADO
 * (@EntityGraph) y un ÍNDICE POR TOMOS (paginación).
 *
 * Palabras clave:
 * - `@SpringBootApplication`: la "arroba" `@` en Java se llama ANOTACIÓN.
 *   Combina `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
 * - `public static void main`: punto de entrada estándar de Java. `static`
 *   significa que no hay que instanciar la clase para invocarlo.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 *   // ANTES: XML gigante (applicationContext.xml) + web.xml.
 *   // AHORA: una anotación y un `main`. Todo autoconfigurado.
 * </pre>
 */
@SpringBootApplication
public class JpaAvanzadoApplication {

    // PREGUNTA DE ALUMNO — "¿por qué `main` está casi vacío?"
    //   Porque Spring hace todo: escanea @RestController, @Entity,
    //   @Repository; levanta Tomcat embebido; crea H2 en memoria; carga
    //   data.sql; expone /api/products.
    public static void main(String[] args) {
        SpringApplication.run(JpaAvanzadoApplication.class, args);
    }
}
