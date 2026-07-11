package com.springroadmap.vslice.features.createorder;

import java.math.BigDecimal;

/**
 * Command de entrada para crear una orden.
 *
 * <p>En Vertical Slice se llama "Command" a un DTO que representa la intencion
 * de MODIFICAR el sistema (crear, actualizar, borrar). Los DTOs que solo LEEN
 * se llaman "Query". Esto es CQRS-lite: separar lecturas de escrituras a nivel
 * de nombres, sin necesidad de dos bases de datos.</p>
 *
 * <p><b>ANTES vs AHORA (arquitectura por capas vs vertical slice):</b>
 * En la arquitectura horizontal este DTO viviria en {@code dto/CreateOrderDto.java}
 * junto a decenas de otros DTOs no relacionados. Aqui vive PEGADO a su handler
 * y endpoint, en la misma carpeta {@code createorder/}. Si borras la feature,
 * borras la carpeta entera.</p>
 */
public record CreateOrderCommand(String customer, BigDecimal amount) {}
