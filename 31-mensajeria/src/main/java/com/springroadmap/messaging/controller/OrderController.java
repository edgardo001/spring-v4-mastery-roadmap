package com.springroadmap.messaging.controller;

import com.springroadmap.messaging.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * OrderController - capa web REST del modulo.
 *
 * Analogia del mundo real:
 *   Es el mostrador de atencion al publico de la notaria. Recibe la solicitud
 *   del ciudadano (peticion HTTP) y la lleva al notario (OrderService).
 *
 * @RestController = @Controller + @ResponseBody (todos los metodos serializan
 *                   directo a JSON en el body de la respuesta).
 * @RequestMapping("/api/orders") = prefijo comun para todos los endpoints.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // Constructor injection (regla del proyecto: sin Lombok, sin field injection).
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST /api/orders?customer=Juan
     *
     * Crea un pedido y devuelve el ID generado en JSON.
     *
     * @RequestParam("customer") = extrae el query parameter llamado 'customer'.
     *
     * ANTES (Java 8, Servlet crudo):
     *   String customer = request.getParameter("customer");
     *   response.setContentType("application/json");
     *   response.getWriter().write("{\"orderId\":" + id + "}");
     *
     * AHORA:
     *   Spring inyecta el parametro y serializa el Map a JSON automaticamente.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestParam("customer") String customer) {
        Long orderId = orderService.createOrder(customer);

        // Map.of(...) = crea un Map INMUTABLE con las claves/valores dados (Java 9+).
        // ANTES (Java 8):
        //   Map<String,Object> body = new HashMap<>();
        //   body.put("orderId", orderId);
        //   body.put("customer", customer);
        Map<String, Object> body = Map.of(
                "orderId", orderId,
                "customer", customer,
                "status", "CREATED"
        );

        // 201 Created es el codigo HTTP semanticamente correcto tras un POST.
        return ResponseEntity.status(201).body(body);
    }
}
