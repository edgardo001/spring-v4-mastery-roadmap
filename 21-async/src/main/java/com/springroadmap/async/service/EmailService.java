package com.springroadmap.async.service;

// CompletableFuture: contenedor de "un valor que llegará en el futuro".
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// @Async: marca el método para ejecutarse fuera del hilo actual.
import org.springframework.scheduling.annotation.Async;
// @Service: indica a Spring que esta clase es un componente de "lógica de negocio".
import org.springframework.stereotype.Service;

/**
 * Servicio que simula el envío de un correo electrónico.
 *
 * <p>El método {@link #sendEmail(String)} está marcado con {@code @Async}, por
 * lo que Spring intercepta la llamada mediante un proxy AOP y despacha su
 * ejecución al bean {@code taskExecutor} definido en {@code AsyncConfig}. El
 * llamador recibe inmediatamente un {@link CompletableFuture} vacío, sin
 * bloquearse por los 200 ms del envío simulado.
 *
 * <p><b>Analogía</b>: pedirle a un compañero de trabajo que lleve una carta
 * al correo. Le entregas el sobre (invocas el método), él dice "yo me
 * encargo" y tú puedes seguir con tus tareas; más tarde te pasa el acuse de
 * recibo (el {@code CompletableFuture} se completa con el resultado).
 *
 * <hr>
 * <b>ANTES (Java 8) vs AHORA (Java 21)</b>
 * <pre>
 * // ANTES: crear el hilo manualmente y comunicarse por variables compartidas.
 * final String[] resultado = new String[1];
 * Thread t = new Thread(new Runnable() {
 *     public void run() {
 *         Thread.sleep(200);            // (envuelto en try/catch)
 *         resultado[0] = "SENT:" + to;
 *     }
 * });
 * t.start();
 * t.join();                             // bloqueo hasta que termine
 * String r = resultado[0];
 *
 * // AHORA: una anotación y un CompletableFuture. Legible y componible.
 * CompletableFuture&lt;String&gt; f = emailService.sendEmail(to);
 * String r = f.get();                   // o .thenApply(...) sin bloquear.
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué el método NO puede ser llamado desde la
 * MISMA clase (self-invocation)?" Porque Spring implementa {@code @Async}
 * envolviendo el bean en un PROXY. Si el método interno llama a otro método
 * de la misma instancia, la llamada pasa por {@code this} directamente,
 * saltándose el proxy y por lo tanto la asincronía. Regla práctica: los
 * métodos {@code @Async} se invocan desde OTRO bean (controller o service).
 */
@Service
public class EmailService {

    // Logger estático estándar (regla DevOps: nada de System.out.println).
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * Envía un correo de forma asíncrona.
     *
     * @param to dirección destino, ej. "ada@x.com".
     * @return futuro con la cadena "SENT:" + to una vez completado.
     */
    // @Async("taskExecutor"): usa EXPLÍCITAMENTE el pool que definimos en
    // AsyncConfig. Sin el nombre, Spring elige el primer Executor que
    // encuentre; ser explícitos evita sorpresas cuando aparecen varios pools.
    @Async("taskExecutor")
    public CompletableFuture<String> sendEmail(String to) {
        // Registro del hilo en el que se está ejecutando: en el hilo HTTP
        // se ve "http-nio-8080-exec-1"; aquí veremos "async-task-1".
        log.info("Enviando email a {} en hilo {}", to, Thread.currentThread().getName());

        try {
            // Simulamos latencia (llamada SMTP, API externa, etc.).
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // Buena práctica: si nos interrumpen, restaurar el flag y salir.
            Thread.currentThread().interrupt();
            // Retornamos un future YA fallido para que el llamador se entere.
            return CompletableFuture.failedFuture(e);
        }

        // CompletableFuture.completedFuture(x) fabrica un futuro YA resuelto
        // con el valor x. Necesario porque @Async exige que el método retorne
        // un CompletableFuture (o void); no puede retornar String directo.
        return CompletableFuture.completedFuture("SENT:" + to);
    }
}
