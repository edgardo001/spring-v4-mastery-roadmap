package com.springroadmap.grpc.controller;

import com.springroadmap.grpc.grpc.HelloGrpcServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Puente REST -> gRPC.
 *
 * <p>ANALOGIA: es el "traductor bilingue" que atiende a los clientes REST (HTTP/JSON)
 * y les pasa las peticiones al servicio gRPC interno. Muy comun en produccion:
 * la API publica es REST (facil para navegadores/moviles) pero por dentro los
 * microservicios se comunican por gRPC (mas rapido, tipado, binario).
 *
 * <p>ANTES vs AHORA:
 * <pre>
 * // ANTES (Java 8) - anotaciones separadas para el mismo endpoint
 * &#64;RequestMapping(value = "/api/hello", method = RequestMethod.GET)
 * public Map&lt;String, String&gt; hello(&#64;RequestParam String name) { ... }
 *
 * // AHORA (Java 21) - &#64;GetMapping + record como DTO de respuesta
 * &#64;GetMapping("/hello")
 * public HelloResponse hello(&#64;RequestParam String name) { ... }
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class HelloRestBridge {

    private final HelloGrpcServer grpcServer;

    /**
     * Constructor injection. Spring inyecta el bean {@link HelloGrpcServer}
     * automaticamente porque es un &#64;Component.
     *
     * @param grpcServer el "cliente" al servidor gRPC (simulado en este demo).
     */
    public HelloRestBridge(final HelloGrpcServer grpcServer) {
        this.grpcServer = grpcServer;
    }

    /**
     * Endpoint publico REST.
     *
     * <p>En un escenario real con gRPC completo, este metodo:
     * <ol>
     *   <li>Crearia un {@code HelloRequest} generado por protoc.</li>
     *   <li>Llamaria a {@code blockingStub.sayHello(request)}.</li>
     *   <li>Extraeria el campo {@code message} del {@code HelloResponse}.</li>
     * </ol>
     * Aqui lo simplificamos a una llamada Java directa.
     *
     * @param name nombre a saludar, viene en query string (?name=Juan)
     * @return DTO {@link HelloResponse} serializado como JSON por Jackson
     */
    @GetMapping("/hello")
    public HelloResponse hello(@RequestParam(name = "name", defaultValue = "mundo") final String name) {
        final String message = grpcServer.sayHello(name);
        return new HelloResponse(message);
    }

    /**
     * DTO de respuesta usando 'record' (Java 14+).
     *
     * <p>ANTES vs AHORA:
     * <pre>
     * // ANTES (Java 8) - clase POJO manual con getter, equals, hashCode, toString.
     * public class HelloResponse {
     *     private final String message;
     *     public HelloResponse(String message) { this.message = message; }
     *     public String getMessage() { return message; }
     *     // + equals, hashCode, toString...
     * }
     *
     * // AHORA (Java 21) - una sola linea. El compilador genera todo.
     * public record HelloResponse(String message) {}
     * </pre>
     */
    public record HelloResponse(String message) {
    }
}
