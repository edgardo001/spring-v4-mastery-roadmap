package com.springroadmap.async.controller;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springroadmap.async.service.EmailService;

/**
 * Controlador HTTP que expone el endpoint {@code POST /api/emails?to=X}.
 *
 * <p>Delega el envío al {@link EmailService} y espera el resultado con un
 * timeout de 5 segundos, retornando 200 con la cadena "SENT:X". Este ejemplo
 * ilustra el patrón "asíncrono + espera controlada": la tarea pesada corre
 * en el pool, pero el endpoint da una respuesta síncrona al cliente HTTP.
 *
 * <p><b>Analogía</b>: eres un cliente en un banco. El cajero le pasa tu
 * papeleo al backoffice (pool asíncrono) y espera con timeout de 5 minutos
 * la confirmación antes de decirte "listo". Si en 5 min no llega, te dice
 * "lo intentamos más tarde" (504) en vez de tenerte parado eternamente.
 *
 * <hr>
 * <b>ANTES (Java 8) vs AHORA (Java 21)</b>
 * <pre>
 * // ANTES: constructor injection sin final y sin @Autowired explícito era raro;
 * // se usaba field injection (mala práctica hoy):
 * &#64;Autowired private EmailService emailService;
 *
 * // AHORA: constructor injection con campo final. Testeable, inmutable,
 * // sin necesidad de @Autowired (Spring 4.3+ lo infiere).
 * private final EmailService emailService;
 * public EmailController(EmailService emailService) { this.emailService = emailService; }
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué usar .get(5, SECONDS) y no simplemente .get()?"
 * Porque {@code .get()} sin timeout bloquea INDEFINIDAMENTE si el pool está
 * atascado. En un endpoint web esto agota los hilos de Tomcat y tumba el
 * servidor. Timeout explícito = falla rápida.
 */
@RestController                           // = @Controller + @ResponseBody por defecto.
@RequestMapping("/api/emails")            // Prefijo común de todos los endpoints de esta clase.
public class EmailController {

    // Dependencia inmutable (constructor injection).
    private final EmailService emailService;

    // Un solo constructor público → Spring lo detecta automáticamente sin @Autowired.
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Endpoint asíncrono. Ejemplo: {@code curl -X POST "http://localhost:8080/api/emails?to=ada@x.com"}.
     *
     * @param to query param con el destinatario.
     * @return 200 OK con el string "SENT:<to>".
     */
    @PostMapping
    public ResponseEntity<String> send(@RequestParam String to)
            throws InterruptedException, ExecutionException, TimeoutException {
        // .get(5, SECONDS): espera bloqueante hasta 5 s. Si el pool responde
        // en 200 ms (caso feliz), retorna al instante con el valor.
        String result = emailService.sendEmail(to).get(5, TimeUnit.SECONDS);
        return ResponseEntity.ok(result);
    }
}
