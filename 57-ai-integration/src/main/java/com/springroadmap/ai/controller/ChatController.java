package com.springroadmap.ai.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springroadmap.ai.client.LlmClient;
import com.springroadmap.ai.config.LlmProperties;

/**
 * Endpoint HTTP para enviar mensajes al LLM.
 *
 * Analogia: es la "ventanilla de atencion" — recibe la peticion del
 * cliente y decide si delegar al camarero (LlmClient) o rechazarla
 * porque falta configuracion.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *  - Antes: `@RequestMapping(value = "/chat", method = POST)`.
 *  - Ahora: `@PostMapping("/chat")` — mas corto y expresivo.
 *
 *  - Antes: `Map<String, String> r = new HashMap<>(); r.put("k","v");`
 *  - Ahora: `Map.of("k", "v")` — literal inmutable.
 */
@RestController
@RequestMapping("/api")
public class ChatController {

    private final LlmClient client;
    private final LlmProperties props;

    public ChatController(LlmClient client, LlmProperties props) {
        this.client = client;
        this.props = props;
    }

    /**
     * POST /api/chat?message=hola
     *
     * - 503 si `llm.api-key` no esta configurada (fail-fast).
     * - 200 con `{"reply": "..."}` si el LLM responde.
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestParam("message") String message) {
        // isBlank() (Java 11+): true si es null-vacio-solo-espacios.
        // ANTES (Java 8): apiKey == null || apiKey.trim().isEmpty().
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "LLM not configured"));
        }
        String reply = client.chat(message);
        return ResponseEntity.ok(Map.of("reply", reply));
    }
}
