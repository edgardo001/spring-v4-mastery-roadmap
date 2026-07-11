package com.springroadmap.cloudconfig;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test unitario del {@link FeatureController} usando MockMvc en modo
 * <b>standalone</b> — patron portable adoptado en el roadmap desde el
 * modulo 02 porque Boot 4.1.0 elimino {@code @WebMvcTest} y las demas
 * anotaciones test-slice.
 *
 * <p>Analogia: en vez de levantar TODO el edificio Spring (lento) para
 * probar la ventanilla, hacemos un simulacro con un controller de
 * carton y verificamos que responde lo correcto.
 *
 * <p>PREGUNTA DE ALUMNO — "¿por que no uso {@code @Autowired MockMvc}?"
 *   Porque eso requiere {@code @AutoConfigureMockMvc}, eliminada en
 *   Boot 4.1.0. La forma portable es
 *   {@code MockMvcBuilders.standaloneSetup(...)}.
 */
class FeatureControllerTest {

    // MockMvc simula el ciclo HTTP sin abrir socket real.
    private MockMvc mockMvc;

    // @BeforeEach corre antes de cada @Test para dejar el estado limpio.
    @BeforeEach
    void setUp() {
        // 1) Preparamos el bean FeatureFlags con valores conocidos.
        FeatureFlags flags = new FeatureFlags();
        flags.setBetaEnabled(true);
        flags.setMaxRetries(5);

        // 2) Instanciamos el controller inyectando el bean a mano.
        FeatureController controller = new FeatureController(flags);

        // 3) Construimos el MockMvc con el patron OBLIGATORIO del roadmap.
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getCurrent_returns200WithConfiguredFlags() throws Exception {
        // perform(get(...)) simula un GET /api/features.
        mockMvc.perform(get("/api/features"))
                // Aserto 1: HTTP 200 OK.
                .andExpect(status().isOk())
                // Aserto 2: el JSON contiene betaEnabled=true.
                .andExpect(jsonPath("$.betaEnabled", equalTo(true)))
                // Aserto 3: el JSON contiene maxRetries=5.
                .andExpect(jsonPath("$.maxRetries", equalTo(5)));
    }
}
