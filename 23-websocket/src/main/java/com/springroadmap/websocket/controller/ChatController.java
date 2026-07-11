package com.springroadmap.websocket.controller;

import com.springroadmap.websocket.dto.ChatMessage;

// @MessageMapping = "cuando llegue un mensaje STOMP a este destino, ejecutá este método".
// Es al mundo WebSocket lo que @GetMapping/@PostMapping es al mundo HTTP.
import org.springframework.messaging.handler.annotation.MessageMapping;

// @SendTo = "el valor de retorno de este método, publicalo en este destino del broker".
import org.springframework.messaging.handler.annotation.SendTo;

// @Controller (no @RestController): en WebSocket no devolvemos respuestas HTTP,
// devolvemos objetos que Spring publica en el broker via @SendTo.
import org.springframework.stereotype.Controller;

import java.time.Instant;

/**
 * Controlador de chat por WebSocket.
 *
 * FLUJO:
 *   1) El cliente envía a "/app/chat.send" un JSON: {"from":"ada","content":"hola"}
 *   2) Spring lo deserializa a ChatMessage y llama a sendMessage(...).
 *   3) sendMessage devuelve un ChatMessage nuevo con timestamp asignado.
 *   4) @SendTo hace que ese objeto se publique en "/topic/messages".
 *   5) Todos los clientes suscritos a "/topic/messages" lo reciben en tiempo real.
 *
 * ANALOGÍA:
 *   Es el LOCUTOR de la radio: recibe el mensaje de un oyente (via /app/chat.send),
 *   lo sella con la hora (timestamp) y lo repite por la antena (/topic/messages)
 *   para que todos los sintonizados lo escuchen a la vez.
 *
 * ANTES (long polling con HTTP):
 *   Cada 2 segundos: GET /messages?since=... — costoso, tardío, consume batería.
 * AHORA (WebSocket + STOMP):
 *   Conexión abierta; el servidor EMPUJA al instante. Latencia ~10 ms.
 */
@Controller
public class ChatController {

    /**
     * Procesa un mensaje entrante del cliente y lo re-emite al topic público.
     *
     * PREGUNTA DE ALUMNO — "¿por qué construyo un ChatMessage nuevo en vez de reusar el que llega?"
     *   Porque el timestamp debe ser autoritativo del SERVIDOR, no del cliente
     *   (el reloj del navegador puede estar mal, o el cliente puede mentir).
     *   Además, un record es inmutable: no puedo modificarle el timestamp al original.
     *
     * @param incoming mensaje enviado por el cliente (deserializado del JSON).
     * @return mensaje enriquecido con timestamp del servidor, que @SendTo publica al broker.
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage incoming) {
        // Instant.now() = "ahora mismo en UTC". Se usa a propósito Instant y no LocalDateTime
        // para no depender de la zona horaria del servidor (ver MEMORY.md, error #22).
        return new ChatMessage(
                incoming.from(),          // record accessor: se llama from(), NO getFrom().
                incoming.content(),
                Instant.now()
        );
    }
}
