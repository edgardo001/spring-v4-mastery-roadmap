package com.springroadmap.di.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test UNITARIO puro (sin Spring) del NotificationService.
 *
 * ¿Por qué "sin Spring"?
 *   Porque NotificationService no depende de nadie (no tiene constructor
 *   con parámetros), así que podemos instanciarlo con `new`. Es MUCHO más
 *   rápido que arrancar el contexto (milisegundos vs segundos).
 *
 * Regla general: si la clase no requiere Spring para funcionar,
 * pruébala como un POJO normal con JUnit.
 */
class NotificationServiceTest {

    @Test
    void sendEmail_devuelvePrefijoConEmail() {
        // ARRANGE
        NotificationService service = new NotificationService();

        // ACT
        String result = service.sendEmail("ada@example.com", "hola");

        // ASSERT
        assertNotNull(result);
        assertEquals("EMAIL_SENT_TO:ada@example.com", result);
    }

    @Test
    void sendEmail_conOtroEmail_devuelveEsePropio() {
        NotificationService service = new NotificationService();

        String result = service.sendEmail("grace@example.com", "test");

        assertEquals("EMAIL_SENT_TO:grace@example.com", result);
    }
}
