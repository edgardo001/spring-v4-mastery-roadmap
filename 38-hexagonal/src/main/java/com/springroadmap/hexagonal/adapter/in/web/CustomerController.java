package com.springroadmap.hexagonal.adapter.in.web;

import com.springroadmap.hexagonal.domain.model.Customer;
// OJO: el controlador depende de la INTERFAZ del use case, NUNCA de CustomerService.
// Ese es el núcleo de la arquitectura hexagonal.
import com.springroadmap.hexagonal.domain.port.in.RegisterCustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ADAPTADOR DE ENTRADA (Primary Adapter / Inbound Adapter).
 *
 * Analogía del mundo real: es la "ventanilla de recepción" del banco. El cliente
 * (el mundo HTTP externo) llega, presenta un formulario JSON, y el recepcionista
 * lo traduce a una llamada interna al operador (use case).
 *
 * ¿Qué hace este archivo?
 *   1. Recibe peticiones HTTP POST en /api/customers.
 *   2. Deserializa el JSON a un DTO simple (record 'RegisterCustomerRequest').
 *   3. Llama al PUERTO DE ENTRADA (interfaz RegisterCustomerUseCase).
 *   4. Devuelve el Customer creado como JSON.
 *
 * IMPORTANTE: el controlador NO llama directamente a CustomerService.class.
 * Llama a la interfaz. Spring inyecta la implementación en tiempo de arranque.
 * Esto permite testear el controlador con un mock del use case en 1 milisegundo.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES: el DTO 'RegisterCustomerRequest' habría sido una clase con getters/setters
 *          (~15 líneas). AHORA es un 'record' de 1 línea.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué no reciclo el 'Customer' como request body?"
 *   Porque mezclarías capas. El request body es un contrato HTTP; puede cambiar
 *   (agregar campos, versionarse). El Customer es el modelo del dominio; no debe
 *   cambiar porque el frontend agregue un campo. Por eso hay un DTO separado.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    // Dependencia sobre la INTERFAZ, no la implementación.
    private final RegisterCustomerUseCase registerCustomerUseCase;

    public CustomerController(RegisterCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = registerCustomerUseCase;
    }

    /**
     * DTO de entrada. Es un 'record' anidado en el mismo archivo: al ser público
     * y estático (por defecto en un record top-level anidado), Spring puede
     * instanciarlo desde el JSON con Jackson usando su constructor canónico.
     */
    public record RegisterCustomerRequest(String name, String email) { }

    /**
     * POST /api/customers
     * Body: { "name": "Juan", "email": "juan@x.com" }
     * Response: 201 Created con el Customer creado (incluye id).
     */
    @PostMapping
    public ResponseEntity<Customer> register(@RequestBody RegisterCustomerRequest request) {
        // Llamamos al puerto de entrada. Toda la lógica está encapsulada allí.
        Customer creado = registerCustomerUseCase.register(request.name(), request.email());
        // ResponseEntity.status(201).body(...) devolvería lo mismo. Aquí usamos 200 OK
        // simple para no complicar el ejemplo; en producción devuelve 201 + Location.
        return ResponseEntity.ok(creado);
    }
}
