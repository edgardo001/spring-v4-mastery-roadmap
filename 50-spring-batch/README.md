## 50 — Spring Batch

### Propósito
Aprender a construir procesos de **procesamiento por lotes (Batch Processing)** capaces de manejar grandes volúmenes de datos (millones de registros) de forma robusta, transaccional, reanudable y sin colapsar la memoria de la JVM.

### Problema que resuelve
Tu banco te pide importar cada noche un archivo CSV de **10 millones de filas** hacia la base de datos.
- Si lo haces con un `for` normal y `entityManager.persist()`, la JVM revienta con `OutOfMemoryError` a la fila 500.000.
- Si el proceso se cae en la fila 8.500.000, ¿reinicias desde cero y pierdes 6 horas de trabajo?
- Algunas filas vienen corruptas (fechas mal formateadas). ¿Detienes todo el job o las saltas y sigues?
- El proceso debe correr todas las noches a las 02:00 AM automáticamente.

### Cómo lo resuelve
**Spring Batch** define una arquitectura formal para lotes:
1. Un **Job** (Trabajo) se compone de uno o más **Steps** (Pasos).
2. Cada Step usa un patrón **Chunk-Oriented Processing**: un `ItemReader` lee de a un ítem, un `ItemProcessor` lo transforma, y un `ItemWriter` los agrupa en lotes (chunks) de N ítems y los escribe en una sola transacción.
3. Un `JobRepository` persiste en BD el estado del Job (qué chunk se completó, cuál falló). Si el proceso se cae, al reiniciar **retoma exactamente desde el último chunk exitoso**.
4. Configura políticas de `skip` (saltar filas malas), `retry` (reintentar transitorios) y `restart` (reanudar Jobs fallidos).

### Por qué aprenderlo
Spring Batch es el estándar de facto en **bancos, retail, telcos y seguros** para procesos ETL nocturnos, conciliación de cuentas, generación masiva de reportes, facturación mensual y cargas iniciales de datos. Cualquier proyecto empresarial serio con "carga masiva" tiene Spring Batch detrás.

```mermaid
graph TD
    A["@Scheduled / CLI / REST"] -->|launch(jobParameters)| B["JobLauncher"]
    B --> C["Job: importCsvJob"]

    C --> D["Step 1: readCsv"]
    C --> E["Step 2: generateReport"]

    subgraph "Chunk-Oriented Step"
        D --> F["ItemReader (FlatFileItemReader)"]
        F -->|item x1| G["ItemProcessor (transform/validate)"]
        G -->|item x1| H["Chunk buffer (size=1000)"]
        H -->|chunk lleno| I["ItemWriter (JpaItemWriter)"]
        I -->|commit TX| J[(BD Destino)]
    end

    B -.->|guarda estado| K[(JobRepository)]
    C -.->|checkpoint por chunk| K

    style C fill:#f03e3e,color:#fff
    style K fill:#2b8a3e,color:#fff
```

---

### Glosario Básico

#### `Job`
La unidad de trabajo completa. Ej: "Importar CSV nocturno". Contiene uno o varios Steps ordenados.

#### `Step`
Una fase del Job. Ej: "Paso 1: Leer CSV", "Paso 2: Generar reporte de errores". Cada Step tiene su propio Reader/Processor/Writer.

#### `ItemReader<T>`
Lee un ítem a la vez desde una fuente (CSV, BD, JMS). **Streaming**, nunca carga todo en memoria.

#### `ItemProcessor<I, O>`
Transforma o valida el ítem. Puede devolver `null` para descartarlo (filtrado).

#### `ItemWriter<T>`
Escribe un lote de ítems (chunk) en una sola transacción hacia el destino.

#### `Chunk`
Grupo de N ítems que se commitean juntos. Si el chunk es 1000, se hace 1 commit cada 1000 filas (10.000x más rápido que 1 commit por fila).

#### `JobRepository`
Tabla en BD (`BATCH_JOB_INSTANCE`, `BATCH_STEP_EXECUTION`) donde Spring Batch guarda el estado. Permite reanudar Jobs.

#### `JobParameters`
Parámetros que identifican una ejecución única. Ej: `date=2026-07-10`. Dos ejecuciones con los mismos parámetros son consideradas la MISMA (no se pueden repetir).

---

### Conceptos

