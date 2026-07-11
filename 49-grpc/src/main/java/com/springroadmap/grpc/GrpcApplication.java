package com.springroadmap.grpc;

// PREGUNTA DE ALUMNO - "que es 'import'?"
//   Es como decirle a Java "voy a usar esta clase que vive en otro paquete".
//   Ahorra tener que escribir el nombre completo cada vez.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del modulo 49 - gRPC.
 *
 * <p>ANALOGIA: si Spring Boot fuese un edificio, esta clase es la puerta principal
 * por la que entra el arranque. Sin ella no hay contexto Spring.
 *
 * <p>Este modulo demuestra el patron gRPC de forma SIMPLIFICADA: en vez de generar
 * codigo con {@code protoc} (que requiere el binario del compilador de protobuf),
 * simulamos el servicio con un componente Spring puro. El README documenta el
 * contrato {@code .proto} equivalente y como escalar el demo a un servidor gRPC real.
 *
 * <p>ANTES vs AHORA (Java 8 vs Java 21) para el arranque de Spring Boot:
 * <pre>
 * // ANTES (Java 8) - identico, el codigo no cambio entre versiones
 * public static void main(String[] args) {
 *     SpringApplication.run(GrpcApplication.class, args);
 *   }
 * // AHORA (Java 21) - identico. La modernizacion ocurre en el resto del codigo
 * // (records, var, switch expressions, pattern matching).
 * </pre>
 */
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan.
// Le dice a Spring "escanea este paquete y sus subpaquetes buscando @Component,
// @Service, @RestController, etc., y registralos como beans".
@SpringBootApplication
public class GrpcApplication {

    /**
     * Metodo main - punto de entrada estandar de cualquier programa Java.
     *
     * @param args argumentos de linea de comando (ej: --server.port=9000)
     */
    public static void main(final String[] args) {
        // SpringApplication.run: arranca el contexto de Spring, levanta Tomcat embebido,
        // registra los beans y deja el proceso escuchando peticiones HTTP.
        SpringApplication.run(GrpcApplication.class, args);
    }
}
