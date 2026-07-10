package com.springroadmap.tests.controller;

import com.springroadmap.tests.service.InvoiceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el cálculo de facturas por HTTP GET.
 *
 * ANALOGÍA: es el mostrador de atención al cliente. Recibe los datos por
 * la URL (base y descuento) y devuelve el total.
 *
 * ENDPOINT: {@code GET /api/invoice?base=1000&discount=10}
 *   Respuesta: número (double) con el total.
 *
 * TESTEO: se prueba con MockMvc standalone (sin arrancar Spring completo),
 * inyectando un {@code InvoiceService} mockeado.
 */
@RestController
@RequestMapping("/api")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Constructor injection: mismo patrón que los servicios.
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Devuelve el total de la factura.
     *
     * @RequestParam extrae parámetros de la query string ({@code ?base=...}).
     */
    @GetMapping("/invoice")
    public double invoice(@RequestParam double base, @RequestParam double discount) {
        return invoiceService.calculateInvoice(base, discount);
    }
}
