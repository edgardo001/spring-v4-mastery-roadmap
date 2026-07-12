package com.springroadmap.springai.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChatClientSimulatorTest {

    private final ChatClientSimulator client = new ChatClientSimulator(
            "Eres un asistente util.", "gpt-4o-mini-sim", 0.7);

    @Test
    void fluentApiGeneratesContent() {
        final String content = client.prompt()
                .system("Eres un tutor de Spring")
                .user("Que es un bean?")
                .call()
                .content();

        assertTrue(content.contains("Eres un tutor de Spring"));
        assertTrue(content.contains("Que es un bean?"));
        assertTrue(content.contains("gpt-4o-mini-sim"));
    }

    @Test
    void temperatureAndModelOverrideDefaults() {
        final String content = client.prompt()
                .user("hola")
                .temperature(0.0)
                .model("mi-modelo")
                .call()
                .content();

        assertTrue(content.contains("mi-modelo"));
        assertTrue(content.contains("temp=0.0"));
    }

    @Test
    void userIsRequired() {
        assertThrows(IllegalStateException.class, () -> client.prompt().system("x").call());
    }

    @Test
    void advisorPostProcessesResponse() {
        client.addAdvisor(text -> text.toUpperCase());
        final String content = client.prompt().user("hola").call().content();
        assertEquals(content, content.toUpperCase());
    }
}
