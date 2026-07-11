package com.springroadmap.securityadv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del modulo 33 — Security Avanzado.
 *
 * <p><b>Proposito:</b> demostrar <i>Method Security</i> con
 * {@code @PreAuthorize} y {@code @PostAuthorize}, en lugar de la
 * clasica seguridad basada solo en URL.</p>
 *
 * <p><b>Analogia:</b> el modulo 13 puso un guardia en la puerta del
 * edificio (URL-based). Aqui ademas ponemos un guardia dentro de cada
 * OFICINA (metodo del servicio) que revisa el gafete antes de dejarte
 * ejecutar una accion sensible (borrar, leer documento ajeno, etc.).</p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES: clase principal larga con XML de contexto
 * //   public class App {
 * //       public static void main(String[] args) {
 * //           new ClassPathXmlApplicationContext("beans.xml");
 * //       }
 * //   }
 *
 * // AHORA: una sola anotacion @SpringBootApplication arranca todo.
 * </pre>
 */
@SpringBootApplication
public class SecurityAvanzadoApplication {

    /**
     * Metodo main — arranca el contenedor de Spring Boot.
     *
     * <p>Palabras clave:
     * <ul>
     *   <li>{@code public static void main(String[] args)} — firma exigida
     *       por la JVM para ejecutar la clase.</li>
     *   <li>{@code SpringApplication.run} — bootstrap del ApplicationContext.</li>
     * </ul>
     */
    public static void main(String[] args) {
        SpringApplication.run(SecurityAvanzadoApplication.class, args);
    }
}
