package com.springroadmap.ddd.order.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Money - Value Object que representa una cantidad de dinero con su moneda.
 *
 * ANALOGIA: un billete tiene un valor (100) y una moneda (USD/CLP/EUR). Un billete
 * "100" sin moneda es ambiguo. Aqui obligamos a que siempre viajen juntos.
 *
 * INVARIANTES protegidas en el constructor:
 *  - amount >= 0 (no permitimos dinero negativo en este dominio).
 *  - currency debe tener exactamente 3 caracteres (ISO-4217: USD, CLP, EUR...).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *  - Antes: clase POJO con getters y validacion en el constructor manualmente.
 *  - Ahora: record con constructor compacto para validaciones. Menos codigo, misma seguridad.
 *
 * ¿Por que BigDecimal y no double?
 *   double sufre errores de redondeo (0.1 + 0.2 != 0.3). En dominios financieros
 *   se usa BigDecimal para evitar bugs de centavos perdidos.
 */
public record Money(BigDecimal amount, String currency) {

    /**
     * Constructor compacto con validacion de invariantes.
     * Si algo esta mal, se lanza excepcion — Money invalido no puede existir.
     */
    public Money {
        Objects.requireNonNull(amount, "amount no puede ser null");
        Objects.requireNonNull(currency, "currency no puede ser null");
        // BigDecimal.ZERO: constante representando el valor 0.
        // compareTo: retorna -1 si amount < 0, 0 si iguales, 1 si mayor. No usar equals (compara escala).
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount no puede ser negativo: " + amount);
        }
        if (currency.length() != 3) {
            throw new IllegalArgumentException("currency debe tener 3 caracteres ISO-4217: " + currency);
        }
    }

    /**
     * Factory de conveniencia: Money.of(100, "USD").
     * Evita al cliente escribir BigDecimal.valueOf(...) todo el tiempo.
     */
    public static Money of(long amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    /**
     * Suma dos Money del mismo tipo. Devuelve una NUEVA instancia (inmutabilidad).
     * Si las monedas difieren, se lanza excepcion — no se puede sumar USD + CLP.
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "other no puede ser null");
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "No se puede sumar monedas distintas: " + this.currency + " + " + other.currency);
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
