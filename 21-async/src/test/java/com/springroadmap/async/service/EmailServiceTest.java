package com.springroadmap.async.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de integración del {@link EmailService}. Al ser {@code @SpringBootTest}
 * levanta el contexto Spring COMPLETO, con el pool "taskExecutor" real. Esto
 * verifica que:
 *   1. La anotación {@code @Async} se está aplicando (proxy AOP correcto).
 *   2. El pool corre la tarea y retorna el valor esperado.
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué no uso Mockito y me ahorro @SpringBootTest?"
 * Porque @Async es efectiva SOLO cuando el bean pasa por el proxy de Spring.
 * Instanciar {@code new EmailService()} a mano ejecuta el método en el hilo
 * de test, sin asincronía. Para probar comportamiento asíncrono real, hace
 * falta que Spring cree el bean.
 */
@SpringBootTest
class EmailServiceTest {

    // Inyección por campo SOLO en tests (aceptable y común aquí).
    @Autowired
    private EmailService emailService;

    @Test
    void sendEmailReturnsSentPrefix() throws ExecutionException, InterruptedException {
        // .get() (sin timeout) es aceptable en tests: si algo se cuelga,
        // JUnit corta con su propio timeout global. Aquí queremos el valor
        // final para asertar.
        String result = emailService.sendEmail("ada@x.com").get();

        assertThat(result).isEqualTo("SENT:ada@x.com");
    }
}
