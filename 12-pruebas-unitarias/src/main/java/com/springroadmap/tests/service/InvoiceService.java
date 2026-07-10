package com.springroadmap.tests.service;

import org.springframework.stereotype.Service;

/**
 * Servicio de facturación que COMPONE otros dos servicios:
 *   - {@link PricingService} para aplicar descuento.
 *   - {@link TaxCalculator} para agregar IVA.
 *
 * ANALOGÍA: es el cajero final que toma el precio con descuento y agrega
 * los impuestos, entregando el total a pagar.
 *
 * ¿POR QUÉ ES BUEN EJEMPLO PARA MOCKITO? Porque tiene DOS colaboradores
 * inyectados por constructor. En el test creamos {@code @Mock} para cada
 * uno y {@code @InjectMocks InvoiceService} para inyectarlos automáticamente.
 */
@Service
public class InvoiceService {

    // 'final' indica que el campo se asigna UNA sola vez (en el constructor)
    // y nunca cambia. Es la base de la inmutabilidad, requisito de un buen
    // diseño testeable.
    private final PricingService pricingService;
    private final TaxCalculator taxCalculator;

    /**
     * Constructor injection: Spring detecta este constructor único y
     * pasa automáticamente los beans registrados.
     *
     * ANTES vs AHORA:
     *   ANTES (Spring 3/4): se usaba {@code @Autowired} en campos privados.
     *   AHORA (Spring 6/Boot 3+): constructor injection es el estándar,
     *   sin necesidad de anotación cuando hay un solo constructor.
     */
    public InvoiceService(PricingService pricingService, TaxCalculator taxCalculator) {
        this.pricingService = pricingService;
        this.taxCalculator = taxCalculator;
    }

    /**
     * Calcula el total de la factura:
     *   precioConDescuento = pricingService.calculateFinalPrice(base, discount)
     *   impuesto           = taxCalculator.calculateTax(precioConDescuento)
     *   total              = precioConDescuento + impuesto
     */
    public double calculateInvoice(double base, double discount) {
        double discounted = pricingService.calculateFinalPrice(base, discount);
        double tax = taxCalculator.calculateTax(discounted);
        return discounted + tax;
    }
}
