package com.springroadmap.files;

// 'import' hace visibles clases de otros paquetes sin escribir su nombre completo.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del módulo 16 (Subida y descarga de archivos).
 *
 * <p>Analogía: esta clase es el "botón de encendido" de la aplicación.
 * Al ejecutarla Spring Boot arranca el contenedor de dependencias,
 * levanta un Tomcat embebido en el puerto 8080 y registra los
 * controladores REST (aquí, {@code FileController}).</p>
 *
 * <p>{@code @SpringBootApplication} es una meta-anotación que combina tres:
 * <ul>
 *   <li>{@code @Configuration} — declara esta clase como fuente de beans.</li>
 *   <li>{@code @EnableAutoConfiguration} — activa la magia de Spring Boot
 *       (auto-configura Tomcat, Jackson, MultipartResolver, etc.).</li>
 *   <li>{@code @ComponentScan} — escanea el paquete
 *       {@code com.springroadmap.files} en busca de {@code @Service},
 *       {@code @RestController}, etc.</li>
 * </ul></p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // Antes (Java 8): no hay diferencia real, la anotación existe desde Boot 1.x.
 * // La modernización es sintáctica en OTROS archivos (records, var, etc.).
 * </pre>
 */
@SpringBootApplication
public class FilesApplication {

    /**
     * Punto de entrada estándar de Java. La JVM llama {@code main} al ejecutar
     * {@code java -jar subida-archivos-1.0.0.jar}.
     *
     * <p>Palabras clave:
     * <ul>
     *   <li>{@code public} — visible desde fuera del paquete (necesario para la JVM).</li>
     *   <li>{@code static} — pertenece a la CLASE, no a una instancia; la JVM
     *       no puede crear objetos antes de arrancar el contenedor.</li>
     *   <li>{@code void} — no devuelve nada.</li>
     *   <li>{@code String[] args} — argumentos de línea de comando (ej: {@code --server.port=9090}).</li>
     * </ul></p>
     */
    public static void main(String[] args) {
        // SpringApplication.run: crea el ApplicationContext, escanea beans y arranca Tomcat.
        SpringApplication.run(FilesApplication.class, args);
    }
}
