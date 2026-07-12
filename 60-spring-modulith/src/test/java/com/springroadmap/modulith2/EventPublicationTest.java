package com.springroadmap.modulith2;

import com.springroadmap.modulith2.orders.OrderCreatedEvent;
import com.springroadmap.modulith2.registry.EventPublication;
import com.springroadmap.modulith2.registry.EventPublicationRegistry;
import com.springroadmap.modulith2.registry.RegisteredEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica el ciclo de vida del Event Publication Registry:
 * 1) Publicar un evento => aparece registro con completed_at = null.
 * 2) Tras el commit, el listener async lo procesa y setea completed_at.
 */
@SpringBootTest
class EventPublicationTest {

    @Autowired
    private RegisteredEventPublisher publisher;

    @Autowired
    private EventPublicationRegistry registry;

    @Test
    void publicationIsPersistedAndThenCompletedAsync() throws InterruptedException {
        String eventType = OrderCreatedEvent.class.getName();

        publisher.publish("Order", new OrderCreatedEvent(42L, "Cliente Prueba"));

        List<EventPublication> initial = registry.findByEventType(eventType);
        assertThat(initial).isNotEmpty();
        EventPublication first = initial.get(0);
        assertThat(first.getPublishedAt()).isNotNull();
        // completed_at aun puede ser null: el listener corre en otro hilo tras el commit.

        Thread.sleep(500);

        List<EventPublication> after = registry.findByEventType(eventType);
        assertThat(after)
                .as("todas las publicaciones deben estar completadas tras el listener async")
                .allSatisfy(pub -> assertThat(pub.getCompletedAt()).isNotNull());
    }
}
