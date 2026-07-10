package com.springroadmap.exceptions.exception;

import com.springroadmap.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Manejador global de excepciones.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   → cualquier excepción lanzada desde CUALQUIER @RestController de la
 *     aplicación es interceptada aquí y transformada en una respuesta JSON.
 *
 * Ventaja empresarial: cero try-catch en los controllers. Los controllers
 * solo describen el "happy path". Todos los errores se centralizan y
 * responden con el mismo esquema (ErrorResponse) — el frontend parsea UN
 * único formato, sin importar qué endpoint falló.
 *
 * =====================================================================
 * ANTES (Spring 3 / Java 8) vs AHORA (Spring Boot 4 / Java 21)
 * =====================================================================
 * ANTES: cada Controller tenía su propio try/catch para cada tipo de error
 *        y construía manualmente un HashMap<String,Object> para responder.
 * AHORA: una sola clase @RestControllerAdvice + un record ErrorResponse
 *        cubre TODA la aplicación.
 *
 * PREGUNTA DE ALUMNO — "¿Y si no pongo el handler genérico Exception.class?"
 *   Cualquier excepción no cubierta escapa como 500 con el mensaje por
 *   defecto de Spring (o incluso con stack trace si no configuras
 *   server.error.include-stacktrace=never). Siempre pon el catch-all.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger SLF4J. NUNCA usamos System.out.println (regla del agente DevOps).
     * Se declara `private static final` para reutilizar la misma instancia.
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 404 — Recurso no encontrado.
     *
     * @param ex      excepción capturada; Spring nos la entrega ya casteada.
     * @param request request HTTP entrante; se usa para extraer la URI del path.
     * @return ResponseEntity con status 404 y ErrorResponse en el body.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        // log.warn = nivel WARN (para 4xx no es un error de servidor sino del cliente).
        log.warn("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse(
                "NOT_FOUND",
                ex.getMessage(),
                Instant.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * 422 — Regla de negocio violada.
     *
     * Unprocessable Entity: el JSON es válido pero rompe una regla de dominio.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex, HttpServletRequest request) {

        log.warn("Regla de negocio violada: {}", ex.getMessage());

        ErrorResponse body = new ErrorResponse(
                "BUSINESS_RULE",
                ex.getMessage(),
                Instant.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    /**
     * 500 — Catch-all para cualquier excepción no prevista.
     *
     * REGLA DE SEGURIDAD: NUNCA exponer el mensaje de la excepción original al
     * cliente (podría revelar rutas de archivos, nombres de tablas, credenciales
     * accidentales en el mensaje, etc.). Solo un mensaje genérico.
     * El detalle real se guarda en el log del servidor (log.error con la excepción
     * completa, incluyendo el stack trace, para que el equipo lo investigue).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {

        // log.error con la excepción completa (stack trace en el log del servidor).
        log.error("Error inesperado en {}: ", request.getRequestURI(), ex);

        ErrorResponse body = new ErrorResponse(
                "INTERNAL_ERROR",
                // Mensaje GENÉRICO, nunca ex.getMessage().
                "unexpected error",
                Instant.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
