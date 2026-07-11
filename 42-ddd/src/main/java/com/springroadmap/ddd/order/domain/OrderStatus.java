package com.springroadmap.ddd.order.domain;

/**
 * OrderStatus - estados posibles de una Order.
 *
 * ANALOGIA: como el estado civil de una persona (soltero, casado, divorciado).
 * Solo estos valores existen; no se puede "inventar" un estado nuevo.
 *
 * Un enum en Java es una clase especial con instancias fijas conocidas en tiempo de compilacion.
 *
 * ANTES (Java 8):
 *   Los enum ya existian desde Java 5. La sintaxis es identica a Java 21.
 *   Lo que cambia en Java 21 es poder usar {@code switch} como expresion:
 *
 * <pre>
 * // Java 8:
 * String label;
 * switch (status) {
 *     case PENDING: label = "Pendiente"; break;
 *     case APPROVED: label = "Aprobada"; break;
 *     default: label = "?";
 * }
 *
 * // Java 21:
 * String label = switch (status) {
 *     case PENDING -> "Pendiente";
 *     case APPROVED -> "Aprobada";
 * };
 * </pre>
 */
public enum OrderStatus {
    /** Orden recien creada, esperando aprobacion. */
    PENDING,
    /** Orden aprobada por el sistema/usuario. */
    APPROVED,
    /** Orden cancelada (reservado para uso futuro). */
    CANCELLED
}
