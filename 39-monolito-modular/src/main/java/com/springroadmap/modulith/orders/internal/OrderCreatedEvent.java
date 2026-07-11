package com.springroadmap.modulith.orders.internal;

/**
 * Evento de dominio del modulo orders.
 *
 * Nota: lo dejamos en 'internal' del modulo orders porque es DETALLE del
 * modulo. Otros modulos lo consumen, pero el "contrato" es solo la forma
 * del record. En una version mas estricta se moveria a un paquete
 * 'orders.events' o similar como API publica.
 */
public record OrderCreatedEvent(Long orderId, String customer) {
}
