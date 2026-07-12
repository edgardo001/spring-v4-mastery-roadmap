package com.springroadmap.batch;

import com.springroadmap.batch.controller.BatchController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test del BatchController usando MockMvc standalone.
 *
 * Nota crítica (MEMORY.md): en Spring Boot 4.1.0 fueron ELIMINADOS
 * @WebMvcTest y @AutoConfigureMockMvc. Patrón portable OBLIGATORIO:
 *   MockMvcBuilders.standaloneSetup(new Controller(mocks)).build();
 *
 * Aquí mockeamos JobLauncher para no ejecutar realmente el Job.
 */
class BatchControllerTest {

    private MockMvc mockMvc;
    private JobLauncher jobLauncher;
    private Job importCustomerJob;

    @BeforeEach
    void setUp() throws Exception {
        // Mocks manuales (sin @MockBean para respetar el patrón standalone).
        jobLauncher = mock(JobLauncher.class);
        importCustomerJob = mock(Job.class);

        // JobExecution simulado que devuelve id=42 y status=COMPLETED.
        JobExecution fakeExecution = mock(JobExecution.class);
        when(fakeExecution.getId()).thenReturn(42L);
        when(fakeExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

        // Cuando el controller llame a jobLauncher.run(anyJob, anyParams) -> devolvemos el fake.
        when(jobLauncher.run(eq(importCustomerJob), any(JobParameters.class)))
                .thenReturn(fakeExecution);

        BatchController controller = new BatchController(jobLauncher, importCustomerJob);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void postRun_devuelveJobExecutionIdYStatus() throws Exception {
        mockMvc.perform(post("/api/batch/run"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobExecutionId").value(42))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
