package com.springroadmap.vslice.shared;

/**
 * Respuesta de error simple y uniforme para toda la API.
 *
 * <p>Un {@code record} es la forma moderna (Java 16+) de declarar un DTO
 * inmutable: el compilador genera constructor, getters, equals/hashCode y
 * toString automaticamente.</p>
 *
 * <p><b>ANTES (Java 8):</b>
 * <pre>
 *   public class ApiError {
 *       private final String message;
 *       public ApiError(String message) { this.message = message; }
 *       public String getMessage() { return message; }
 *   }
 * </pre>
 * <b>AHORA (Java 21):</b>
 * <pre>
 *   public record ApiError(String message) {}
 * </pre></p>
 */
public record ApiError(String message) {}
