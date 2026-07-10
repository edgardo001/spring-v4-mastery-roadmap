package com.springroadmap.validation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador GLOBAL de errores de validación.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *   -> Se aplica a TODOS los @RestController.
 *   -> Los métodos devuelven directamente el body serializado a JSON.
 *
 * ============================================================
 * ANTES (sin @ControllerAdvice)
 * ============================================================
 * Cuando @Valid fallaba, Spring devolvía un JSON gigante y feo:
 *   {
 *     "timestamp": "...",
 *     "status": 400,
 *     "error": "Bad Request",
 *     "trace": "org.springframework.web.bind.MethodArgumentNotValidException...",
 *     "path": "/api/users"
 *   }
 * El frontend tenía que parsear un trace para saber qué campo estaba mal.
 *
 * ============================================================
 * AHORA (con @RestControllerAdvice)
 * ============================================================
 * Devolvemos un Map plano field -> message:
 *   {
 *     "name":  "name obligatorio",
 *     "email": "email inválido"
 *   }
 * Simple, predecible, mostrable directamente en un formulario.
 *
 * FAQ ALUMNO — "¿Por qué LinkedHashMap y no HashMap?"
 *   LinkedHashMap preserva el orden de inserción. Así los errores
 *   salen en el mismo orden en que Spring los detectó (típicamente
 *   el orden de declaración de los campos del DTO). Más predecible
 *   para el frontend y para los tests.
 * ============================================================
 */
@RestControllerAdvice
public class ValidationExceptionHandler {

    /**
     * Captura MethodArgumentNotValidException, lanzada por Spring cuando
     * la validación de un @Valid @RequestBody falla.
     *
     * @param ex excepción con BindingResult que contiene TODOS los errores.
     * @return 400 Bad Request con un Map field -> message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        // Usamos LinkedHashMap para mantener el orden de detección.
        Map<String, String> errors = new LinkedHashMap<>();

        // getBindingResult() -> contenedor de errores de binding + validación.
        // getFieldErrors() -> lista de errores por CAMPO (ignora errores globales de clase).
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            // getField()          -> nombre del campo (p. ej. "email").
            // getDefaultMessage() -> el `message = "..."` que pusimos en la anotación.
            //                        Si dos anotaciones fallan en el mismo campo, se
            //                        conserva solo el PRIMER mensaje (comportamiento
            //                        estándar de put() en Map).
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
