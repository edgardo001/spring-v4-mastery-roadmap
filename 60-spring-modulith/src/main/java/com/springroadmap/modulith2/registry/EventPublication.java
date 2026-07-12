package com.springroadmap.modulith2.registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entidad persistente del Event Publication Registry.
 *
 * Equivalente al schema que Spring Modulith crea automaticamente en
 * su tabla 'event_publication'. Aqui la controlamos nosotros.
 */
@Entity
@Table(name = "event_publication")
public class EventPublication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "serialized_event", nullable = false, length = 4000)
    private String serializedEvent;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected EventPublication() {
    }

    public EventPublication(String aggregateType, String eventType, String serializedEvent, Instant publishedAt) {
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.serializedEvent = serializedEvent;
        this.publishedAt = publishedAt;
    }

    public void markCompleted(Instant when) {
        this.completedAt = when;
    }

    public Long getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
