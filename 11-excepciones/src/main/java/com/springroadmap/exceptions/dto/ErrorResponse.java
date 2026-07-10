package com.springroadmap.exceptions.dto;

import java.time.Instant;

/**
 * DTO uniforme para todas las respuestas de error del API.
 *
 * Es un `record` (Java 14+) — una forma corta de declarar una clase
 * INMUTABLE con getters, equals, hashCode y toString generados
 * automáticamente por el compilador.
 *
 * Campos:
 *   - code:      código interno legible (p.ej. "NOT_FOUND", "BUSINESS_RULE").
 *   - message:   mensaje legible por humanos, mostrable al usuario.
 *   - timestamp: instante en el que ocurrió el error (UTC, `java.time.Instant`).
 *   - path:      URI del endpoint que falló (p.ej. "/api/orders/1").
 *
 * =====================================================================
 * ANTES (Java 8) vs AHORA (Java 21)
 * =====================================================================
 * ANTES — POJO con getters/setters (30+ líneas):
 *   public class ErrorResponse {
 *       private String code;
 *       private String message;
 *       private Date   timestamp;  // ← java.util.Date, sin zona horaria
 *       private String path;
 *       public ErrorResponse(String code, String message, Date ts, String path) {
 *           this.code = code; this.message = message;
 *           this.timestamp = ts; this.path = path;
 *       }
 *       public String getCode()    { return code; }
 *       public String getMessage() { return message; }
 *       public Date   getTimestamp(){ return timestamp; }
 *       public String getPath()    { return path; }
 *       // equals / hashCode / toString ...
 *   }
 *
 * AHORA — record en UNA línea. El compilador genera todo:
 *   public record ErrorResponse(String code, String message, Instant timestamp, String path) {}
 *
 * `Instant` en lugar de `Date` evita el bug clásico de zonas horarias
 * (ver MEMORY.md, error frecuente #22).
 */
public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String path
) {
    /*
     * Un record es IMPLÍCITAMENTE final: no se puede heredar.
     * Sus campos son IMPLÍCITAMENTE private final.
     * Jackson (el serializador JSON de Spring) sabe convertirlo sin configuración.
     */
}
