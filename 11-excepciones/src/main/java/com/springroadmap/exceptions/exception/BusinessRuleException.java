package com.springroadmap.exceptions.exception;

/**
 * Excepción de dominio: se violó una regla de negocio.
 *
 * Ejemplo: "no se puede procesar un pedido con id impar" (regla ficticia
 * para el ejercicio). En un sistema real sería: "el usuario ya está desactivado",
 * "no hay stock suficiente", "el pago fue rechazado".
 *
 * Convención empresarial:
 *   BusinessRuleException → HTTP 422 Unprocessable Entity
 *   (el request está bien formado pero viola una regla de negocio)
 *
 * =====================================================================
 * ANTES (Java 8) vs AHORA (Java 21)
 * =====================================================================
 * La declaración es idéntica. En Java 21 podría añadirse un `record` para
 * el detalle estructurado, pero mantenemos String por simplicidad.
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * @param message mensaje descriptivo de la regla violada.
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}
