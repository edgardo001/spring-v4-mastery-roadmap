package com.springroadmap.jdbc.controller;

import com.springroadmap.jdbc.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del CustomerController con MockMvc en modo "standalone",
 * pero cableado con el CustomerRepository REAL (H2 en memoria).
 *
 * PATRÓN CLAVE (Spring Boot 4.1.0 no tiene @WebMvcTest):
 *   - Usamos @SpringBootTest para levantar el contexto completo (DataSource + repos).
 *   - Inyectamos el CustomerController real vía @Autowired.
 *   - Construimos MockMvc con MockMvcBuilders.standaloneSetup(controller).
 *   Así probamos el endpoint HTTP sin arrancar Tomcat, pero con acceso real a la BD.
 */
@SpringBootTest
class CustomerControllerTest {

    @Autowired
    private CustomerController controller;

    @Autowired
    private CustomerRepository repository; // para asserts complementarios

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAll_devuelve200ConLista() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void getById_conIdExistente_devuelve200() throws Exception {
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ada Lovelace"));
    }

    @Test
    void getById_conIdInexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/customers/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void post_creaCustomer_yDevuelve201ConLocation() throws Exception {
        final String body = """
                { "name": "Linus Torvalds", "email": "linus@example.com" }
                """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/customers/")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Linus Torvalds"));
    }

    @Test
    void delete_conIdExistente_devuelve204() throws Exception {
        // Insertamos uno para poder borrarlo sin depender del seed.
        final var saved = repository.save(new com.springroadmap.jdbc.domain.Customer(
                null, "Temp User", "temp@example.com"));

        mockMvc.perform(delete("/api/customers/" + saved.id()))
                .andExpect(status().isNoContent());
    }
}
