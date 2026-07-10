package com.springroadmap.cache.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springroadmap.cache.service.SlowService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test MockMvc en modo standalone: NO levanta el contexto de Spring.
 *
 * OJO: en standalone NO hay proxy de caché. Aquí solo verificamos el binding
 * HTTP (status 200 y payload). Los tests de caché reales están en
 * {@link com.springroadmap.cache.service.SlowServiceCacheTest}.
 *
 * Instanciamos SlowService directamente porque no depende de otros beans.
 */
class ItemControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SlowService service = new SlowService();
        ItemController controller = new ItemController(service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getItemDevuelve200YPayload() throws Exception {
        mockMvc.perform(get("/api/items/42"))
                .andExpect(status().isOk())
                .andExpect(content().string("ITEM_42"));
    }
}
