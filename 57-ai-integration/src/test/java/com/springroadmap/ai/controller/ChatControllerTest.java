package com.springroadmap.ai.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.ai.client.LlmClient;
import com.springroadmap.ai.config.LlmProperties;

/**
 * Tests del controller usando MockMvc en modo standalone.
 *
 * NOTA (MEMORY.md): `@WebMvcTest` y `@AutoConfigureMockMvc` fueron
 * eliminados en Spring Boot 4.1.0. Usamos `MockMvcBuilders.standaloneSetup`.
 */
class ChatControllerTest {

    @Test
    void returns503WhenApiKeyIsMissing() throws Exception {
        LlmProperties props = new LlmProperties();
        props.setApiKey(""); // sin api key
        LlmClient client = mock(LlmClient.class);

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ChatController(client, props)).build();

        mvc.perform(post("/api/chat").param("message", "hola"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("LLM not configured"));
    }

    @Test
    void returns200WithReplyWhenApiKeyIsPresent() throws Exception {
        LlmProperties props = new LlmProperties();
        props.setApiKey("sk-fake");
        LlmClient client = mock(LlmClient.class);
        when(client.chat(anyString())).thenReturn("Hola humano");

        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ChatController(client, props)).build();

        mvc.perform(post("/api/chat").param("message", "hola"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Hola humano"));
    }
}
