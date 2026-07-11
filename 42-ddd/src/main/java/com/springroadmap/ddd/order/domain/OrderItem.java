package com.springroadmap.ddd.order.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * OrderItem - Value Object que representa una linea dentro de una Order.
 *
 * ANALOGIA: como una linea del ticket del supermercado ("2 x Leche = $2000").
 * No tiene identidad propia; forma parte del ticket completo (el Aggregate Order).
 *
 * Se anota con {@code @Embeddable} para que JPA lo guarde EMBEBIDO en la tabla de Order
 * (via {@code @ElementCollection} en el aggregate root). No es una entidad independiente.
 *
 * ¿Por que no es record? JPA no soporta records como @Embeddable de forma estable en todas
 * las versiones de Hibernate. Usamos una clase clasica con constructor protegido para JPA
 * y constructor publico validado para el dominio.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *  - Antes: POJO con getters/setters.
 *  - Ahora: campos private final + solo getters, para forzar inmutabilidad como Value Object.
 */
@Embeddable
public class OrderItem {

    // final: una vez asignado en el constructor, no puede reasignarse. Refuerza inmutabilidad.
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private String currency;

    /**
     * Constructor sin argumentos requerido por JPA para hidratar objetos desde la BD.
     * Es {@code protected} para que el codigo cliente NO lo pueda usar accidentalmente.
     */
    protected OrderItem() {
    }

    /**
     * Constructor de dominio: la unica forma legitima de crear un OrderItem.
     * Valida invariantes (cantidad positiva, precio no negativo).
     */
    public OrderItem(String productName, int quantity, BigDecimal unitPrice, String currency) {
        Objects.requireNonNull(productName, "productName no puede ser null");
        Objects.requireNonNull(unitPrice, "unitPrice no puede ser null");
        Objects.requireNonNull(currency, "currency no puede ser null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity debe ser > 0");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("unitPrice no puede ser negativo");
        }
        if (currency.length() != 3) {
            throw new IllegalArgumentException("currency ISO-4217 (3 chars): " + currency);
        }
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.currency = currency;
    }

    /** Devuelve el subtotal de la linea: quantity * unitPrice, envuelto en un Money. */
    public Money subtotal() {
        return new Money(unitPrice.multiply(BigDecimal.valueOf(quantity)), currency);
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getCurrency() { return currency; }
}
