package com.springroadmap.tests.service;

// @Service marca esta clase como un "bean de negocio" que Spring instancia
// una vez y comparte (singleton) con quien lo pida.
import org.springframework.stereotype.Service;

/**
 * Servicio que calcula el precio final después de aplicar un descuento.
 *
 * ANALOGÍA: es la caja registradora de un almacén. Le das el precio base y
 * el descuento en porcentaje, y te devuelve cuánto debes pagar.
 *
 * Este servicio se usa como ejemplo DIDÁCTICO de tests unitarios PUROS
 * (sin dependencias). No necesita mocks: JUnit 5 basta.
 */
@Service
public class PricingService {

    /**
     * Calcula {@code base - (base * discountPercent / 100)}.
     *
     * @param base            precio base positivo (ej: 1000.0).
     * @param discountPercent porcentaje de descuento en [0, 100].
     * @return precio final después del descuento.
     * @throws IllegalArgumentException si {@code discountPercent < 0} o {@code > 100}.
     *
     * COMENTARIO DE LENGUAJE:
     *   - {@code double} es un tipo primitivo de 64 bits para números con decimales.
     *     Es simple pero NO recomendado para dinero real en producción
     *     (usa {@code BigDecimal}). Aquí lo usamos por simplicidad didáctica.
     *   - {@code throw new IllegalArgumentException(...)} lanza una excepción
     *     no chequeada (no requiere {@code throws} en la firma).
     */
    public double calculateFinalPrice(double base, double discountPercent) {
        // PREGUNTA DE ALUMNO — "¿por qué validar aquí y no en el controller?"
        //   Porque el servicio es la última línea de defensa del dominio.
        //   Un test unitario del servicio garantiza la regla, sin importar
        //   quién lo llame (controller, batch, otro servicio).
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException(
                    "discountPercent debe estar en [0, 100], recibido: " + discountPercent);
        }
        // Fórmula estándar de descuento porcentual.
        return base - (base * discountPercent / 100.0);
    }
}
