package com.springroadmap.ddd.order.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitarios PUROS del Value Object Money.
 * Sin Spring, sin BD — solo Java + JUnit.
 */
class MoneyTest {

    @Test
    void rejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () ->
            new Money(new BigDecimal("-1"), "USD"));
    }

    @Test
    void rejectsWrongCurrencyLength() {
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.TEN, "US"));
        assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.TEN, "USDX"));
    }

    @Test
    void add_sumsSameCurrency() {
        Money a = Money.of(10, "USD");
        Money b = Money.of(20, "USD");
        assertEquals(new BigDecimal("30"), a.add(b).amount());
    }

    @Test
    void add_rejectsDifferentCurrencies() {
        assertThrows(IllegalArgumentException.class, () ->
            Money.of(10, "USD").add(Money.of(20, "CLP")));
    }
}
