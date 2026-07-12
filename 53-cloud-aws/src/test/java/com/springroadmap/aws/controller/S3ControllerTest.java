package com.springroadmap.aws.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.aws.service.S3Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del S3Controller usando MockMvc standalone (patrón portable del roadmap —
 * Boot 4.1.0 NO trae @WebMvcTest, ver MEMORY.md módulo 02).
 */
class S3ControllerTest {

    private S3Service s3ServiceMock;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        s3ServiceMock = mock(S3Service.class);
        // `standaloneSetup(...)` = arma un MockMvc SIN levantar contexto Spring.
        // Solo registra el controller que le pasamos (con sus deps mockeadas).
        mockMvc = MockMvcBuilders.standaloneSetup(new S3Controller(s3ServiceMock)).build();
    }

    @Test
    void upload_returns200AndCallsService() throws Exception {
        byte[] body = "contenido de prueba".getBytes();

        mockMvc.perform(post("/api/s3/mi-key")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("mi-key")));

        // Verifica que el service fue llamado con la key y bytes correctos.
        verify(s3ServiceMock).upload(eq("mi-key"), any(byte[].class));
    }

    @Test
    void download_returnsBytesFromService() throws Exception {
        byte[] fakeContent = "data-from-s3".getBytes();
        when(s3ServiceMock.download("mi-key")).thenReturn(fakeContent);

        mockMvc.perform(get("/api/s3/mi-key"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fakeContent));
    }
}
