package com.springroadmap.batch.config;

import com.springroadmap.batch.domain.Customer;
import com.springroadmap.batch.domain.CustomerDto;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuración del Job y Step de Spring Batch (chunk-oriented).
 *
 * Analogía del mundo real - "planta de correo":
 *   - Reader = el que abre sobres de la bolsa (CSV).
 *   - Processor = el validador que descarta sobres rotos (email sin '@').
 *   - Writer = el que sella cajas de 10 sobres y las manda a la bodega (BD).
 *   - JobRepository = la libreta donde el jefe anota qué cajas ya se despacharon,
 *     para saber por dónde retomar si se corta la luz.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - Antes: JobBuilderFactory / StepBuilderFactory (deprecados en Spring Batch 5).
 *   - Ahora: 'new JobBuilder(name, jobRepository)' y 'new StepBuilder(name, jobRepository)'
 *     recibiendo el JobRepository por constructor.
 *
 * PREGUNTA DE ALUMNO — "¿qué es un 'chunk'?"
 *   Un lote. Si chunk=10, se leen 10 items, se procesan 10, y ENTONCES se
 *   escribe todo el lote en UNA sola transacción a la BD. Si algo falla,
 *   rollback del lote completo. Bajar de 1000 a 1 commit-por-fila es la
 *   diferencia entre 10 minutos y 10 horas de ejecución.
 */
@Configuration
public class BatchConfig {

    /**
     * Bean 'importCustomerJob' - el Job completo.
     * Un Job es una secuencia de Steps. Aquí tenemos uno solo.
     *
     * @param jobRepository libreta donde Spring Batch guarda el progreso.
     * @param step          el paso chunk-oriented definido abajo.
     */
    @Bean
    public Job importCustomerJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("importCustomerJob", jobRepository)
                .start(step)
                .build();
    }

    /**
     * Bean 'step' - Step chunk-oriented con Reader/Processor/Writer.
     * chunk(10, txManager) = 10 items por transacción.
     *
     * @param jobRepository libreta de progreso.
     * @param txManager     administrador transaccional (para el commit del chunk).
     * @param reader        lector del CSV.
     * @param processor     validador/transformador CustomerDto -> Customer.
     * @param writer        persistidor JPA.
     */
    @Bean
    public Step step(JobRepository jobRepository,
                     PlatformTransactionManager txManager,
                     FlatFileItemReader<CustomerDto> reader,
                     ItemProcessor<CustomerDto, Customer> processor,
                     JpaItemWriter<Customer> writer) {
        return new StepBuilder("importCustomerStep", jobRepository)
                // <IN, OUT> Reader emite IN, Writer recibe OUT.
                .<CustomerDto, Customer>chunk(10, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    /**
     * Bean Reader: FlatFileItemReader que lee 'classpath:customers.csv'.
     * - linesToSkip(1): salta la fila del encabezado.
     * - delimited: CSV separado por comas.
     * - names("name","email"): mapea columnas a propiedades del CustomerDto.
     * - fieldSetMapper: usa BeanWrapper para setear vía setters JavaBean.
     */
    @Bean
    public FlatFileItemReader<CustomerDto> reader() {
        BeanWrapperFieldSetMapper<CustomerDto> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(CustomerDto.class);

        return new FlatFileItemReaderBuilder<CustomerDto>()
                .name("customerCsvReader")
                .resource(new ClassPathResource("customers.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names("name", "email")
                .fieldSetMapper(mapper)
                .build();
    }

    /**
     * Bean Processor: valida (email debe contener '@') y transforma DTO -> Entity.
     * Retornar 'null' descarta el item (no llega al Writer).
     *
     * Usamos lambda (Java 8+). ANTES escribiríamos una clase anónima:
     *   new ItemProcessor<CustomerDto, Customer>() { public Customer process(...) {...} }
     */
    @Bean
    public ItemProcessor<CustomerDto, Customer> processor() {
        return dto -> {
            // Validación: email vacío o sin '@' se descarta.
            if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
                return null; // null = filter out
            }
            // Transformación: DTO plano -> Entity JPA.
            return new Customer(dto.getName(), dto.getEmail());
        };
    }

    /**
     * Bean Writer: JpaItemWriter que persiste el chunk usando el EntityManager.
     * En cada commit del chunk, Hibernate hace INSERTs en batch.
     */
    @Bean
    public JpaItemWriter<Customer> writer(EntityManagerFactory emf) {
        // Spring Batch 6.0: JpaItemWriter ahora exige EntityManagerFactory por constructor.
        return new JpaItemWriter<Customer>(emf);
    }
}
