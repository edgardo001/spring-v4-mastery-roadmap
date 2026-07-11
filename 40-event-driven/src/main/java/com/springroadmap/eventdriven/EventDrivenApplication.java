package com.springroadmap.eventdriven;

// 'import' trae clases de otras librerías para usarlas por su nombre corto.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal (bootstrap) del módulo 40 — Event-Driven.
 *
 * Analogía del mundo real:
 *   Piensa en una oficina de correos. Cuando alguien paga un servicio (evento),
 *   se disparan varias acciones en paralelo: se envía un email, se emite una
 *   factura, y se guarda una estadística. En vez de que el cajero llame uno a uno,
 *   grita "¡PAGO RECIBIDO!" y los tres empleados responden por su cuenta.
 *
 * Eso es exactamente lo que hace `ApplicationEventPublisher` + `@EventListener`.
 *
 * @SpringBootApplication combina tres anotaciones:
 *   - @Configuration (esta clase puede definir @Bean).
 *   - @EnableAutoConfiguration (Spring configura beans automáticamente por classpath).
 *   - @ComponentScan (busca @Component/@Service/@Controller en este paquete y subpaquetes).
 */
@SpringBootApplication
public class EventDrivenApplication {

    /**
     * Método `main` — punto de entrada del programa Java.
     * `public static void main(String[] args)` es la firma OBLIGATORIA de arranque.
     *   - `public`  → visible desde fuera.
     *   - `static`  → no requiere instancia (`new`) para ejecutarse.
     *   - `void`    → no retorna nada.
     *   - `String[] args` → argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        // SpringApplication.run() arranca el contenedor Spring y expone Tomcat en 8080.
        SpringApplication.run(EventDrivenApplication.class, args);
    }
}
