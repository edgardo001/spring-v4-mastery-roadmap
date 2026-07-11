package com.springroadmap.eventdriven.event;

import java.math.BigDecimal;

/**
 * Evento de dominio: "Un pago fue procesado con éxito".
 *
 * ANTES (Java 8):
 *   public final class PaymentSuccessEvent {
 *       private final Long paymentId;
 *       private final BigDecimal amount;
 *       public PaymentSuccessEvent(Long paymentId, BigDecimal amount) { ... }
 *       public Long getPaymentId() { return paymentId; }
 *       public BigDecimal getAmount() { return amount; }
 *       // + equals, hashCode, toString a mano
 *   }
 *
 * AHORA (Java 21) — un `record` es una clase INMUTABLE con:
 *   - Constructor generado automáticamente.
 *   - Accessors `paymentId()` y `amount()` (nótese: SIN `get`).
 *   - equals/hashCode/toString automáticos.
 *
 * Publicar POJOs simples como evento es lo estándar en Spring desde Spring Framework 4.2
 * (antes había que extender `ApplicationEvent`). Ya no hace falta.
 */
public record PaymentSuccessEvent(Long paymentId, BigDecimal amount) { }
