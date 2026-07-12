package com.springroadmap.springai.chat;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClientSimulator chatClient;

    public ChatController(final ChatClientSimulator chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * POST /api/chat  body: { "message": "...", "system": "..." (opcional) }
     * Responde con la salida del ChatClient (simulado).
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody final ChatRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "message es obligatorio"));
        }

        final String content = chatClient.prompt()
                .system(request.system())
                .user(request.message())
                .call()
                .content();

        return ResponseEntity.ok(Map.of("content", content));
    }

    public record ChatRequest(String message, String system) {
    }
}
