package com.springroadmap.eventdriven.controller;

import com.springroadmap.eventdriven.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controlador REST del módulo.
 *
 * @RestController = @Controller + @ResponseBody (devuelve JSON directamente).
 * @RequestMapping fija el prefijo común "/api/payments".
 *
 * Endpoint:
 *   POST /api/payments?amount=100 → dispara processPayment y publica el evento.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> pay(@RequestParam BigDecimal amount) {
        Long id = paymentService.processPayment(amount);
        // Map.of (Java 9+) crea un Map INMUTABLE en una línea.
        // ANTES (Java 8): Map<String,Object> m = new HashMap<>(); m.put("id", id); m.put("amount", amount);
        return ResponseEntity.ok(Map.of("id", id, "amount", amount));
    }
}
