package com.springroadmap.jpa;

// `import` trae a este archivo la clase que vamos a usar. Sin `import` habría
// que escribir el nombre completo (paquete + clase) cada vez.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (bootstrap) de la aplicación Spring Boot del módulo 07.
 *
 * Analogía: piensa en esta clase como el "encendido" de un auto. Girar la
 * llave (ejecutar `main`) enciende el motor (Spring), y el motor a su vez
 * enciende todos los subsistemas (radio, aire, luces = beans/JPA/Hibernate).
 *
 * Palabras clave:
 * - `@SpringBootApplication`: anotación (la "arroba" `@` en Java se llama
 *   anotación; es metadata que Spring lee en tiempo de arranque). Combina
 *   tres cosas: `@Configuration` (permite declarar beans), `@EnableAutoConfiguration`
 *   (Spring adivina qué configurar mirando el classpath: como ve JPA e H2,
 *   configura un DataSource + EntityManagerFactory + Hibernate) y
 *   `@ComponentScan` (busca `@RestController`, `@Service`, `@Repository`
 *   en este paquete y sub-paquetes).
 * - `public static void main(String[] args)`: la firma clásica del punto de
 *   entrada de Java. `static` = no requiere instanciar la clase para llamarlo.
 */
@SpringBootApplication
public class JpaHibernateApplication {

    // PREGUNTA DE ALUMNO — "¿por qué el `main` está vacío? ¿Dónde está mi código?"
    //   Spring hace todo el trabajo: escanea, construye beans, levanta Tomcat
    //   embebido en el puerto 8080, prepara la base H2 en memoria y expone
    //   los endpoints. Tu código vive en los `@RestController`, `@Service`,
    //   `@Entity` y `@Repository`, que Spring encuentra automáticamente.
    public static void main(String[] args) {
        // `SpringApplication.run(...)` arranca el contexto. Devuelve un
        // `ConfigurableApplicationContext`, que aquí no usamos.
        SpringApplication.run(JpaHibernateApplication.class, args);
    }
}
