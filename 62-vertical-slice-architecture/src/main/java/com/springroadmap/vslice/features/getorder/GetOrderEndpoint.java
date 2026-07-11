package com.springroadmap.vslice.features.getorder;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint HTTP GET /api/orders/{id}.
 *
 * <p>Notese que este RestController convive con {@code CreateOrderEndpoint}
 * sin conflicto: Spring registra cada controlador y sus rutas por separado.
 * En arquitectura por capas ambos metodos estarian en un unico
 * {@code OrderController} con 300+ lineas.</p>
 */
@RestController
public class GetOrderEndpoint {

    private final GetOrderHandler handler;

    public GetOrderEndpoint(GetOrderHandler handler) {
        this.handler = handler;
    }

    /**
     * GET /api/orders/{id}
     * <ul>
     *   <li>200 OK si existe.</li>
     *   <li>404 Not Found si no existe.</li>
     * </ul>
     */
    @GetMapping("/api/orders/{id}")
    public ResponseEntity<GetOrderResponse> getOne(@PathVariable Long id) {
        // Optional.map + orElseGet: patron idiomatico.
        return handler.handle(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
