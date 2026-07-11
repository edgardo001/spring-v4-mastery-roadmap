package com.springroadmap.restclient.controller;

import com.springroadmap.restclient.dto.Todo;
import com.springroadmap.restclient.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del controlador en modo standalone (sin arrancar ApplicationContext).
 * Se mockea el TodoService con Mockito y se enruta manualmente el controlador.
 *
 * Antes vs Ahora:
 *  - Antes: @WebMvcTest cargaba parte del contexto Spring (mas lento).
 *  - Ahora (standalone): MockMvcBuilders.standaloneSetup(controller) es
 *    ultra-rapido y aisla al 100% la capa web.
 */
class TodoControllerTest {

    private MockMvc mockMvc;
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = mock(TodoService.class);
        TodoController controller = new TodoController(todoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getById_devuelve200YJson() throws Exception {
        when(todoService.fetch(eq(1L))).thenReturn(new Todo(1L, "hola", true));

        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("hola"))
                .andExpect(jsonPath("$.completed").value(true));
    }
}
