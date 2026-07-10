package com.springroadmap.files.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.files.service.FileStorageService;

/**
 * Test MockMvc del {@link FileController} usando el patrón STANDALONE
 * (sin cargar el ApplicationContext completo — más rápido y aislado).
 *
 * <p>Recordatorio (MEMORY.md 2026-07-10): en Spring Boot 4.1.0 NO existe
 * {@code @WebMvcTest}. Usamos {@code MockMvcBuilders.standaloneSetup(...)}
 * que sólo requiere una instancia del controller.</p>
 */
class FileControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Instanciamos el servicio con una carpeta temp del test para no ensuciar disco.
        FileStorageService service = new FileStorageService(tempDir);
        FileController controller = new FileController(service);

        // standaloneSetup: registra el controller en un MockMvc mínimo.
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void uploadReturnsFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "contenido".getBytes());

        mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename", notNullValue()));
    }

    @Test
    void downloadReturnsResource() throws Exception {
        // 1) Subir primero para obtener el nombre generado.
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", "text/plain", "hola bytes".getBytes());

        String json = mockMvc.perform(multipart("/api/files").file(file))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Parseo naive del JSON (evitamos añadir dependencias de test extra).
        // El body es del tipo {"filename":"xxx.txt"}.
        String filename = json.replaceAll(".*\"filename\"\\s*:\\s*\"([^\"]+)\".*", "$1");

        // 2) Descargar el archivo recién subido.
        mockMvc.perform(get("/api/files/{name}", filename))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(content().bytes("hola bytes".getBytes()));
    }
}
