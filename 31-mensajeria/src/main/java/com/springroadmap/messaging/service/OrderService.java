package com.springroadmap.messaging.service;

import com.springroadmap.messaging.event.OrderCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * OrderService - servicio de dominio que "crea pedidos" y PUBLICA un evento.
 *
 * Analogia del mundo real:
 *   Imagina la ventanilla de una notaria. Cuando registras un contrato:
 *     1. Le asignan un folio unico.
 *     2. El notario grita al pasillo: "¡Contrato 42 registrado a nombre de Juan!".
 *   Los interesados (auditor, contador, mensajero) escuchan y actuan cada uno
 *   por su cuenta. La notaria NO les llama uno por uno; solo grita al pasillo.
 *   Esa es la idea de ApplicationEventPublisher: publicar sin conocer a los oyentes.
 *
 * @Service : le dice a Spring "instancia esto y regalame el bean cuando lo pida".
 *
 * INYECCION POR CONSTRUCTOR (regla del proyecto):
 *   Recibimos ApplicationEventPublisher como parametro del constructor.
 *   Ventajas:
 *     - El campo 'publisher' es 'final' (nadie lo puede reasignar).
 *     - Testear es facil: en un test paso un mock al constructor.
 *     - Si Spring no encuentra el bean, falla al arrancar (mejor que un NPE).
 */
@Service
public class OrderService {

    // 'final' garantiza que este campo se asigna UNA sola vez (en el constructor).
    private final ApplicationEventPublisher publisher;

    // AtomicLong = contador thread-safe. Simula la generacion de IDs de pedido.
    // (En produccion vendria de una secuencia de BD).
    private final AtomicLong sequence = new AtomicLong(0L);

    /**
     * Constructor unico. Spring detecta este constructor y le inyecta
     * automaticamente el bean ApplicationEventPublisher (viene con Boot).
     *
     * ANTES (Java 8 / Spring 4):
     *   @Autowired
     *   private ApplicationEventPublisher publisher;   // field injection
     *
     * AHORA (Spring 5+):
     *   Constructor injection sin @Autowired (unico constructor => Spring lo detecta).
     */
    public OrderService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Crea un pedido y publica el evento OrderCreatedEvent.
     *
     * ¿Por que publicar un evento en lugar de llamar directo a NotificationService?
     *   Porque OrderService NO necesita saber cuantos oyentes hay ni quienes son.
     *   Manana podemos agregar EmailListener, AuditListener, MetricsListener,
     *   AnalyticsListener... sin tocar esta clase. Eso es DESACOPLAMIENTO.
     *
     * @param customer nombre del cliente que hace el pedido.
     * @return el ID de pedido generado.
     */
    public Long createOrder(String customer) {
        // 1. Generamos un ID incremental de manera thread-safe.
        Long orderId = sequence.incrementAndGet();

        // 2. Publicamos el evento. Spring recorre TODOS los @EventListener
        //    registrados y les entrega el objeto. Por defecto es SINCRONO
        //    (misma hebra) - se agregara @Async cuando se necesite en produccion.
        publisher.publishEvent(new OrderCreatedEvent(orderId, customer));

        // 3. Devolvemos el ID al llamador (el controller).
        return orderId;
    }
}
