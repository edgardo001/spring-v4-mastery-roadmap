package com.springroadmap.modulith.notifications.internal;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Detalle interno del modulo notifications.
 * No debe ser usado desde fuera del paquete 'notifications'.
 */
@Component
public class NotificationHistory {

    private final List<String> entries = new CopyOnWriteArrayList<>();

    public void record(String message) {
        entries.add(message);
    }

    public List<String> all() {
        return List.copyOf(entries);
    }
}
