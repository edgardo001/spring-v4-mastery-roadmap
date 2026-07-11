package com.springroadmap.ddd.order.infrastructure;

import com.springroadmap.ddd.order.application.OrderService;
import com.springroadmap.ddd.order.domain.Order;
import com.springroadmap.ddd.order.domain.OrderId;
import com.springroadmap.ddd.order.domain.OrderItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderController - adapter primario HTTP para el bounded context "order".
 *
 * ANALOGIA: como el mesero del restaurante. Toma tu pedido (HTTP) y se lo lleva
 * al cajero (OrderService), pero NO cocina ni valida reglas de negocio.
 *
 * Endpoints:
 *  - POST /api/orders           -> crea una orden.
 *  - PUT  /api/orders/{id}/approve -> aprueba una orden.
 *
 * NOTA de arquitectura: el controller usa DTOs (records anidados) en el @RequestBody
 * para NO exponer directamente las entidades del dominio (regla del roadmap).
 *
 * PREGUNTA DE ALUMNO — "¿Por que un record dentro del controller?"
 *   Los records son una forma corta de declarar DTOs inmutables. Al declararlos dentro
 *   del controller queda claro que son "estructuras de transporte" locales, no del dominio.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    /** DTO de entrada del POST. Se usa un record por brevedad y por inmutabilidad. */
    public record ItemRequest(String productName, int quantity, BigDecimal unitPrice, String currency) {}
    public record CreateOrderRequest(String customer, List<ItemRequest> items) {}

    /** DTO de salida: NO expone la entidad Order directamente (buenas practicas DDD). */
    public record OrderResponse(String id, String customer, String status,
                                BigDecimal total, String currency) {
        static OrderResponse from(Order order) {
            return new OrderResponse(
                order.getId().value(),
                order.getCustomer(),
                order.getStatus().name(),
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency()
            );
        }
    }

    /**
     * POST /api/orders — crea una nueva orden.
     * @param req DTO con customer y lista de items.
     * @return 200 con el OrderResponse.
     */
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest req) {
        // Mapeamos DTOs de entrada al modelo de dominio.
        List<OrderItem> items = req.items().stream()
            .map(it -> new OrderItem(it.productName(), it.quantity(), it.unitPrice(), it.currency()))
            .toList();
        Order created = service.create(req.customer(), items);
        return ResponseEntity.ok(OrderResponse.from(created));
    }

    /**
     * PUT /api/orders/{id}/approve — aprueba una orden existente.
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<OrderResponse> approve(@PathVariable String id) {
        Order approved = service.approve(new OrderId(id));
        return ResponseEntity.ok(OrderResponse.from(approved));
    }
}
