package com.springroadmap.springai.chat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ChatControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        final ChatClientSimulator sim = new ChatClientSimulator(
                "Eres un asistente util.", "gpt-4o-mini-sim", 0.7);
        final ChatController controller = new ChatController(sim);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void chatReturnsSimulatedContent() throws Exception {
        final String body = "{\"message\":\"Explicame RAG\",\"system\":\"Eres profesor\"}";

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").value(org.hamcrest.Matchers.containsString("Explicame RAG")))
                .andExpect(jsonPath("$.content").value(org.hamcrest.Matchers.containsString("Eres profesor")));
    }

    @Test
    void chatRejectsBlankMessage() throws Exception {
        final String body = "{\"message\":\"\"}";

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
