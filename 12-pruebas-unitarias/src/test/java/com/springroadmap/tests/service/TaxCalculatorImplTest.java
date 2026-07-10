package com.springroadmap.tests.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test unitario puro de {@link TaxCalculatorImpl}.
 *
 * Verifica la regla de negocio: IVA Chile = 19% sobre el monto.
 */
class TaxCalculatorImplTest {

    private final TaxCalculator calculator = new TaxCalculatorImpl();

    @Test
    @DisplayName("IVA Chile: 1000 -> 190")
    void calculateTax_aplica19PorCiento() {
        assertEquals(190.0, calculator.calculateTax(1000.0), 0.0001);
    }

    @Test
    @DisplayName("IVA sobre cero es cero")
    void calculateTax_conCero_devuelveCero() {
        assertEquals(0.0, calculator.calculateTax(0.0), 0.0001);
    }
}
