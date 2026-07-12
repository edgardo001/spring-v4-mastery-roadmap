package com.springroadmap.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades tipadas para el proveedor LLM (OpenAI-compatible).
 *
 * Analogia: es la "caja de configuracion" del cliente HTTP. En vez de
 * usar `@Value("${...}")` disperso por la app, agrupamos todo en una
 * clase con getters/setters (bean estilo Java 8) y Spring la puebla
 * automaticamente desde application.yml.
 *
 * `@ConfigurationProperties(prefix = "llm")` significa que las claves
 * `llm.api-key`, `llm.base-url`, `llm.model` en el yml se enlazan a los
 * campos apiKey, baseUrl, model respectivamente (kebab-case → camelCase).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *  - Antes: usar `@Value("${llm.api-key}") String apiKey;` en cada bean.
 *           Se ensucia el codigo y no hay tipado seguro.
 *  - Ahora: una unica clase `@ConfigurationProperties` reune todo.
 *           Podria ser un `record`, pero para permitir mutabilidad y
 *           valores por defecto simples usamos POJO clasico (mas familiar
 *           al lector proveniente de Java 8).
 *
 * PREGUNTA DE ALUMNO — "¿por que no un record?"
 *   Los records son inmutables y su binding requiere constructor
 *   completo. Un POJO acepta valores por defecto directos en el campo,
 *   lo cual es mas legible para principiantes.
 */
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    // Clave secreta del proveedor. Se lee desde OPENAI_API_KEY en el yml.
    // Si esta vacia, el controller responde 503 (LLM not configured).
    private String apiKey = "";

    // URL base del endpoint compatible con OpenAI.
    private String baseUrl = "https://api.openai.com/v1";

    // Modelo por defecto. `gpt-4o-mini` = barato y rapido para demos.
    private String model = "gpt-4o-mini";

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
