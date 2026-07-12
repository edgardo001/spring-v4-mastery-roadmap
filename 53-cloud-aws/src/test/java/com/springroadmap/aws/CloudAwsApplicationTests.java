package com.springroadmap.aws;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de arranque del contexto. Verifica que:
 *  1. Spring puede levantar la aplicación completa.
 *  2. Los tres beans AWS (S3, SQS, Secrets Manager) se construyen sin necesidad de
 *     tener LocalStack corriendo (los clientes son "lazy" — solo intentan conectar cuando se llama a un método).
 */
@SpringBootTest
class CloudAwsApplicationTests {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private SecretsManagerClient secretsManagerClient;

    @Test
    void contextLoads() {
        // Si el contexto arranca y los beans están inyectados, el test pasa.
        assertThat(s3Client).isNotNull();
        assertThat(sqsClient).isNotNull();
        assertThat(secretsManagerClient).isNotNull();
    }
}
