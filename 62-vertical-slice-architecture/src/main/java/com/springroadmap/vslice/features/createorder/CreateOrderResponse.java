package com.springroadmap.vslice.features.createorder;

/**
 * Respuesta al crear una orden. Solo devolvemos lo minimo necesario:
 * el id (para futuras consultas) y el status con que quedo la orden.
 *
 * <p>Cada feature define SU PROPIO response, aunque a otras features les
 * bastaria con menos o mas campos. Esto es intencional: los cambios en la
 * feature "getOrder" no deben romper "createOrder".</p>
 */
public record CreateOrderResponse(Long id, String status) {}
