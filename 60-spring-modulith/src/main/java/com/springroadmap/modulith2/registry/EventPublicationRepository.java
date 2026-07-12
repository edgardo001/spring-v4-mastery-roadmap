package com.springroadmap.modulith2.registry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventPublicationRepository extends JpaRepository<EventPublication, Long> {

    List<EventPublication> findByEventType(String eventType);
}
