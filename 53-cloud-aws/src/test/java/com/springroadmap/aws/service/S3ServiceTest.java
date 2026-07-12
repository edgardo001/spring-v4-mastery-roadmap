package com.springroadmap.aws.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test unitario del S3Service. Mockea el S3Client y verifica que:
 *  - `upload` invoca `putObject` con el bucket y key correctos.
 *  - El bucket configurado se propaga a la request.
 */
class S3ServiceTest {

    private S3Client s3ClientMock;
    private S3Service service;

    @BeforeEach
    void setUp() {
        // `mock(...)` = crea un stub que registra invocaciones sin hacer nada real.
        s3ClientMock = mock(S3Client.class);
        service = new S3Service(s3ClientMock, "test-bucket");
    }

    @Test
    void upload_invokesPutObjectWithCorrectRequest() {
        // Arrange
        byte[] content = "hello".getBytes();

        // Act
        service.upload("mi-archivo.txt", content);

        // Assert — capturamos el argumento pasado a putObject para inspeccionarlo.
        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3ClientMock).putObject(captor.capture(), any(RequestBody.class));

        PutObjectRequest sent = captor.getValue();
        assertThat(sent.bucket()).isEqualTo("test-bucket");
        assertThat(sent.key()).isEqualTo("mi-archivo.txt");
    }

    @Test
    void getBucket_returnsConfiguredBucket() {
        assertThat(service.getBucket()).isEqualTo("test-bucket");
    }
}
