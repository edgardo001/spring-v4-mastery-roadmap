package com.springroadmap.rsocket.controller;

import com.springroadmap.rsocket.domain.Greeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Controlador RSocket. Expone tres de los cuatro modelos de interaccion de RSocket:
 * <ul>
 *   <li><b>request/response</b> - un mensaje pide, un mensaje responde (como HTTP).</li>
 *   <li><b>request/stream</b> - un mensaje pide, el servidor devuelve un flujo de N mensajes.</li>
 *   <li><b>fire-and-forget</b> - el cliente envia y no espera respuesta.</li>
 * </ul>
 *
 * <p>El cuarto modelo, <b>channel</b> (Flux&lt;X&gt; -&gt; Flux&lt;Y&gt;, bidireccional), se deja
 * como ejercicio.</p>
 *
 * <p>{@code @Controller} + {@code @MessageMapping} le dice a Spring que estos metodos
 * se enrutan por la "route" del frame RSocket (no por URL HTTP).</p>
 */
@Controller
public class HelloController {

    // PREGUNTA DE ALUMNO - "por que un logger y no System.out.println?"
    //   Un logger da contexto (hilo, clase, timestamp) y se puede filtrar en produccion.
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    /**
     * request/response: el cliente envia un nombre y recibe UN Greeting.
     * Ruta RSocket: "hello.request".
     */
    @MessageMapping("hello.request")
    public Mono<Greeting> requestResponse(final String name) {
        log.info("[RSocket request/response] name={}", name);
        return Mono.just(new Greeting("Hello, " + name));
    }

    /**
     * request/stream: el cliente pide un stream, el servidor emite 3 Greetings, uno por segundo.
     * Ruta RSocket: "hello.stream".
     *
     * <p>{@code Flux.interval(1s).take(3)} produce 0,1,2 con una pausa de 1 segundo entre elementos.
     * El backpressure lo maneja RSocket a nivel de protocolo (el cliente pide N items via REQUEST_N).</p>
     */
    @MessageMapping("hello.stream")
    public Flux<Greeting> stream(final String name) {
        log.info("[RSocket request/stream] name={}", name);
        return Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .map(i -> new Greeting("#" + i + " " + name));
    }

    /**
     * fire-and-forget: el cliente envia y no espera respuesta (Mono&lt;Void&gt;).
     * Ruta RSocket: "hello.fire".
     */
    @MessageMapping("hello.fire")
    public Mono<Void> fireAndForget(final String name) {
        log.info("[RSocket fire-and-forget] name={}", name);
        return Mono.empty();
    }
}
