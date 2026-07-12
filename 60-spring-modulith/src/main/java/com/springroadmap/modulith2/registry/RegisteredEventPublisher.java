package com.springroadmap.modulith2.registry;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Publisher que combina Spring ApplicationEventPublisher con el registro.
 *
 * Patron: primero persiste la publicacion (dentro de la misma transaccion
 * que el aggregate), despues publica el evento envuelto en un
 * {@link RegisteredEvent} para que el listener sepa cual registro cerrar.
 */
@Component
public class RegisteredEventPublisher {

    private final ApplicationEventPublisher delegate;
    private final EventPublicationRegistry registry;

    public RegisteredEventPublisher(ApplicationEventPublisher delegate,
                                    EventPublicationRegistry registry) {
        this.delegate = delegate;
        this.registry = registry;
    }

    @Transactional
    public void publish(String aggregateType, Object event) {
        String eventType = event.getClass().getName();
        String serialized = event.toString();
        Long publicationId = registry.register(aggregateType, eventType, serialized);
        delegate.publishEvent(new RegisteredEvent(publicationId, event));
    }
}
