package com.springroadmap.integration;

// `import` = trae clases de otros paquetes para poder usarlas aquí.
// SpringApplication arranca el contexto de Spring y el servidor Tomcat embebido.
import org.springframework.boot.SpringApplication;
// @SpringBootApplication combina: @Configuration + @EnableAutoConfiguration + @ComponentScan.
import org.springframework.boot.autoconfigure.SpringBootApplication;
// @IntegrationComponentScan permite descubrir interfaces marcadas con @MessagingGateway.
// SIN esta anotación, Spring NO genera el proxy dinámico para el OrderGateway.
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

/**
 * Punto de entrada del módulo 51 - Spring Integration.
 *
 * <h2>Analogía</h2>
 * Piensa en Spring Integration como una <b>red de tuberías industriales</b>:
 * cada `MessageChannel` es un tubo, cada `@ServiceActivator` es una válvula/máquina
 * que procesa el líquido (mensaje), y el `IntegrationFlow` es el plano de la fábrica
 * que declara <i>cómo</i> se conectan.
 *
 * <h2>ANTES (Java 8 + Spring 3/4) vs AHORA (Java 21 + Spring 6/Boot 4)</h2>
 * <pre>
 * // ANTES: main con clase pública y método estático
 * public class App {
 *     public static void main(String[] args) {
 *         SpringApplication.run(App.class, args);
 *     }
 * }
 *
 * // AHORA (idéntico en sintaxis, pero se ejecuta sobre JDK 21 con virtual threads,
 * // pattern matching y records disponibles en toda la app).
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿Por qué necesito @IntegrationComponentScan si ya tengo
 * @SpringBootApplication?" R: Porque los @MessagingGateway son <b>interfaces</b>
 * (no clases anotadas con @Component), y el ComponentScan estándar no las detecta.
 * Esta anotación específica escanea interfaces gateway y genera un proxy dinámico
 * que implementa la interfaz por ti.</p>
 */
@SpringBootApplication
@EnableIntegration          // Habilita infraestructura de Spring Integration (canales globales, error channel).
@IntegrationComponentScan   // Escanea @MessagingGateway en este paquete y subpaquetes.
public class SpringIntegrationApplication {

    /**
     * Método `main` = punto de entrada del programa.
     * `public` = accesible desde fuera. `static` = pertenece a la clase, no a una instancia.
     * `void` = no retorna nada. `String[] args` = argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        // SpringApplication.run levanta el contexto, escanea beans y arranca Tomcat en 8080.
        SpringApplication.run(SpringIntegrationApplication.class, args);
    }
}
