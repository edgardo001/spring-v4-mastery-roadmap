package com.springroadmap.resilience.controller;

import com.springroadmap.resilience.service.FlakyService;
import com.springroadmap.resilience.service.ResilientClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test MockMvc standalone del {@link ResilienceController}.
 *
 * <p>NOTA (MEMORY.md): en Spring Boot 4.1.0 fue eliminado {@code @WebMvcTest}.
 * Patrón portable: {@code MockMvcBuilders.standaloneSetup(new Controller(...))}.
 * Aquí construimos manualmente el {@link ResilientClient} con un CB + Retry
 * ad-hoc para no depender del contexto Spring completo.</p>
 */
class ResilienceControllerTest {

    private MockMvc mockMvc;
    private FlakyService flakyService;

    @BeforeEach
    void setUp() {
        // 1) Servicio flaky nuevo por test (contador en cero).
        flakyService = new FlakyService();

        // 2) Retry con maxAttempts generoso para asegurar éxito en 1 request.
        Retry retry = Retry.of("test-retry",
                RetryConfig.custom()
                        .maxAttempts(10)
                        .waitDuration(Duration.ofMillis(10))
                        .retryOnException(ex -> ex instanceof RuntimeException)
                        .build());

        // 3) CircuitBreaker con ventana chica.
        CircuitBreaker cb = CircuitBreaker.of("test-cb",
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(90f)
                        .slidingWindowSize(20)
                        .minimumNumberOfCalls(20)
                        .build());

        // 4) Cliente + Controller cableados a mano.
        ResilientClient client = new ResilientClient(flakyService, cb, retry);
        ResilienceController controller = new ResilienceController(client);

        // 5) MockMvc standalone (sin cargar el contexto de Spring).
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getCallDevuelve200YRespuestaOK() throws Exception {
        mockMvc.perform(get("/api/resilience/call"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("OK from FlakyService")));
    }
}
