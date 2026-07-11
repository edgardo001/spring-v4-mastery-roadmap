package com.springroadmap.vslice.features.getorder;

import java.math.BigDecimal;

/**
 * Response de la feature "obtener orden por id".
 *
 * <p>Nota: aqui SI devolvemos el customer y el amount, porque es una consulta
 * de detalle. En la feature "listOrders" devolvemos un DTO mas ligero
 * (OrderSummary). Cada feature moldea su respuesta segun su necesidad; no hay
 * un "OrderDto" universal.</p>
 */
public record GetOrderResponse(Long id, String customer, BigDecimal amount, String status) {}
