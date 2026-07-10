// Package raíz del módulo (coincide con la carpeta física).
package com.springroadmap.exceptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo 11 - Manejo Global de Excepciones.
 *
 * Esta clase, como en cualquier proyecto Spring Boot, arranca el contexto:
 *   1. Escanea `com.springroadmap.exceptions` y subpaquetes.
 *   2. Detecta el @RestController (OrderController) y el @RestControllerAdvice
 *      (GlobalExceptionHandler) y los registra como beans singleton.
 *   3. Cuando el Controller lanza una excepción, Spring busca el @ExceptionHandler
 *      correspondiente en el @RestControllerAdvice y devuelve una respuesta HTTP
 *      con el código de estado adecuado y un ErrorResponse en formato JSON.
 *
 * PREGUNTA DE ALUMNO — "¿Cómo sabe Spring qué handler usar?"
 *   Recorre las excepciones declaradas en cada @ExceptionHandler y elige la MÁS
 *   ESPECÍFICA que coincide con la excepción lanzada. Si no encuentra una
 *   específica, usa la genérica (Exception.class) como catch-all.
 */
@SpringBootApplication
public class ExceptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExceptionsApplication.class, args);
    }
}