#### 1. Configuración de Job y Step (Spring Batch 5+)
- **Qué es** — Definir el Job y sus Steps usando los builders modernos `JobBuilder` y `StepBuilder` (sin las clases `*Factory` deprecadas).
- **Código**:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-batch</artifactId>
  </dependency>
  ```
  ```java
  @Configuration
  @EnableBatchProcessing
  @RequiredArgsConstructor
  @Slf4j
  public class ImportCsvJobConfig {

      private final JobRepository jobRepository;
      private final PlatformTransactionManager txManager;

      @Bean
      public Job importCsvJob(Step readCsvStep) {
          return new JobBuilder("importCsvJob", jobRepository)
                  .start(readCsvStep)
                  .build();
      }

      @Bean
      public Step readCsvStep(ItemReader<CustomerCsv> reader,
                              ItemProcessor<CustomerCsv, Customer> processor,
                              ItemWriter<Customer> writer) {
          return new StepBuilder("readCsvStep", jobRepository)
                  .<CustomerCsv, Customer>chunk(1000, txManager)  // 1 commit cada 1000
                  .reader(reader)
                  .processor(processor)
                  .writer(writer)
                  .build();
      }
  }
  ```

#### 2. Chunk-Oriented: CSV → JPA
- **Qué es** — El patrón clásico: `FlatFileItemReader` lee el CSV línea por línea, un Processor valida, y `JpaItemWriter` persiste en lotes.
- **Código**:
  ```java
  @Bean
  public FlatFileItemReader<CustomerCsv> csvReader() {
      return new FlatFileItemReaderBuilder<CustomerCsv>()
              .name("csvReader")
              .resource(new FileSystemResource("input/customers.csv"))
              .linesToSkip(1) // Skip header
              .delimited().delimiter(",")
              .names("id", "name", "email", "birthDate")
              .targetType(CustomerCsv.class)
              .build();
  }

  @Bean
  public ItemProcessor<CustomerCsv, Customer> processor() {
      return item -> {
          if (item.getEmail() == null || !item.getEmail().contains("@")) {
              log.warn("Skipping invalid email: {}", item.getId());
              return null; // null = descartar item
          }
          return Customer.builder()
                  .id(item.getId())
                  .name(item.getName().toUpperCase())
                  .email(item.getEmail())
                  .build();
      };
  }

  @Bean
  public JpaItemWriter<Customer> jpaWriter(EntityManagerFactory emf) {
      JpaItemWriter<Customer> writer = new JpaItemWriter<>();
      writer.setEntityManagerFactory(emf);
      return writer;
  }
  ```

#### 3. Manejo de Fallos: Skip, Retry y Restart
- **Qué es** — Configurar tolerancia a fallos: saltar filas malformadas, reintentar errores transitorios (deadlocks) y reanudar Jobs caídos.
- **Código**:
  ```java
  @Bean
  public Step resilientStep(ItemReader<CustomerCsv> reader,
                            ItemProcessor<CustomerCsv, Customer> processor,
                            ItemWriter<Customer> writer) {
      return new StepBuilder("resilientStep", jobRepository)
              .<CustomerCsv, Customer>chunk(1000, txManager)
              .reader(reader).processor(processor).writer(writer)
              .faultTolerant()
              .skip(FlatFileParseException.class).skipLimit(100) // 100 filas malas máx.
              .retry(DeadlockLoserDataAccessException.class).retryLimit(3)
              .build();
      // Restart: si el Job se cae en el chunk 8500, al relanzarlo con los MISMOS
      // JobParameters, Spring Batch consulta BATCH_STEP_EXECUTION y arranca en 8501.
  }
  ```

#### 4. Paralelización: Partitioning y Multi-threaded Step
- **Qué es** — Dividir el trabajo en particiones (ej: por rango de IDs) y procesar cada partición en un hilo distinto para acelerar Jobs largos.
- **Código**:
  ```java
  @Bean
  public Step partitionedStep(Step slaveStep, Partitioner partitioner) {
      return new StepBuilder("partitionedStep", jobRepository)
              .partitioner("slaveStep", partitioner)
              .step(slaveStep)
              .gridSize(4) // 4 particiones = 4 hilos
              .taskExecutor(new SimpleAsyncTaskExecutor("batch-"))
              .build();
  }

  @Bean
  public Partitioner rangePartitioner() {
      return gridSize -> {
          Map<String, ExecutionContext> partitions = new HashMap<>();
          int total = 10_000_000, chunk = total / gridSize;
          for (int i = 0; i < gridSize; i++) {
              ExecutionContext ctx = new ExecutionContext();
              ctx.putLong("minId", i * chunk);
              ctx.putLong("maxId", (i + 1) * chunk - 1);
              partitions.put("part" + i, ctx);
          }
          return partitions;
      };
  }
  ```

#### 5. Programación Nocturna con `@Scheduled` + JobLauncher
- **Qué es** — Disparar el Job cada noche a las 02:00 AM usando el scheduler de Spring.
- **Código**:
  ```java
  @Component
  @RequiredArgsConstructor
  @Slf4j
  public class NightlyBatchScheduler {

      private final JobLauncher jobLauncher;
      private final Job importCsvJob;

      @Scheduled(cron = "0 0 2 * * *") // 02:00 AM todos los días
      public void runNightly() throws Exception {
          JobParameters params = new JobParametersBuilder()
                  .addLocalDateTime("runDate", LocalDateTime.now()) // Único por ejecución
                  .toJobParameters();
          JobExecution exec = jobLauncher.run(importCsvJob, params);
          log.info("Job finished with status: {}", exec.getStatus());
      }
  }
  ```

---

### Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `JobInstanceAlreadyCompleteException` | Lanzaste el Job dos veces con `JobParameters` idénticos. Spring Batch cree que ya lo hiciste. | Agrega siempre un parámetro único (timestamp, UUID) al `JobParametersBuilder`. Nunca uses parámetros estáticos. |
| `OutOfMemoryError` en el Reader | Usaste `JdbcCursorItemReader` sin `fetchSize` o hiciste `SELECT *` sin paginar. El driver JDBC carga los 10M en RAM. | Usa `JdbcPagingItemReader` (paginado real) o configura `fetchSize=1000` en el cursor. El Reader **debe** ser streaming. |
| Chunk demasiado grande | `chunk(50000, ...)` genera transacciones de 5 minutos, bloqueos de BD y consumo brutal de heap. | Sweet spot: **500–2000**. Mide throughput vs uso de heap. Nunca más de 10.000. |
| Transacción parcial visible | El Writer falla en el ítem 750 del chunk 1000. Los 999 anteriores... ¿se ven? | No: el chunk es **transaccional**. Todo el chunk hace rollback. Pero si el Writer no está bajo `PlatformTransactionManager` (ej: escribes a un CSV file), no hay rollback: usa un `CompositeItemWriter` con compensación manual. |

---

### Ejercicios
1. Crea un proyecto Spring Boot 4.1.0 con `spring-boot-starter-batch` + `spring-boot-starter-data-jpa` + H2.
2. Coloca un CSV `input/customers.csv` con 100.000 filas (usa un generador). Define la entidad `Customer` y su repositorio JPA.
3. Configura el `Job importCsvJob` con un Step `chunk(1000)` que use `FlatFileItemReader` → `Processor` (valida email) → `JpaItemWriter`.
4. Agrega tolerancia: `.skip(FlatFileParseException.class).skipLimit(50)`. Introduce 10 filas corruptas y verifica que el Job termina exitosamente.
5. Detén la app en medio del Job (Ctrl+C). Relánzala con los mismos `JobParameters` y confirma en la tabla `BATCH_STEP_EXECUTION` que retomó desde el último chunk exitoso.

### Cómo ejecutar
```bash
cd 50-spring-batch
mvn spring-boot:run

# Ver el estado del Job en H2 Console
# http://localhost:8080/h2-console
# SELECT * FROM BATCH_JOB_EXECUTION;
# SELECT * FROM BATCH_STEP_EXECUTION;
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `config/ImportCsvJobConfig.java` | Definición del `Job` y `Step` con `JobBuilder`/`StepBuilder`. |
| `config/BatchReadersWriters.java` | Beans `FlatFileItemReader`, `ItemProcessor`, `JpaItemWriter`. |
| `config/ResilientStepConfig.java` | Step con `faultTolerant()`, skip y retry. |
| `config/PartitionedStepConfig.java` | Partitioning multi-hilo con `Partitioner` y `TaskExecutor`. |
| `scheduler/NightlyBatchScheduler.java` | `@Scheduled` cron que lanza el Job vía `JobLauncher`. |
| `domain/Customer.java` | Entidad JPA destino. |
| `domain/CustomerCsv.java` | POJO que mapea la fila del CSV. |
| `application.yml` | Config de datasource, `spring.batch.jdbc.initialize-schema=always`. |
