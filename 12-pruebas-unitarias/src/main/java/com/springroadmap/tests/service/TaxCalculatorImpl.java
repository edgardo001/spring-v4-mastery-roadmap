package com.springroadmap.tests.service;

import org.springframework.stereotype.Service;

/**
 * Implementación del IVA chileno: 19% sobre el monto.
 *
 * ANALOGÍA: cada país tiene su propia "receta" de impuesto. Esta es la de
 * Chile. Si el proyecto se despliega en otro país, basta con crear otra
 * clase que implemente {@link TaxCalculator} y Spring la inyectará.
 */
@Service
public class TaxCalculatorImpl implements TaxCalculator {

    /** IVA Chile = 19%. Constante en formato decimal (0.19). */
    private static final double IVA_CHILE = 0.19;

    // @Override indica al compilador que estamos sobreescribiendo un método
    // del contrato. Si la firma no coincide, el compilador nos avisa.
    @Override
    public double calculateTax(double amount) {
        return amount * IVA_CHILE;
    }
}
