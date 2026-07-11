package com.springroadmap.actuator.controller;

import com.springroadmap.actuator.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST que expone el endpoint POST /api/orders.
 *
 * ANALOGIA: es la "ventanilla" del banco donde el cliente entrega su solicitud.
 * El controlador no hace la logica, la delega al {@link OrderService}.
 *
 * PREGUNTA DE ALUMNO — "diferencia entre @Controller y @RestController?"
 *   @RestController = @Controller + @ResponseBody. Es decir, todo lo que devuelvas
 *   se serializa a JSON automaticamente (usando Jackson), sin renderizar una vista HTML.
 *
 * ANTES (Spring MVC clasico) vs AHORA (Spring 6/7 + Boot 4):
 *   ANTES: @Controller + ModelAndView + web.xml + DispatcherServlet configurado a mano.
 *   AHORA: @RestController, Tomcat embebido, cero XML, JSON automatico.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST /api/orders — crea un pedido e incrementa la metrica.
     * <p>
     * PALABRAS CLAVE:
     * - {@code Map.of("id", ...)}: fabrica inmutable introducida en Java 9 (equivalente
     *   moderno a Collections.unmodifiableMap(new HashMap&lt;&gt;(){{ put(...); }})).
     */
    @PostMapping
    public Map<String, String> createOrder() {
        String id = orderService.createOrder();
        return Map.of("id", id, "status", "CREATED");
    }
}
