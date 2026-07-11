package com.springroadmap.vslice.features.createorder;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springroadmap.vslice.shared.ApiError;

/**
 * Endpoint HTTP de la feature "crear orden".
 *
 * <p><b>Diferencia clave con arquitectura por capas:</b> en la arquitectura
 * horizontal habria UN solo {@code OrderController} con muchisimos metodos
 * ({@code create}, {@code get}, {@code list}, {@code update}, {@code delete}).
 * Aqui hay UN endpoint POR feature. Si te toca modificar "crear orden",
 * solo tocas esta carpeta.</p>
 *
 * <p>El endpoint NO tiene logica: recibe el request, llama al handler,
 * arma el ResponseEntity. Todo el "que hacer" vive en el handler.</p>
 */
@RestController
public class CreateOrderEndpoint {

    private final CreateOrderHandler handler;

    // Constructor injection.
    public CreateOrderEndpoint(CreateOrderHandler handler) {
        this.handler = handler;
    }

    /**
     * POST /api/orders — crea una orden.
     * <p>Retorno esperado:
     * <ul>
     *   <li>201 Created + header Location si todo OK.</li>
     *   <li>400 Bad Request + {@link ApiError} si la validacion falla.</li>
     * </ul></p>
     *
     * <p><b>ANTES vs AHORA:</b> antes usabamos {@code Object} como retorno o
     * varias sobrecargas. AHORA {@code ResponseEntity<?>} nos permite devolver
     * tanto la respuesta exitosa como el error con un tipo homogeneo (comodin ?).</p>
     */
    @PostMapping("/api/orders")
    public ResponseEntity<?> create(@RequestBody CreateOrderCommand command) {
        try {
            CreateOrderResponse response = handler.handle(command);
            // Location apunta al recurso recien creado.
            URI location = URI.create("/api/orders/" + response.id());
            return ResponseEntity.created(location).body(response);
        } catch (IllegalArgumentException ex) {
            // Validacion fallida => 400 con mensaje amigable.
            return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
        }
    }
}
