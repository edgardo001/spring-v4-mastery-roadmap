package com.springroadmap.tests.controller;

import com.springroadmap.tests.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del {@link InvoiceController} con MockMvc en modo STANDALONE.
 *
 * ¿POR QUÉ STANDALONE?
 *   En Spring Boot 4.1.0 se eliminó {@code @WebMvcTest}. Debemos construir
 *   {@code MockMvc} manualmente:
 *     MockMvcBuilders.standaloneSetup(new InvoiceController(mockService)).build()
 *
 *   Ventajas:
 *     - NO arranca el contexto de Spring (test ultra-rápido).
 *     - Nosotros controlamos las dependencias (pasamos mocks al constructor).
 *     - Aísla la lógica del controller (routing, binding de parámetros).
 *
 * Aquí mockeamos {@code InvoiceService} con {@code Mockito.mock(...)} de forma
 * MANUAL (sin @Mock) para mostrar la alternativa "explícita".
 */
class InvoiceControllerTest {

    private InvoiceService invoiceServiceMock;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        invoiceServiceMock = mock(InvoiceService.class);
        InvoiceController controller = new InvoiceController(invoiceServiceMock);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getInvoice_devuelve200ConTotal() throws Exception {
        // ARRANGE: cuando el service reciba cualquier double,double, devuelve 1071.0
        when(invoiceServiceMock.calculateInvoice(anyDouble(), anyDouble())).thenReturn(1071.0);

        // ACT + ASSERT
        mockMvc.perform(get("/api/invoice")
                        .param("base", "1000")
                        .param("discount", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("1071.0"));

        // VERIFY: el controller delegó al service con los valores exactos parseados.
        verify(invoiceServiceMock).calculateInvoice(1000.0, 10.0);
    }

    @Test
    void getInvoice_sinParametros_devuelve400() throws Exception {
        // Si faltan @RequestParam obligatorios, Spring MVC responde 400 Bad Request.
        mockMvc.perform(get("/api/invoice"))
                .andExpect(status().isBadRequest());
    }
}
