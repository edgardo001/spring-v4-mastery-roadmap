package com.springroadmap.messaging.event;

/**
 * OrderCreatedEvent - MENSAJE inmutable que viaja por el bus de eventos.
 *
 * Analogia del mundo real:
 *   Es un TELEGRAMA. El emisor lo redacta con datos concretos (numero de pedido,
 *   cliente), lo entrega en la oficina de correos (ApplicationEventPublisher) y
 *   NO puede modificarlo despues. Los destinatarios (listeners) lo leen tal cual.
 *
 * ¿Por que un 'record' y no una 'class'?
 *   - 'record' es una clase inmutable especial introducida oficialmente en Java 16.
 *   - Genera automaticamente: constructor, getters (orderId(), customer()),
 *     equals(), hashCode() y toString().
 *   - Es IDEAL para "objetos de datos" como eventos y DTOs.
 *
 * ANTES (Java 8):
 *   public final class OrderCreatedEvent {
 *       private final Long orderId;
 *       private final String customer;
 *       public OrderCreatedEvent(Long orderId, String customer) {
 *           this.orderId = orderId;
 *           this.customer = customer;
 *       }
 *       public Long getOrderId() { return orderId; }
 *       public String getCustomer() { return customer; }
 *       // + equals/hashCode/toString manuales ... 30+ lineas.
 *   }
 *
 * AHORA (Java 21):
 *   public record OrderCreatedEvent(Long orderId, String customer) {}
 *   // 1 sola linea. Lo demas lo escribe el compilador.
 *
 * PREGUNTA DE ALUMNO - "¿Es obligatorio heredar de ApplicationEvent?"
 *   R: NO en Spring 4.2+. Cualquier POJO (o record) puede ser un evento.
 *      Spring lo envolvera internamente en un PayloadApplicationEvent.
 */
public record OrderCreatedEvent(Long orderId, String customer) {
    // El cuerpo va VACIO a proposito: no necesitamos logica extra.
    // Los accesores orderId() y customer() ya existen automaticamente.
}
