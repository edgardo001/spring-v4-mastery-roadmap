package com.springroadmap.ai.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.springroadmap.ai.config.LlmProperties;

/**
 * Cliente HTTP manual para un proveedor LLM compatible con OpenAI.
 *
 * Analogia: es el "camarero bilingue" — traduce nuestra peticion en
 * espanol ("dime hola") al JSON que espera la API del proveedor, envia
 * la peticion, y trae de vuelta solo el texto de la respuesta.
 *
 * Este modulo NO usa Spring AI (framework dedicado — se ve en el modulo
 * 58). Aqui hacemos la llamada "a mano" con RestClient para que se
 * entiendan las tripas: cuerpo JSON, extraccion del `choices[0].message
 * .content`, y manejo de la clave API.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *  - Antes: RestTemplate + HttpEntity + HttpHeaders manuales.
 *      HttpHeaders h = new HttpHeaders();
 *      h.set("Authorization", "Bearer " + key);
 *      HttpEntity<Map> req = new HttpEntity<>(body, h);
 *      restTemplate.postForObject(url, req, Map.class);
 *  - Ahora: RestClient fluido, headers y body en una sola cadena.
 *      restClient.post().uri("/chat/completions")
 *                .header("Authorization", ...).body(body)
 *                .retrieve().body(Map.class);
 *
 * NOTA (MEMORY.md): en Spring Boot 4.1.0 NO se autoconfigura
 * `RestClient.Builder` como bean. Por eso lo creamos con
 * `RestClient.builder()` directamente en el constructor.
 */
@Component
public class LlmClient {

    // `final` = el campo se asigna en el constructor y no cambia nunca.
    private final LlmProperties props;
    private final RestClient restClient;

    // Constructor injection: Spring pasa el bean LlmProperties.
    public LlmClient(LlmProperties props) {
        this.props = props;
        // Construimos el RestClient con la baseUrl del proveedor.
        this.restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Envia un mensaje del usuario al LLM y devuelve el texto de la
     * respuesta.
     *
     * Formato del body (estilo OpenAI):
     *   {
     *     "model": "gpt-4o-mini",
     *     "messages": [{"role": "user", "content": "hola"}]
     *   }
     *
     * Formato de la respuesta relevante:
     *   {
     *     "choices": [ {"message": {"content": "Hola, ¿en que ayudo?"}} ]
     *   }
     */
    public String chat(String userMessage) {
        // `Map.of` (Java 9+) crea un mapa inmutable literal.
        // ANTES (Java 8): new HashMap<>() { { put("role", "user"); ... } }.
        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "messages", List.of(Map.of("role", "user", "content", userMessage))
        );

        // `retrieve().body(Map.class)` deserializa el JSON de respuesta
        // en un Map generico (sin necesidad de crear un DTO).
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + props.getApiKey())
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .body(Map.class);

        return extractContent(response);
    }

    /**
     * Placeholder para streaming.
     *
     * Streaming real (Server-Sent Events / Flux<String>) requiere
     * WebFlux, que este modulo NO incluye para mantenerlo simple. En
     * produccion se usaria `WebClient` y se procesarian los eventos
     * `data: {...}` linea a linea.
     */
    public String streamAsBlock(String message) {
        // Placeholder honesto: devolvemos la respuesta completa como si
        // ya se hubiera "streamed", pero en un solo bloque.
        return chat(message);
    }

    /**
     * Extrae `choices[0].message.content` de la respuesta.
     *
     * PREGUNTA DE ALUMNO — "¿por que tantos casts?"
     *   Porque el Map<String, Object> es generico. Cada nivel del JSON
     *   se convierte al tipo Java correspondiente (Map o List) y
     *   debemos indicarlo explicitamente al compilador.
     */
    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        if (response == null) return "";
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) return "";
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null) return "";
        Object content = message.get("content");
        return content == null ? "" : content.toString();
    }
}
