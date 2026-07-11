package com.springroadmap.grpc.service;

import org.springframework.stereotype.Service;

/**
 * Implementacion en memoria del servicio Hello.
 *
 * <p>ANALOGIA: si {@link HelloService} es la carta del restaurante,
 * esta clase es el cocinero que efectivamente prepara el plato.
 *
 * <p>@Service marca la clase como bean de negocio (variante semantica de @Component).
 * Spring la instancia una vez (singleton) y la inyecta donde se necesite.
 */
@Service
public class HelloServiceImpl implements HelloService {

    /**
     * Construye el saludo.
     *
     * <p>ANTES vs AHORA:
     * <pre>
     * // ANTES (Java 8) - concatenacion clasica
     * return "Hola, " + name + "! (via gRPC-demo)";
     *
     * // AHORA (Java 21) - String.format o text blocks para strings multilinea.
     * // Para una sola linea la concatenacion sigue siendo la opcion mas clara.
     * </pre>
     */
    @Override
    public String sayHello(final String name) {
        // Validacion defensiva: en gRPC el equivalente seria responder con
        // Status.INVALID_ARGUMENT via responseObserver.onError(...).
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name no puede ser null ni vacio");
        }
        return "Hola, " + name + "! (via gRPC-demo)";
    }
}
