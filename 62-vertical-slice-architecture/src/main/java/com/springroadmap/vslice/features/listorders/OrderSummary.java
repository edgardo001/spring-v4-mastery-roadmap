package com.springroadmap.vslice.features.listorders;

/**
 * DTO ligero para la lista de ordenes (solo id + customer + status).
 *
 * <p>El listado no incluye {@code amount} intencionalmente para ilustrar que
 * cada slice moldea sus DTOs a su medida. Si otro equipo cambia esto, no
 * afecta a las features {@code createOrder} ni {@code getOrder}.</p>
 */
public record OrderSummary(Long id, String customer, String status) {}
