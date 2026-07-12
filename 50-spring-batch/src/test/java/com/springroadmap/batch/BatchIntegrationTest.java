package com.springroadmap.batch;

import com.springroadmap.batch.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración del Job completo.
 *
 * @SpringBatchTest expone JobLauncherTestUtils (utilidad de test que corre el
 * Job de forma síncrona y devuelve el JobExecution para verificar).
 *
 * Verifica:
 *   1) El Job termina con BatchStatus.COMPLETED.
 *   2) Se persistieron los 5 customers del CSV.
 */
@SpringBootTest
@SpringBatchTest
class BatchIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CustomerRepository customerRepository;

    @AfterEach
    void cleanup() {
        // Limpieza entre tests para no arrastrar filas de una corrida a otra.
        customerRepository.deleteAll();
    }

    @Test
    void importCustomerJob_procesaLos5Clientes_yTerminaCompleted() throws Exception {
        // JobParameters únicos: evita "JobInstanceAlreadyComplete".
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        // Estado final del Job.
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // Se persistieron los 5 registros del customers.csv.
        assertThat(customerRepository.count()).isEqualTo(5L);
    }
}
