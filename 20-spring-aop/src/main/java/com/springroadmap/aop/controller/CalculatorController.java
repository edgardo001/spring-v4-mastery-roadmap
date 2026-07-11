package com.springroadmap.aop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springroadmap.aop.service.CalculatorService;

/**
 * CalculatorController — Expone la calculadora vía HTTP.
 *
 * <p><b>Analogía:</b> es el mostrador de atención al cliente. Recibe pedidos por la
 * ventanilla (HTTP), los pasa a la cocina (service) y devuelve el resultado.
 *
 * <p><b>Endpoints:</b>
 * <ul>
 *   <li>GET {@code /api/calc/add?a=X&b=Y} — 200 con el resultado de a+b.</li>
 *   <li>GET {@code /api/calc/sub?a=X&b=Y} — 200 con el resultado de a-b.</li>
 * </ul>
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b>
 * <pre>
 *   // ANTES: @Controller + @ResponseBody en cada método.
 *   // AHORA: @RestController combina ambos. Además puedes usar 'var':
 *   //   var result = service.add(a, b);
 * </pre>
 */
// PREGUNTA DE ALUMNO — "¿qué es un endpoint?"
//   Es la combinación de una URL + un método HTTP (GET/POST/...) que la aplicación
//   entiende. "GET /api/calc/add" es un endpoint.
@RestController
@RequestMapping("/api/calc")
public class CalculatorController {

    // Constructor injection: preferido sobre @Autowired en campo. Hace la clase
    // testeable e inmutable.
    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    /** GET /api/calc/add?a=..&b=.. — suma vía service (interceptado por aspecto). */
    @GetMapping("/add")
    public int add(@RequestParam int a, @RequestParam int b) {
        return service.add(a, b);
    }

    /** GET /api/calc/sub?a=..&b=.. — resta vía service (NO interceptado). */
    @GetMapping("/sub")
    public int sub(@RequestParam int a, @RequestParam int b) {
        return service.sub(a, b);
    }
}
