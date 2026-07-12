package com.springroadmap.integration.controller;

import com.springroadmap.integration.domain.Order;
import com.springroadmap.integration.gateway.OrderGateway;
import org.springframework.http.ResponseEntity;
// @RestController = @Controller + @ResponseBody. Convierte los retornos en JSON automáticamente.
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * `OrderController` - Endpoint REST que invoca el flujo de integración.
 *
 * <h2>Flujo</h2>
 * <ol>
 *   <li>Cliente HTTP hace POST /api/orders con body JSON.</li>
 *   <li>Spring MVC deserializa el JSON a un `Order` (record).</li>
 *   <li>Se llama `gateway.process(order)`.</li>
 *   <li>Por debajo: Message&lt;Order&gt; -> orderInput -> transform -> handle -> String.</li>
 *   <li>El controller devuelve el String como JSON.</li>
 * </ol>
 *
 * <h2>ANTES (Java 8) vs AHORA (Java 21)</h2>
 * <pre>
 * // ANTES: inyección por campo con @Autowired (mala práctica moderna)
 * &#64;Autowired private OrderGateway gateway;
 *
 * // AHORA: inyección por constructor (inmutable, testable, obligatoria en Boot 4).
 * private final OrderGateway gateway;
 * public OrderController(OrderGateway gateway) { this.gateway = gateway; }
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿Necesito @Autowired en el constructor?"
 * R: NO. Desde Spring 4.3, si la clase tiene un único constructor, Spring lo usa
 * automáticamente. La anotación es opcional y se omite por convención moderna.</p>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // `private final` = referencia inmutable inyectada por constructor.
    private final OrderGateway gateway;

    /**
     * Constructor injection: Spring pasa el bean `OrderGateway` (proxy dinámico) automáticamente.
     */
    public OrderController(OrderGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * POST /api/orders - dispara el flujo de integración con la orden recibida.
     *
     * @param order objeto Order deserializado desde el JSON del body.
     * @return ResponseEntity con el String producido por el flujo (payload final).
     */
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        // El gateway parece un método normal, pero por dentro envía un Message y espera respuesta.
        String result = gateway.process(order);
        // ResponseEntity.ok(x) = HTTP 200 con `x` como body.
        return ResponseEntity.ok(result);
    }
}
