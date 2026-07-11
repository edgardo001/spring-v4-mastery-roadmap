package com.springroadmap.hexagonal.domain.port.in;

// Importamos el modelo de dominio. NO importamos nada de Spring.
import com.springroadmap.hexagonal.domain.model.Customer;

/**
 * PUERTO DE ENTRADA (Primary Port / Inbound Port).
 *
 * Analogía del mundo real: es el "botón" que el mundo exterior puede pulsar
 * para pedir algo al hexágono. El botón dice: "Registrar cliente". Al exterior
 * no le importa CÓMO se registra, solo que existe ese botón.
 *
 * ¿Por qué una interfaz?
 *   - El controlador REST NO dependerá de una clase concreta (CustomerService),
 *     sino de esta interfaz. Eso permite:
 *       (a) testear el controlador con un mock del use case.
 *       (b) cambiar la implementación del caso de uso sin tocar el controlador.
 *       (c) tener varias implementaciones (ej. una async, otra sync).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   La sintaxis de una interfaz simple NO cambió. Lo que cambió es que ahora
 *   una interfaz puede declarar 'default', 'static', 'private' y 'sealed'.
 *   Ejemplo Java 21 con 'sealed' (opcional): 'public sealed interface X permits A, B'.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué no anoto esta interfaz con @Service?"
 *   Porque las anotaciones de Spring pertenecen a la infraestructura. El dominio
 *   debe seguir siendo Java puro. La implementación (CustomerService) sí llevará
 *   @Service, porque vive en el paquete 'application' que es un poco menos puro.
 */
public interface RegisterCustomerUseCase {

    /**
     * Registra un nuevo cliente con nombre y correo.
     *
     * @param name  nombre del cliente (no null ni vacío según regla de negocio).
     * @param email correo del cliente.
     * @return el Customer creado, con id asignado por el adaptador de persistencia.
     */
    Customer register(String name, String email);
}
