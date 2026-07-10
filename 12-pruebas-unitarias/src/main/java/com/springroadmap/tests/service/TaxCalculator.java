package com.springroadmap.tests.service;

/**
 * Contrato (interfaz) para calcular impuestos sobre un monto.
 *
 * ANALOGÍA: es como el "menú" del SII (Servicio de Impuestos Internos).
 * Define QUÉ se puede pedir (calcular impuesto), pero no CÓMO. La
 * implementación concreta ({@link TaxCalculatorImpl}) decide la tasa.
 *
 * ¿POR QUÉ UNA INTERFAZ? Porque en tests con Mockito podemos crear un
 * {@code @Mock TaxCalculator} y controlar su comportamiento sin tocar
 * la implementación real. Esta es la esencia de la INVERSIÓN DE DEPENDENCIAS.
 */
public interface TaxCalculator {

    /**
     * Calcula el impuesto correspondiente a un monto neto.
     *
     * @param amount monto sobre el cual aplicar el impuesto.
     * @return valor del impuesto (NO incluye el amount original).
     */
    double calculateTax(double amount);
}
