package com.springroadmap.actuator.service;

import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Service;

/**
 * Servicio de negocio que crea pedidos e incrementa una metrica Micrometer.
 *
 * ANALOGIA: es como el cajero de una tienda que ademas del ticket de venta pulsa un
 * "clicker" cada vez que atiende a un cliente. El clicker es el {@link Counter}.
 *
 * PREGUNTA DE ALUMNO — "por que inyectar el Counter en el constructor?"
 *   Constructor injection es la forma recomendada desde Spring 4.3+: es inmutable
 *   (campo final), facil de testear (no requiere reflexion) y evita NullPointerException
 *   porque Spring falla el arranque si no encuentra el bean.
 */
@Service
public class OrderService {

    // final: la referencia no se puede reasignar despues del constructor.
    private final Counter ordersCounter;

    /**
     * Spring inyecta el Counter creado en {@code MetricsConfig#ordersCounter}.
     * <p>
     * NOTA: en Spring 4.3+ ya no hace falta {@code @Autowired} si hay un solo constructor.
     */
    public OrderService(Counter ordersCounter) {
        this.ordersCounter = ordersCounter;
    }

    /**
     * Crea un pedido (simulado) e incrementa el contador de pedidos.
     *
     * @return un identificador ficticio del pedido creado.
     */
    public String createOrder() {
        // increment() aumenta el contador en 1.0 de forma thread-safe.
        ordersCounter.increment();
        // En un caso real aqui iria: repository.save(new Order(...)); publish event; etc.
        return "order-" + System.currentTimeMillis();
    }
}
