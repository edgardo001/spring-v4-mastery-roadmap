package com.springroadmap.files.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

/**
 * Test unitario del {@link FileStorageService}.
 *
 * <p>Usa una carpeta temporal creada por JUnit ({@code @TempDir}) para
 * NO ensuciar el disco del CI ni depender de {@code java.io.tmpdir}.
 * Tras el test JUnit borra la carpeta automáticamente.</p>
 */
class FileStorageServiceTest {

    /**
     * Escenario: guardo un MockMultipartFile y luego lo cargo como Resource.
     * Verifico que:
     *   1. store() devuelve un nombre no vacío.
     *   2. El archivo existe físicamente en la carpeta temporal.
     *   3. load() devuelve un Resource cuyo contenido coincide byte a byte.
     */
    @Test
    void storeAndLoadRoundTrip(@TempDir Path tempDir) throws Exception {
        // Arrange
        FileStorageService service = new FileStorageService(tempDir);

        byte[] content = "hola mundo".getBytes();
        MockMultipartFile mock = new MockMultipartFile(
                "file",              // nombre del parámetro (como el @RequestParam)
                "saludo.txt",         // nombre original del archivo
                "text/plain",         // Content-Type
                content);             // bytes del archivo

        // Act
        String stored = service.store(mock);
        Resource resource = service.load(stored);

        // Assert
        assertThat(stored).isNotBlank().endsWith(".txt");
        assertThat(tempDir.resolve(stored)).exists();
        assertThat(resource.exists()).isTrue();

        byte[] loaded = Files.readAllBytes(tempDir.resolve(stored));
        assertThat(loaded).isEqualTo(content);
    }
}
