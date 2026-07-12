package com.springroadmap.aws;

// `import` = trae una clase desde otro paquete para poder usar su nombre corto.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del módulo 53 — Integración con AWS (S3, SQS, Secrets Manager).
 *
 * <p><b>Analogía del mundo real:</b> AWS es como un centro comercial gigante donde cada tienda es
 * un servicio (S3 = bodega de cajas, SQS = tablón de mensajes, Secrets Manager = caja fuerte).
 * Esta aplicación es un cliente que camina al centro comercial con una lista de compras y usa
 * la "tarjeta de identificación" (credenciales) para entrar a cada tienda.
 *
 * <p><b>Propósito:</b> demostrar cómo interactuar con servicios AWS desde Spring Boot 4 usando
 * el <b>AWS SDK v2</b> directamente (sin spring-cloud-aws, que aún no soporta Boot 4.x — mismo
 * problema documentado para Spring Cloud Config en el módulo 29).
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b>
 * <pre>
 *   // ANTES (Java 8) — clase con main "clásico":
 *   public class CloudAwsApplication {
 *       public static void main(String[] args) {
 *           SpringApplication.run(CloudAwsApplication.class, args);
 *       }
 *   }
 *
 *   // AHORA (Java 21) — mismo main pero puede convivir con records, var, switch expressions
 *   // y virtual threads. La firma del main no cambia: sigue siendo `public static void main`.
 * </pre>
 */
// `@SpringBootApplication` = combo de @Configuration + @EnableAutoConfiguration + @ComponentScan.
// Le dice a Spring Boot: "arranca el contexto y escanea este paquete + subpaquetes".
@SpringBootApplication
public class CloudAwsApplication {

    /**
     * Punto de entrada de la JVM. `public static void main(String[] args)` es la firma
     * obligatoria que la JVM busca al ejecutar `java -jar cloud-aws-1.0.0.jar`.
     *
     * <p>Palabras clave explicadas:
     * <ul>
     *   <li><b>public</b>: visible desde cualquier clase (la JVM debe poder llamarlo).</li>
     *   <li><b>static</b>: pertenece a la CLASE, no a una instancia. La JVM no crea un objeto
     *       antes de llamarlo — llama directo a `CloudAwsApplication.main(...)`.</li>
     *   <li><b>void</b>: no retorna nada.</li>
     *   <li><b>String[] args</b>: argumentos de línea de comandos (ej: `java -jar app.jar --server.port=9000`).</li>
     * </ul>
     */
    public static void main(String[] args) {
        // `SpringApplication.run(...)` arranca el contexto de Spring:
        //   1. Escanea @Component/@Service/@Configuration.
        //   2. Aplica autoconfiguración (Tomcat embebido, Jackson, etc.).
        //   3. Levanta el servidor web en el puerto configurado.
        SpringApplication.run(CloudAwsApplication.class, args);
    }
}
