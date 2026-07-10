package com.springroadmap.tests.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios de {@link InvoiceService} con MOCKITO puro (sin Spring).
 *
 * OBJETIVO PEDAGÓGICO: aislar el servicio bajo prueba de sus colaboradores.
 * No queremos que un bug en {@link PricingService} o {@link TaxCalculator}
 * haga fallar este test — queremos aislar SOLO la lógica de composición.
 *
 * PATRÓN:
 *   1. @ExtendWith(MockitoExtension.class) — Mockito integra JUnit 5.
 *   2. @Mock  — Mockito crea un "doble de prueba" (fake) del tipo indicado.
 *   3. @InjectMocks — Mockito busca el constructor con los @Mock y los inyecta.
 *   4. when(mock.metodo(...)).thenReturn(valor) — programamos el comportamiento.
 *   5. verify(mock).metodo(...) — verificamos que fue llamado como esperamos.
 *
 * ANTES (JUnit 4 + Mockito 1):
 *   {@code @RunWith(MockitoJUnitRunner.class)}
 *   {@code Mockito.mock(PricingService.class);} manualmente.
 * AHORA (JUnit 5 + Mockito 5):
 *   {@code @ExtendWith(MockitoExtension.class)} + {@code @Mock} / {@code @InjectMocks}.
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private PricingService pricingService;

    @Mock
    private TaxCalculator taxCalculator;

    // @InjectMocks: Mockito construye InvoiceService pasándole los mocks al constructor.
    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void calculateInvoice_llamaAmbosColaboradoresYSumaImpuesto() {
        // ARRANGE — programamos el comportamiento de los mocks.
        // when(pricingService.calculateFinalPrice(1000, 10)).thenReturn(900);
        when(pricingService.calculateFinalPrice(1000.0, 10.0)).thenReturn(900.0);
        // when(taxCalculator.calculateTax(900)).thenReturn(171); // 19% de 900
        when(taxCalculator.calculateTax(900.0)).thenReturn(171.0);

        // ACT
        double total = invoiceService.calculateInvoice(1000.0, 10.0);

        // ASSERT — verificamos el resultado.
        assertEquals(1071.0, total, 0.0001);

        // VERIFY — verificamos que los mocks fueron llamados con los args esperados.
        // Esto detecta refactors que "olvidan" llamar a un colaborador.
        verify(pricingService).calculateFinalPrice(eq(1000.0), eq(10.0));
        verify(taxCalculator).calculateTax(eq(900.0));
        // verifyNoMoreInteractions: garantiza que NO hubo llamadas extra
        // (por ejemplo, llamar dos veces al taxCalculator sería un bug).
        verifyNoMoreInteractions(pricingService, taxCalculator);
    }

    @Test
    void calculateInvoice_conCero_devuelveCero() {
        // Escenario borde: descuento 100% y base 0 -> total 0.
        when(pricingService.calculateFinalPrice(0.0, 0.0)).thenReturn(0.0);
        when(taxCalculator.calculateTax(0.0)).thenReturn(0.0);

        double total = invoiceService.calculateInvoice(0.0, 0.0);

        assertEquals(0.0, total, 0.0001);
        verify(pricingService).calculateFinalPrice(0.0, 0.0);
        verify(taxCalculator).calculateTax(0.0);
    }
}
