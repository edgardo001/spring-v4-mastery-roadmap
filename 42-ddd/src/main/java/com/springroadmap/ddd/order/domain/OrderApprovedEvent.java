package com.springroadmap.ddd.order.domain;

import java.time.Instant;

/**
 * OrderApprovedEvent - Domain Event: "algo importante paso en el dominio".
 *
 * ANALOGIA: como un anuncio en el altavoz de un aeropuerto ("el vuelo 123 despego").
 * Los oyentes interesados pueden reaccionar (enviar email, generar factura, etc.).
 *
 * Un Domain Event:
 *  - Es INMUTABLE (representa un hecho que YA paso, no cambia).
 *  - Se nombra en pasado ("OrderApproved", no "ApproveOrder").
 *  - Lleva la informacion minima para que los suscriptores reaccionen.
 *
 * Se modela como {@code record} porque encaja perfectamente: inmutable, con equals/hashCode
 * generados y con toString util para logs.
 *
 * ANTES (Java 8):
 *   Se creaba una clase con constructor + getters + equals/hashCode manuales.
 *
 * AHORA (Java 21):
 *   Una linea: {@code record OrderApprovedEvent(OrderId orderId, Instant occurredAt) {}}
 */
public record OrderApprovedEvent(OrderId orderId, Instant occurredAt) {
    /** Factory para crear el evento con el timestamp de "ahora". */
    public static OrderApprovedEvent now(OrderId orderId) {
        return new OrderApprovedEvent(orderId, Instant.now());
    }
}
