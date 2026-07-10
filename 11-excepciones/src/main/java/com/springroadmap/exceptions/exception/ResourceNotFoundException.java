package com.springroadmap.exceptions.exception;

/**
 * Excepción de dominio: el recurso pedido no existe.
 *
 * Extiende RuntimeException (unchecked) para NO obligar a declararla con
 * `throws` en cada método. Spring y el resto del ecosistema esperan que las
 * excepciones de negocio sean unchecked.
 *
 * Convención empresarial:
 *   ResourceNotFoundException  → HTTP 404 Not Found
 *
 * =====================================================================
 * ANTES (Java 8) vs AHORA (Java 21)
 * =====================================================================
 * La sintaxis es idéntica en ambas versiones: `extends RuntimeException`
 * existe desde Java 1.0. Lo que cambia en Java 21 es que podríamos usar
 * `sealed class` para restringir qué clases pueden heredarla, pero aquí
 * mantenemos el patrón clásico por simplicidad pedagógica.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué no usar simplemente RuntimeException?"
 *   Porque el @RestControllerAdvice necesita distinguir POR TIPO para
 *   devolver códigos HTTP diferentes (404 vs 422 vs 500). Una única
 *   RuntimeException genérica siempre acabaría en 500.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor con mensaje descriptivo.
     * @param message mensaje que describe qué recurso faltó (p.ej. "Order 42 not found").
     */
    public ResourceNotFoundException(String message) {
        // super(...) llama al constructor de RuntimeException con el mensaje.
        super(message);
    }
}
