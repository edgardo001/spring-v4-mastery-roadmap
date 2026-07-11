package com.springroadmap.mail.controller;

import com.springroadmap.mail.dto.EmailRequest;
import com.springroadmap.mail.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailControllerTest {

    private MockMvc mockMvc;
    private EmailService emailService;

    private static String json(String to, String subject, String body) {
        return "{\"to\":\"" + to + "\",\"subject\":\"" + subject + "\",\"body\":\"" + body + "\"}";
    }

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        EmailController controller = new EmailController(emailService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void postSimple_debeRetornar200YInvocarService() throws Exception {
        mockMvc.perform(post("/api/emails/simple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("a@b.cl", "hi", "hello")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sent"))
                .andExpect(jsonPath("$.type").value("simple"));

        verify(emailService).sendSimple(eq("a@b.cl"), eq("hi"), eq("hello"));
    }

    @Test
    void postHtml_debeRetornar200YInvocarService() throws Exception {
        mockMvc.perform(post("/api/emails/html")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("a@b.cl", "hi", "<h1>hello</h1>")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sent"))
                .andExpect(jsonPath("$.type").value("html"));

        verify(emailService).sendHtml(eq("a@b.cl"), eq("hi"), eq("<h1>hello</h1>"));
    }
}
