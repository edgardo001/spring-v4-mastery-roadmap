package com.springroadmap.ddd.order.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * OrderId - Value Object que representa la identidad de una Order.
 *
 * ANALOGIA: como el numero de patente de un vehiculo. Dos patentes iguales identifican
 * el mismo vehiculo; el "valor" del ID es lo que importa, no la instancia del objeto.
 *
 * En DDD un Value Object:
 *  - Es INMUTABLE (no cambia despues de crearse).
 *  - Se compara por VALOR (no por referencia).
 *  - No tiene identidad propia (a diferencia de una Entity).
 *
 * En Java 21 lo modelamos con {@code record}, que genera constructor, equals, hashCode
 * y toString automaticamente y hace la clase inmutable.
 *
 * ANTES (Java 8):
 * <pre>
 * public final class OrderId {
 *     private final String value;
 *     public OrderId(String value) { this.value = value; }
 *     public String getValue() { return value; }
 *     public boolean equals(Object o) { ... 15 lineas ... }
 *     public int hashCode() { return Objects.hash(value); }
 * }
 * </pre>
 *
 * AHORA (Java 21):
 * <pre>
 * public record OrderId(String value) { }
 * </pre>
 *
 * PREGUNTA DE ALUMNO — "¿Que es un record?"
 *   Un record es una clase inmutable "de datos" que Java genera por ti.
 *   El compilador te crea constructor, getters (con el mismo nombre del campo), equals,
 *   hashCode y toString automaticamente.
 */
public record OrderId(String value) {

    /**
     * Constructor compacto: valida invariantes antes de asignar los campos.
     * Si el valor es null o vacio, la creacion falla — el Value Object nunca queda "invalido".
     */
    public OrderId {
        // Objects.requireNonNull: lanza NullPointerException con mensaje descriptivo si es null.
        Objects.requireNonNull(value, "OrderId no puede ser null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("OrderId no puede estar vacio");
        }
    }

    /**
     * Factory method para generar un OrderId nuevo con UUID aleatorio.
     * Facilita crear ordenes desde el servicio sin acoplarse a UUID directamente.
     */
    public static OrderId newId() {
        // UUID.randomUUID: genera un identificador universal unico (128 bits).
        return new OrderId(UUID.randomUUID().toString());
    }
}
