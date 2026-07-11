package com.springroadmap.modulith.orders;

/**
 * Modelo publico del modulo orders.
 * Otros modulos SOLO pueden depender de esta clase (y OrderService).
 */
public record Order(Long id, String customer) {
}
