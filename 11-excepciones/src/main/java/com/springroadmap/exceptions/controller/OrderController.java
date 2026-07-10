package com.springroadmap.exceptions.controller;

import com.springroadmap.exceptions.exception.BusinessRuleException;
import com.springroadmap.exceptions.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller de demostración para probar el @RestControllerAdvice.
 *
 * Reglas ficticias del endpoint GET /api/orders/{id}:
 *   - id == 0        → ResourceNotFoundException (404).
 *   - id impar (>0)  → BusinessRuleException (422).
 *   - id par         → 200 OK con un JSON simple.
 *
 * Este controller INTENCIONALMENTE no tiene try/catch. La belleza del
 * @RestControllerAdvice es que el controller solo describe el "happy path".
 *
 * PREGUNTA DE ALUMNO — "¿Cómo llega la excepción al advice si aquí solo lanzo?"
 *   Spring MVC envuelve la invocación del método del controller. Si el método
 *   lanza una excepción, Spring la captura, busca un @ExceptionHandler que
 *   coincida y usa su valor de retorno como respuesta HTTP.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    /**
     * @param id identificador del pedido (viene en la URL, @PathVariable lo extrae).
     * @return 200 con {"id": N, "status": "OK"} si id es par y > 0.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findOrder(@PathVariable long id) {

        // Regla 1: id = 0 -> no existe.
        if (id == 0) {
            throw new ResourceNotFoundException("Order with id 0 not found");
        }

        // Regla 2: id impar -> viola regla de negocio (ejemplo didáctico).
        // El operador % (módulo) devuelve el resto. Si es != 0, el número es impar.
        if (id % 2 != 0) {
            throw new BusinessRuleException("Odd order ids are not allowed: " + id);
        }

        // Happy path: devolvemos un Map como JSON.
        // Map.of(...) crea un mapa INMUTABLE (Java 9+). ANTES: new HashMap<>() + put().
        return ResponseEntity.ok(Map.of("id", id, "status", "OK"));
    }
}
