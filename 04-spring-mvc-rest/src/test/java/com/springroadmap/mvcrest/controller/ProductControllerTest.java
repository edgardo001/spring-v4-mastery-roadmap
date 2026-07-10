package com.springroadmap.mvcrest.controller;

import com.springroadmap.mvcrest.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests del ProductController usando MockMvc en modo "standalone".
 *
 * Recordatorio Spring Boot 4.1.0:
 *   NO existen @WebMvcTest ni @AutoConfigureMockMvc (fueron eliminados).
 *   Construimos MockMvc manualmente con:
 *     MockMvcBuilders.standaloneSetup(new ProductController(repo)).build()
 *
 * Ventaja del standalone: instanciamos el controller nosotros mismos,
 * pasándole el repositorio REAL (in-memory), sin arrancar el contexto Spring.
 * Rápido, aislado y sin magia.
 *
 * NOTA: el repositorio precarga 2 productos (ids 1 y 2) en su constructor.
 */
class ProductControllerTest {

    private MockMvc mockMvc;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        this.repository = new ProductRepository();               // Precarga ids 1 y 2.
        ProductController controller = new ProductController(repository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ---------------- GET all ----------------

    @Test
    void getAll_devuelve200YListaConLosDosProductosPrecargados() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.id==1)].name").exists())
                .andExpect(jsonPath("$[?(@.id==2)].name").exists());
    }

    // ---------------- GET by id ----------------

    @Test
    void getById_conIdExistente_devuelve200YProducto() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cafe 250g"));
    }

    @Test
    void getById_conIdInexistente_devuelve404() throws Exception {
        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound());
    }

    // ---------------- POST ----------------

    @Test
    void create_conJsonValido_devuelve201YAsignaIdYLocation() throws Exception {
        String body = """
                { "name": "Te verde", "price": 2500 }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/3"))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Te verde"))
                .andExpect(jsonPath("$.price").value(2500));
    }

    // ---------------- PUT ----------------

    @Test
    void update_conIdExistente_devuelve200YActualiza() throws Exception {
        String body = """
                { "name": "Cafe 500g", "price": 6990 }
                """;

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cafe 500g"))
                .andExpect(jsonPath("$.price").value(6990));
    }

    @Test
    void update_conIdInexistente_devuelve404() throws Exception {
        String body = """
                { "name": "Fantasma", "price": 1 }
                """;

        mockMvc.perform(put("/api/products/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    // ---------------- DELETE ----------------

    @Test
    void delete_conIdExistente_devuelve204YElRecursoDesaparece() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_conIdInexistente_devuelve404() throws Exception {
        mockMvc.perform(delete("/api/products/9999"))
                .andExpect(status().isNotFound());
    }
}
