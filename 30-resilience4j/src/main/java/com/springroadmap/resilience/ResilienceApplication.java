package com.springroadmap.resilience;

// Import de la anotación que marca la clase principal de Spring Boot.
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo 30 — Resilience4j.
 *
 * <p><b>Analogía del mundo real:</b> un "disyuntor eléctrico" en tu casa. Si un
 * electrodoméstico falla muchas veces, el disyuntor "abre" el circuito para
 * proteger el resto de la instalación. Cuando pasa un rato, cierra de nuevo
 * a ver si el electrodoméstico ya se recuperó.</p>
 *
 * <p><b>Qué demostramos:</b> combinar {@code Retry} (reintentar N veces con
 * pausa) + {@code CircuitBreaker} (abrir el circuito si sigue fallando) para
 * blindar una llamada a un servicio flaky.</p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES: main clásico Java 8, sin cambios en Java 21 — sigue igual.
 * public static void main(String[] args) { SpringApplication.run(App.class, args); }
 * </pre>
 * En este archivo NO usamos records ni pattern matching — es solo bootstrap.
 */
@SpringBootApplication  // Le dice a Spring: "escanea este paquete y sub-paquetes, autoconfigura todo".
public class ResilienceApplication {

    /**
     * Arranca el contenedor de Spring Boot (Tomcat embebido incluido).
     * @param args argumentos de línea de comandos que Spring puede procesar
     *             (por ejemplo {@code --server.port=9090}).
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) es equivalente a "new SpringApplication(...).run(...)".
        // Levanta el ApplicationContext, crea los beans, y arranca Tomcat en el puerto 8080.
        SpringApplication.run(ResilienceApplication.class, args);
    }
}
