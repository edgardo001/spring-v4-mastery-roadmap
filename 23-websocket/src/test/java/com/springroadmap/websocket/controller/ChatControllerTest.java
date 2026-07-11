package com.springroadmap.websocket.controller;

import com.springroadmap.websocket.dto.ChatMessage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test UNITARIO del ChatController.
 *
 * Por qué así y no con un WebSocket real:
 *   Levantar un STOMP client + broker in-memory desde JUnit es posible pero costoso
 *   (StompSession, ListenableFuture, timeouts flaky). Para el propósito pedagógico
 *   de este módulo alcanza con probar la LÓGICA del handler: "toma un mensaje sin
 *   timestamp y devuelve uno con timestamp del servidor".
 *
 * ANTES (JUnit 4):  @Test public void test() { ... assertEquals(...); }
 * AHORA (JUnit 5 + AssertJ): fluent assertions, mejor mensaje de error.
 */
class ChatControllerTest {

    @Test
    void sendMessage_assignsServerTimestampAndPreservesPayload() {
        // ARRANGE — instanciamos el controller directo (no hace falta contexto Spring).
        ChatController controller = new ChatController();
        ChatMessage incoming = new ChatMessage("ada", "hola", null);

        // ACT — invocamos el método como si fuera un POJO.
        ChatMessage result = controller.sendMessage(incoming);

        // ASSERT — from y content preservados; timestamp asignado por el servidor.
        assertThat(result).isNotNull();
        assertThat(result.from()).isEqualTo("ada");
        assertThat(result.content()).isEqualTo("hola");
        assertThat(result.timestamp())
                .as("El servidor debe estampar el timestamp autoritativo")
                .isNotNull();
    }
}
