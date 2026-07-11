package com.springroadmap.hexagonal.domain.port.out;

import com.springroadmap.hexagonal.domain.model.Customer;
// Optional: contenedor introducido en Java 8 que representa "puede haber o no un valor".
// Se prefiere sobre 'null' para hacer explícita la ausencia.
import java.util.Optional;

/**
 * PUERTO DE SALIDA (Secondary Port / Outbound Port).
 *
 * Analogía del mundo real: es el "enchufe hembra" que el hexágono expone hacia afuera
 * diciendo: "necesito guardar y buscar clientes; alguien afuera se encarga de cómo".
 * Después alguien conectará un "enchufe macho" (adapter) que sea MySQL, Mongo, memoria, etc.
 *
 * REGLA HEXAGONAL: esta interfaz vive DENTRO del hexágono (paquete domain),
 * pero la IMPLEMENTACIÓN vive FUERA (paquete adapter.out.persistence).
 * Así invertimos la dependencia (Principio D de SOLID: Dependency Inversion).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES: se retornaba 'Customer findById(Long id)' y podía devolver null,
 *          obligando al llamador a hacer 'if (result != null)'.
 *   AHORA: devolvemos 'Optional<Customer>' para que el compilador nos obligue
 *          a pensar en el caso "no encontrado".
 *
 * PREGUNTA DE ALUMNO — "¿Por qué el dominio define esta interfaz y no la infraestructura?"
 *   Porque quien manda es el DOMINIO. El dominio dicta el contrato ("necesito estos métodos").
 *   La infraestructura obedece implementándolo. Esto se llama "inversión de dependencias":
 *   la flecha de dependencia apunta desde afuera hacia el centro, nunca al revés.
 */
public interface CustomerRepositoryPort {

    /**
     * Persiste un Customer y retorna la versión guardada (con id asignado si venía sin id).
     */
    Customer save(Customer customer);

    /**
     * Busca un Customer por su id. Optional vacío si no existe.
     */
    Optional<Customer> findById(Long id);
}
