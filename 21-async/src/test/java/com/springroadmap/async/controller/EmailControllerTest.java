package com.springroadmap.async.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.async.service.EmailService;

/**
 * Test del {@link EmailController} usando MockMvc en modo <b>standalone</b>.
 *
 * <p><b>Por qué standalone y NO @WebMvcTest:</b> Spring Boot 4.1.0 ELIMINÓ
 * {@code @WebMvcTest} y {@code @AutoConfigureMockMvc}. El patrón portable en
 * el roadmap es {@code MockMvcBuilders.standaloneSetup(new Controller(...))}.
 *
 * <p>Mockeamos {@link EmailService} para NO tener que arrancar el pool ni
 * dormir 200 ms en el test: retornamos un {@link CompletableFuture} ya
 * completado con el valor esperado.
 */
class EmailControllerTest {

    private MockMvc mockMvc;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Mock manual con Mockito (sin @MockBean, que requiere @SpringBootTest).
        emailService = mock(EmailService.class);
        // El controller usa .get(5, SECONDS): devolvemos un future YA resuelto
        // para que se retorne al instante sin bloquear el test.
        when(emailService.sendEmail(eq("ada@x.com")))
                .thenReturn(CompletableFuture.completedFuture("SENT:ada@x.com"));

        // standaloneSetup: registra el controller SIN levantar todo Spring.
        // Rápido, sin autoconfiguración, ideal para tests de controller unitarios.
        mockMvc = MockMvcBuilders.standaloneSetup(new EmailController(emailService)).build();
    }

    @Test
    void postEmailsReturns200WithSentBody() throws Exception {
        mockMvc.perform(post("/api/emails").param("to", "ada@x.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("SENT:ada@x.com"));
    }
}
