package com.springroadmap.ddd.order.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order - Aggregate Root del bounded context "order".
 *
 * ANALOGIA: como el "pedido" completo en una pizzeria. El aggregate es la unidad
 * consistente: no tiene sentido cambiar una linea del pedido saltandose la validacion
 * del pedido completo. Solo se accede a la orden por su ID y solo ella modifica sus lineas.
 *
 * REGLAS DDD aplicadas:
 *  - Encapsulacion: los setters son inexistentes; se modifica el estado SOLO por metodos de negocio
 *    (aqui {@link #approve()}). Esto evita "modelos anemicos" tipo bag-of-setters.
 *  - Invariantes: el constructor rechaza ordenes vacias o sin cliente.
 *  - Domain Events: cuando ocurre algo relevante (approve), se registra un evento en una
 *    lista transient para que el servicio de aplicacion lo publique tras persistir.
 *
 * ANTES (modelo anemico) vs AHORA (Aggregate rico):
 * <pre>
 * // ANTES (anemico) — logica de negocio dispersa en el service:
 * order.setStatus("APPROVED");
 * order.setApprovedAt(new Date());
 * if (order.getStatus().equals("APPROVED")) { ... } // en el service, no en la entidad.
 *
 * // AHORA (rico) — la logica vive en el aggregate:
 * order.approve(); // encapsula validacion + cambio de estado + evento.
 * </pre>
 *
 * PREGUNTA DE ALUMNO — "¿Por que el constructor sin argumentos es protected?"
 *   JPA necesita un constructor sin argumentos para hidratar la entidad desde la BD por reflexion.
 *   Lo marcamos protected para que el CODIGO CLIENTE no pueda usarlo (obligamos a pasar por el
 *   constructor publico que valida invariantes).
 */
@Entity
@Table(name = "orders")
public class Order {

    @EmbeddedId
    private OrderId id;

    @Column(nullable = false)
    private String customer;

    /**
     * @ElementCollection: le dice a JPA que guarde la lista de OrderItem en una tabla
     * secundaria (order_items) enlazada por el id de la orden.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items = new ArrayList<>();

    @Embedded
    private Money totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * @Transient: campo que JPA NO persiste. Aqui guardamos los eventos de dominio
     * generados durante los metodos de negocio, para que el service los publique despues.
     */
    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    /**
     * Constructor sin argumentos requerido por JPA. Protected para bloquear su uso directo
     * desde el codigo cliente — asi obligamos a usar el constructor de dominio.
     */
    protected Order() {
    }

    /**
     * Constructor de dominio: unica forma legitima de crear una Order desde cero.
     * Valida las invariantes del aggregate.
     */
    public Order(OrderId id, String customer, List<OrderItem> items, Money totalAmount) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(customer, "customer");
        Objects.requireNonNull(items, "items");
        Objects.requireNonNull(totalAmount, "totalAmount");
        if (customer.isBlank()) {
            throw new IllegalArgumentException("customer no puede estar vacio");
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("una Order requiere al menos 1 item");
        }
        this.id = id;
        this.customer = customer;
        // Copia defensiva: si el cliente muta la lista pasada, el aggregate no se ve afectado.
        this.items = new ArrayList<>(items);
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
    }

    /**
     * Metodo de negocio: aprueba la orden.
     * Reglas:
     *  - Solo se puede aprobar si esta en PENDING.
     *  - Aprobar dos veces lanza IllegalStateException.
     *  - Se registra un OrderApprovedEvent en la lista de eventos de dominio.
     */
    public void approve() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                "Solo se puede aprobar una orden en estado PENDING. Estado actual: " + this.status);
        }
        this.status = OrderStatus.APPROVED;
        this.domainEvents.add(OrderApprovedEvent.now(this.id));
    }

    // === Getters (sin setters — el estado solo cambia via metodos de negocio) ===

    public OrderId getId() { return id; }
    public String getCustomer() { return customer; }
    /** Devuelve una vista INMUTABLE para que el cliente no pueda modificar la lista interna. */
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public Money getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }

    /** Vista inmutable de los eventos de dominio pendientes de publicar. */
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /** El servicio llama a este metodo despues de publicar los eventos, para vaciar la lista. */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
