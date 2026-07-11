package com.springroadmap.vslice.features.createorder;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.springroadmap.vslice.shared.OrderStore;

/**
 * Handler de la feature "crear orden".
 *
 * <p><b>Que es un Handler:</b> el objeto que contiene TODA la logica de esta
 * feature. En arquitectura por capas seria un metodo dentro de un
 * {@code OrderService} de 500 lineas que atiende crear, actualizar, borrar,
 * listar, etc. Aqui es una clase pequena, dedicada, con UNA sola razon para
 * cambiar.</p>
 *
 * <p><b>Analogia:</b> es el cocinero especializado en un solo plato. Cambiar
 * la receta de "hamburguesa" no lo distrae; el que hace "pizzas" es otro
 * cocinero en otra estacion.</p>
 *
 * <p><b>Constructor injection sin Lombok</b> (regla del roadmap): declaramos
 * los campos {@code final} y los recibimos por constructor. Spring inyecta
 * automaticamente el {@code OrderStore}.</p>
 *
 * <p><b>PREGUNTA DE ALUMNO — "¿por que no uso @Service?"</b>
 * {@code @Service} es solo un {@code @Component} con nombre bonito. Como en
 * Vertical Slice el Handler NO es exactamente un "service" tradicional,
 * usar {@code @Component} evita cargar la metafora de la arquitectura en capas.</p>
 */
@Component
public class CreateOrderHandler {

    // final => la referencia se asigna 1 vez en el constructor y no puede cambiar.
    private final OrderStore store;

    // Constructor injection: Spring ve este constructor y le pasa el bean OrderStore.
    public CreateOrderHandler(OrderStore store) {
        this.store = store;
    }

    /**
     * Ejecuta la logica de crear una orden. Validaciones manuales para no
     * introducir jakarta.validation en este modulo pedagogico.
     *
     * @param command datos de entrada
     * @return respuesta con id y status
     * @throws IllegalArgumentException si el customer es null/blanco o amount no es positivo
     */
    public CreateOrderResponse handle(CreateOrderCommand command) {
        // Validacion 1: el command no puede ser null.
        if (command == null) {
            throw new IllegalArgumentException("command es obligatorio");
        }
        // Validacion 2: customer no null y no blanco.
        // isBlank() (Java 11+): true si es null-safe? No, tira NPE. Por eso probamos null primero.
        if (command.customer() == null || command.customer().isBlank()) {
            throw new IllegalArgumentException("customer es obligatorio");
        }
        // Validacion 3: amount > 0. BigDecimal.compareTo devuelve -1/0/1.
        if (command.amount() == null || command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount debe ser mayor a cero");
        }

        // Estado inicial fijo para simplificar. En un caso real podria depender del monto.
        OrderStore.StoredOrder stored = store.save(command.customer(), command.amount(), "CREATED");
        // Devolvemos solo lo publico.
        return new CreateOrderResponse(stored.id(), stored.status());
    }
}
