package com.springroadmap.oauth2;

// `import` = traer clases de otros paquetes para poder usarlas por nombre corto.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación módulo 34 (OAuth2 Resource Server + JWT).
 *
 * Analogía del mundo real:
 *   Piensa en un edificio con guardias en la entrada. Antes (módulo 13) el
 *   guardia te reconocía por tu tarjeta magnética (sesión + cookie). Ahora
 *   (OAuth2) tú traes un "pasaporte" firmado (JWT) que cualquier guardia puede
 *   validar leyendo la firma, sin llamar a la oficina central.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   // ANTES: mismo código; SpringApplication.run existe desde Spring Boot 1.x.
 *   // AHORA: el compilador admite `var`, records, switch expressions, etc.,
 *   //        pero aquí no cambia la firma. Lo moderno vive en las otras clases.
 */
@SpringBootApplication // Meta-anotación: @Configuration + @EnableAutoConfiguration + @ComponentScan.
public class Application {

    /**
     * `main` es el método que la JVM invoca al ejecutar `java -jar ...`.
     * `public`  = accesible desde fuera.
     * `static`  = pertenece a la clase, no a una instancia (no hay `new Application()`).
     * `void`    = no retorna nada.
     * `String[] args` = argumentos pasados por línea de comandos.
     */
    public static void main(String[] args) {
        // Arranca el contexto Spring, escanea @Components y expone el servidor web.
        SpringApplication.run(Application.class, args);
    }
}
