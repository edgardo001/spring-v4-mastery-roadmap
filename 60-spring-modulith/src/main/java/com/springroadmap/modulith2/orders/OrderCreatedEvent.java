package com.springroadmap.modulith2.orders;

/**
 * Evento de dominio publico del modulo orders.
 *
 * A diferencia del modulo 39, aqui lo dejamos en el paquete raiz del modulo
 * (no en 'internal') porque es un CONTRATO consumido por otros modulos.
 */
public record OrderCreatedEvent(Long orderId, String customer) {
}
