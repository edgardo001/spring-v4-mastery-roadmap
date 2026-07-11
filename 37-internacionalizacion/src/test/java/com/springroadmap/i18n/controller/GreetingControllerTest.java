package com.springroadmap.i18n.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Test del GreetingController usando MockMvc en modo STANDALONE.
 *
 * IMPORTANTE (MEMORY.md, Boot 4.1.0):
 *   - Se prohíbe @WebMvcTest y @AutoConfigureMockMvc (eliminados en Boot 4.1.0).
 *   - En standalone hay que registrar manualmente el LocaleResolver para que
 *     el parámetro Locale del controller sea resuelto desde Accept-Language.
 */
class GreetingControllerTest {

    private MockMvc mockMvc;
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        // Reconstruimos manualmente el MessageSource igual que en I18nConfig.
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:messages/messages");
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        this.messageSource = ms;

        // LocaleResolver que lee Accept-Language, default español.
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.of("es"));

        // Construimos el MockMvc en modo standalone y le enchufamos el LocaleResolver.
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new GreetingController(messageSource))
                .setLocaleResolver(localeResolver)
                .build();
    }

    @Test
    void shouldGreetInEnglish() throws Exception {
        mockMvc.perform(get("/api/greet")
                        .param("name", "Ada")
                        .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Ada!"));
    }

    @Test
    void shouldGreetInSpanish() throws Exception {
        mockMvc.perform(get("/api/greet")
                        .param("name", "Ada")
                        .header("Accept-Language", "es"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hola, Ada!"));
    }

    @Test
    void shouldGreetInFrench() throws Exception {
        mockMvc.perform(get("/api/greet")
                        .param("name", "Ada")
                        .header("Accept-Language", "fr"))
                .andExpect(status().isOk())
                .andExpect(content().string("Bonjour, Ada!"));
    }

    @Test
    void shouldFallbackToSpanishWhenNoHeader() throws Exception {
        mockMvc.perform(get("/api/greet")
                        .param("name", "Ada"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hola, Ada!"));
    }

    /**
     * Test defensivo: valida el MessageSource directamente, sin pasar por MockMvc.
     * Útil si por algún motivo el header Accept-Language no fuera resuelto
     * correctamente en standalone.
     */
    @Test
    void messageSourceShouldResolveEnglishGreetingDirectly() {
        String msg = messageSource.getMessage("greeting", new Object[]{"Ada"}, Locale.ENGLISH);
        org.junit.jupiter.api.Assertions.assertEquals("Hello, Ada!", msg);
    }
}
