package com.springroadmap.scheduling.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.springroadmap.scheduling.service.HeartbeatService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Tests del HeartbeatController con MockMvc en modo standalone.
 *
 * Recordatorio Spring Boot 4.1.0:
 *   NO existen @WebMvcTest ni @AutoConfigureMockMvc. Se construye el
 *   MockMvc manualmente pasando el controller instanciado a mano.
 *
 * PREGUNTA DE ALUMNO — "¿Aquí también se disparan las tareas @Scheduled?"
 *   NO. Al no arrancar contexto Spring (sin @SpringBootTest), no hay
 *   scheduler. El HeartbeatService que instanciamos aquí es un POJO
 *   normal: sus contadores solo suben si nosotros llamamos heartbeat()
 *   o cronTick() en el test.
 */
class HeartbeatControllerTest {

    private MockMvc mockMvc;
    private HeartbeatService service;

    @BeforeEach
    void setUp() {
        this.service = new HeartbeatService();
        HeartbeatController controller = new HeartbeatController(service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getHeartbeat_estadoInicial_retornaCerosEnAmbosContadores() throws Exception {
        mockMvc.perform(get("/api/heartbeat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tick").value(0))
                .andExpect(jsonPath("$.cron").value(0));
    }

    @Test
    void getHeartbeat_trasIncrementos_reflejaLosValoresActuales() throws Exception {
        // Simulamos que el scheduler disparó 4 fixedRate y 7 cron ticks.
        service.heartbeat();
        service.heartbeat();
        service.heartbeat();
        service.heartbeat();
        for (int i = 0; i < 7; i++) {
            service.cronTick();
        }

        mockMvc.perform(get("/api/heartbeat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tick").value(4))
                .andExpect(jsonPath("$.cron").value(7));
    }
}
