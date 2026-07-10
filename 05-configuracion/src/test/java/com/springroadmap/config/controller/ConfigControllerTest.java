package com.springroadmap.config.controller;

import com.springroadmap.config.props.AppProperties;
import com.springroadmap.config.service.LegacyValueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del ConfigController con MockMvc en modo STANDALONE.
 *
 * Spring Boot 4.1.0 eliminó @WebMvcTest y @AutoConfigureMockMvc, así que
 * construimos MockMvc a mano y le pasamos un controller con dependencias
 * "de prueba" creadas por nosotros (no arranca el contexto Spring).
 *
 * Este test verifica los valores por defecto (los que están en
 * application.yml), sin activar ningún perfil.
 */
class ConfigControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Construimos AppProperties directamente (record inmutable), tal
        // como si Spring hubiera leído application.yml por defecto.
        AppProperties props = new AppProperties(
                "Configuracion Roadmap (default)",
                "1.0.0",
                new AppProperties.Features(false, 100)
        );
        // El LegacyValueService normalmente recibe el @Value, pero en
        // standalone lo instanciamos con el mismo nombre para verificar
        // que el controller lo combina correctamente en la respuesta.
        LegacyValueService legacy = new LegacyValueService("Configuracion Roadmap (default)");
        ConfigController controller = new ConfigController(props, legacy);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getConfig_devuelveValoresPorDefecto() throws Exception {
        mockMvc.perform(get("/api/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Configuracion Roadmap (default)"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.emailEnabled").value(false))
                .andExpect(jsonPath("$.maxUsers").value(100))
                .andExpect(jsonPath("$.legacyGreeting")
                        .value("Legacy dice: hola desde Configuracion Roadmap (default)"));
    }
}
