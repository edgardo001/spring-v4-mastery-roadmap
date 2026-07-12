package com.springroadmap.springai.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Simulador de la API fluida de Spring AI {@code ChatClient}.
 *
 * <p>Spring AI 1.x (GA 2025) esta compilado contra Spring Boot 3.x y todavia no
 * publica una linea compatible con Spring Boot 4.1.0. Para no bloquear el modulo
 * exponemos un {@code ChatClientSimulator} que imita la API idiomatica:
 *
 * <pre>{@code
 * chatClient.prompt()
 *     .system("Eres un asistente medico")
 *     .user("Como se trata la gripe?")
 *     .call()
 *     .content();
 * }</pre>
 *
 * <p>Cuando Spring AI publique la version para Boot 4, basta con reemplazar este
 * bean por {@code org.springframework.ai.chat.client.ChatClient} y borrar el
 * simulador; los llamadores no cambian su forma de invocarlo.
 */
@Component
public class ChatClientSimulator {

    private final String defaultSystem;
    private final String defaultModel;
    private final double defaultTemperature;
    private final List<UnaryOperator<String>> advisors = new ArrayList<>();

    public ChatClientSimulator(
            @Value("${springai.simulator.default-system:Eres un asistente util.}") final String defaultSystem,
            @Value("${springai.simulator.default-model:gpt-4o-mini-sim}") final String defaultModel,
            @Value("${springai.simulator.temperature:0.7}") final double defaultTemperature) {
        this.defaultSystem = defaultSystem;
        this.defaultModel = defaultModel;
        this.defaultTemperature = defaultTemperature;
    }

    /**
     * Registra un "advisor" (post-procesador) que se ejecuta sobre la respuesta.
     * En Spring AI real esto seria un {@code Advisor} que puede envolver
     * request + response (memoria, logging, RAG).
     */
    public ChatClientSimulator addAdvisor(final UnaryOperator<String> advisor) {
        this.advisors.add(advisor);
        return this;
    }

    /** Punto de entrada fluido: espeja {@code ChatClient#prompt()}. */
    public PromptSpec prompt() {
        return new PromptSpec(this);
    }

    String simulate(final String system, final String user, final Double temperature, final String model) {
        final String sys = system != null ? system : defaultSystem;
        final double temp = temperature != null ? temperature : defaultTemperature;
        final String mdl = model != null ? model : defaultModel;

        // Motor simulado: no llama a un LLM real, solo genera una respuesta
        // deterministica que refleja el prompt para poder testear la cadena.
        String content = "[modelo=" + mdl + " temp=" + temp + "] "
                + "Sistema: '" + sys + "'. "
                + "Respuesta simulada a: '" + user + "'.";

        for (final UnaryOperator<String> advisor : advisors) {
            content = advisor.apply(content);
        }
        return content;
    }

    /** Segundo eslabon de la cadena fluida. */
    public static final class PromptSpec {
        private final ChatClientSimulator client;
        private String system;
        private String user;
        private Double temperature;
        private String model;

        private PromptSpec(final ChatClientSimulator client) {
            this.client = client;
        }

        public PromptSpec system(final String systemMessage) {
            this.system = systemMessage;
            return this;
        }

        public PromptSpec user(final String userMessage) {
            this.user = userMessage;
            return this;
        }

        public PromptSpec temperature(final double temperature) {
            this.temperature = temperature;
            return this;
        }

        public PromptSpec model(final String modelName) {
            this.model = modelName;
            return this;
        }

        public CallSpec call() {
            if (user == null || user.isBlank()) {
                throw new IllegalStateException("user() es obligatorio antes de call()");
            }
            return new CallSpec(client.simulate(system, user, temperature, model));
        }
    }

    /** Tercer eslabon: expone {@code content()} como el ChatClient real. */
    public static final class CallSpec {
        private final String content;

        private CallSpec(final String content) {
            this.content = content;
        }

        public String content() {
            return content;
        }
    }
}
