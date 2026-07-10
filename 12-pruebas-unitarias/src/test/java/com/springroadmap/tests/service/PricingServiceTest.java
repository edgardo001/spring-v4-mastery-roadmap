package com.springroadmap.tests.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitarios PUROS de {@link PricingService}.
 *
 * NO usa Spring, NO usa Mockito. Solo JUnit 5. Es el tipo de test más
 * rápido (milisegundos) y debe ser la MAYORÍA de tu suite.
 *
 * ANOTACIONES CLAVE:
 *   - {@code @Test}          — marca un método como test ejecutable.
 *   - {@code @DisplayName}   — nombre humano legible en el reporte.
 *   - {@code @ParameterizedTest} + {@code @ValueSource} — ejecuta el mismo
 *     test con distintos valores de entrada. Ideal para "tablas de verdad".
 *
 * ANTES (JUnit 4) vs AHORA (JUnit 5):
 *   ANTES:  @RunWith(SpringJUnit4ClassRunner.class) + assertEquals(esperado, actual).
 *   AHORA:  @ExtendWith(...) opcional; sin @RunWith. assertEquals sigue igual.
 */
class PricingServiceTest {

    // Instancia bajo prueba. Como no tiene dependencias, la creamos "a mano".
    private final PricingService service = new PricingService();

    @Test
    @DisplayName("calculateFinalPrice: 1000 con 10% de descuento -> 900")
    void calculateFinalPrice_happyPath() {
        double result = service.calculateFinalPrice(1000.0, 10.0);
        // assertEquals(esperado, actual, delta) — delta es la tolerancia para doubles.
        assertEquals(900.0, result, 0.0001);
    }

    @Test
    @DisplayName("calculateFinalPrice: descuento 100% deja el precio en 0")
    void calculateFinalPrice_descuento100_devuelveCero() {
        double result = service.calculateFinalPrice(500.0, 100.0);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    @DisplayName("calculateFinalPrice: descuento negativo lanza IllegalArgumentException")
    void calculateFinalPrice_descuentoNegativo_lanzaIAE() {
        // assertThrows verifica que el bloque lambda LANCE la excepción esperada.
        // Si NO la lanza, o lanza otra distinta, el test falla.
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateFinalPrice(1000.0, -1.0));
    }

    @Test
    @DisplayName("calculateFinalPrice: descuento > 100 lanza IllegalArgumentException")
    void calculateFinalPrice_descuentoMayor100_lanzaIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateFinalPrice(1000.0, 150.0));
    }

    /**
     * @ParameterizedTest ejecuta este método una vez por cada valor del
     * @ValueSource. En este caso, comprobamos que todos los descuentos
     * en el rango [0, 100] son aceptados y devuelven un valor >= 0.
     */
    @ParameterizedTest(name = "descuento válido {0}% no lanza excepción")
    @ValueSource(doubles = {0.0, 5.0, 10.0, 25.5, 50.0, 75.0, 99.9, 100.0})
    void calculateFinalPrice_descuentosValidos_noLanzanExcepcion(double discount) {
        double result = service.calculateFinalPrice(1000.0, discount);
        // El precio final debe ser >= 0 (nunca negativo).
        // Usamos assertEquals con delta razonable para verificar el rango.
        // Simpler: comprobar que result no es NaN ni negativo.
        assertEquals(1000.0 - (1000.0 * discount / 100.0), result, 0.0001);
    }
}
