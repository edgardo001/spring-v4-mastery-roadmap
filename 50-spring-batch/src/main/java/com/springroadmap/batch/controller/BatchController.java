package com.springroadmap.batch.controller;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST que dispara el Job de importación manualmente.
 *
 * Analogía: es el "botón rojo" que aprieta el operador cuando quiere lanzar
 * la corrida de importación sin esperar al cron nocturno.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - Antes: @Controller + @ResponseBody por método.
 *   - Ahora: @RestController combina ambos en 1 sola anotación.
 *
 * PREGUNTA DE ALUMNO — "¿por qué necesita JobParameters únicos?"
 *   Spring Batch identifica una ejecución por (Job + JobParameters). Si repites
 *   los MISMOS parámetros, arrojaría JobInstanceAlreadyCompleteException.
 *   Por eso agregamos 'timestamp = System.currentTimeMillis()' que es único.
 */
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    // 'final' = no se reasigna. Constructor injection = testeable.
    private final JobLauncher jobLauncher;
    private final Job importCustomerJob;

    /**
     * Constructor injection: Spring provee las dependencias automáticamente.
     * Preferido sobre @Autowired en campos (más testeable, inmutabilidad).
     */
    public BatchController(JobLauncher jobLauncher, Job importCustomerJob) {
        this.jobLauncher = jobLauncher;
        this.importCustomerJob = importCustomerJob;
    }

    /**
     * POST /api/batch/run - dispara el Job importCustomerJob.
     * Devuelve {"jobExecutionId": N, "status": "COMPLETED"}.
     */
    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> run() throws Exception {
        // JobParameters con timestamp único: garantiza que cada corrida es una
        // nueva instancia (Spring Batch no las considera duplicadas).
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(importCustomerJob, params);

        return ResponseEntity.ok(Map.of(
                "jobExecutionId", execution.getId(),
                "status", execution.getStatus().toString()
        ));
    }
}
