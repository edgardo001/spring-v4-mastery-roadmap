package com.springroadmap.di.controller;

import com.springroadmap.di.repository.UserRepository;
import com.springroadmap.di.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del UserController usando MockMvc en modo "standalone".
 *
 * Recordatorio Spring Boot 4.1.0:
 *   NO existen @WebMvcTest ni @AutoConfigureMockMvc.
 *   Construimos MockMvc manualmente con:
 *     MockMvcBuilders.standaloneSetup(new UserController(repo, svc)).build()
 *
 * Ventaja del standalone: instanciamos el controller nosotros mismos,
 * pasándole las dependencias REALES (POJOs sin efectos secundarios), sin
 * necesidad de mocks ni de arrancar el contexto.
 */
class UserControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserRepository repository = new UserRepository();       // Ada Lovelace precargada
        NotificationService service = new NotificationService();
        UserController controller = new UserController(repository, service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void notify_conUsuarioExistente_devuelve200ConMensajeFormateado() throws Exception {
        mockMvc.perform(post("/api/users/notify")
                        .param("email", "ada@example.com")
                        .param("msg", "hola"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "NOTIFIED:Ada Lovelace|EMAIL_SENT_TO:ada@example.com"));
    }

    @Test
    void notify_conUsuarioInexistente_devuelve404() throws Exception {
        mockMvc.perform(post("/api/users/notify")
                        .param("email", "desconocido@example.com")
                        .param("msg", "hola"))
                .andExpect(status().isNotFound());
    }
}
