package com.springroadmap.modulith2.registry;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Registro de publicaciones de eventos.
 *
 * Antes (modulo 39): los eventos vivian en memoria. Un crash entre el
 * commit del aggregate y la ejecucion del listener perdia la notificacion
 * para siempre.
 *
 * Ahora: cada evento se persiste ANTES de publicarse. Un job de recuperacion
 * puede leer registros con completed_at = null y reprocesarlos al arrancar.
 */
@Component
public class EventPublicationRegistry {

    private final EventPublicationRepository repository;

    public EventPublicationRegistry(EventPublicationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Long register(String aggregateType, String eventType, String serializedEvent) {
        EventPublication publication = new EventPublication(
                aggregateType, eventType, serializedEvent, Instant.now());
        return repository.save(publication).getId();
    }

    @Transactional
    public void markCompleted(Long publicationId) {
        Optional<EventPublication> found = repository.findById(publicationId);
        found.ifPresent(pub -> {
            pub.markCompleted(Instant.now());
            repository.save(pub);
        });
    }

    public List<EventPublication> findByEventType(String eventType) {
        return repository.findByEventType(eventType);
    }

    public List<EventPublication> findAll() {
        return repository.findAll();
    }
}
