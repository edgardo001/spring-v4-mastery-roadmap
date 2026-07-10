# Spring v4 Mastery Roadmap

Ruta completa para aprender **Spring Boot 4 + Spring Framework 7** desde los fundamentos hasta arquitecturas empresariales (61 módulos). Cada concepto tiene su propia carpeta con teoría, ejemplos y ejercicios.

## Requisitos

- Java 21+ (LTS)
- Maven 3.9+ o Gradle 8+
- Conocimientos básicos de Java, SQL y HTML
- VS Code + Extension Pack for Java o IntelliJ IDEA Ultimate (recomendado)

## Estructura del Curso

```
spring/
├── 01-fundamentos-java/         # Java moderno (records, pattern matching, streams)
├── 02-intro-spring/             # Introducción a Spring Boot 4, Maven/Gradle
├── 03-dependency-injection/     # DI, IoC, @Component, @Autowired, @Bean
├── 04-spring-mvc-rest/          # @RestController, @RequestMapping, métodos HTTP
├── 05-configuracion/            # application.yml, profiles, @ConfigurationProperties
├── 06-base-datos-jdbc/          # JDBC, JdbcTemplate, DataSource, H2
├── 07-jpa-hibernate/            # JPA, Hibernate, Spring Data JPA, Entity, Repository
├── 08-migraciones-bd/           # Flyway, Liquibase, versionado de esquemas
├── 09-mapeo-dtos-mapstruct/     # MapStruct, DTOs, separación de capas
├── 10-validacion/               # Bean Validation, @Valid, errores
├── 11-excepciones/              # @ControllerAdvice, ExceptionHandler
├── 12-pruebas-unitarias/        # JUnit 5, Mockito, Spring Boot Test
├── 13-seguridad-basica/         # Spring Security, BCrypt, login básico
├── 14-jwt/                      # JWT, access/refresh tokens, Spring Security + JWT
├── 15-documentacion-api/        # OpenAPI 3.1, Swagger UI, springdoc
├── 16-subida-archivos/          # Multipart file upload, almacenamiento
├── 17-cache/                    # Spring Cache, Redis, @Cacheable, @CacheEvict
├── 18-jpa-avanzado/             # @Query, Specifications, Projections, @EntityGraph
├── 19-rest-client/              # RestClient, @HttpExchange, consumir APIs externas
├── 20-spring-aop/               # Aspectos, @Aspect, @Around, anotaciones custom
├── 21-async/                    # @Async, CompletableFuture, WebAsyncTask
├── 22-scheduling/               # @Scheduled, cron, Quartz
├── 23-websocket/                # WebSocket, STOMP, tiempo real
├── 24-rest-avanzado/            # HATEOAS, paginación, versionado, ETag
├── 25-testing-avanzado/         # TestContainers, @DataJpaTest, @WebMvcTest
├── 26-docker/                   # Dockerfile, docker-compose, multi-stage
├── 27-ci-cd/                    # GitHub Actions, Maven/Gradle, pipelines
├── 28-mail/                     # JavaMail, enviar emails, templates Thymeleaf
├── 29-spring-cloud-config/      # Config Server, Config Client, refresh
├── 30-resilience4j/             # Circuit Breaker, Retry, Rate Limiter, Bulkhead
├── 31-mensajeria/               # RabbitMQ / Kafka, @RabbitListener, @KafkaListener
├── 32-graphql/                  # Spring GraphQL, Query, Mutation, DataLoader
├── 33-security-avanzado/        # Method Security, @PreAuthorize, ACL, CSRF
├── 34-oauth2/                   # OAuth2, Keycloak, Resource Server, Client
├── 35-actuator-micrometer/      # Actuator, Micrometer, Prometheus, Grafana
├── 36-testcontainers/           # TestContainers avanzado, bases de datos reales
├── 37-internacionalizacion/     # i18n, MessageSource, locale resolver
├── 38-hexagonal/                # Puertos y Adaptadores, dominio puro
├── 39-monolito-modular/         # Módulos Maven/Gradle, shared kernel
├── 40-event-driven/             # Event Bus, Domain Events, SAGA, Event Sourcing
├── 41-microservicios/           # Spring Cloud, service discovery, gateway
├── 42-ddd/                      # Value Objects, Aggregates, Domain Events
├── 43-spring-react/             # Spring Boot 4 + React 19 + JWT + Docker
├── 44-spring-angular/           # Spring Boot 4 + Angular v22 + JWT + Docker
├── 45-observabilidad/           # OpenTelemetry, logs, tracing distribuido
├── 46-feature-flags/            # Feature flags, toggles, rollout gradual
├── 47-spring-cloud-gateway/     # API Gateway, routing, filters
├── 48-kubernetes/               # K8s, deployments, services, ingress, helm
├── 49-grpc/                     # gRPC, protobuf, Spring gRPC
├── 50-spring-batch/             # Procesamiento batch, jobs, steps, readers
├── 51-spring-integration/       # EIP, messaging, canales, transforms
├── 52-seguridad/                # OWASP, CSP, CORS, SQL Injection, XSS
├── 53-cloud-aws/                # AWS, RDS, S3, Secrets Manager, SQS
├── 54-spring-data-rest/         # Spring Data REST, HAL Explorer
├── 55-spring-shell/             # CLI interactiva con Spring Shell
├── 56-spring-native/            # GraalVM Native Image, AOT, buildpacks
├── 57-ai-integration/           # OpenAI/Claude, Spring AI, RAG, streaming
├── 58-spring-ai/                # Spring AI: vectores, embeddings, prompts
├── 59-reactive/                 # Spring WebFlux, R2DBC, Project Reactor
├── 60-spring-modulith/          # Spring Modulith, módulos, testing
└── 61-rsocket/                  # RSocket, comunicación reactiva bidireccional
```

## Nivel 1 — Fundamentos (Días 1–7)

| # | Carpeta | Tema |
|---|---------|------|
| 1 | `01-fundamentos-java` | Records, pattern matching, streams, Optional, text blocks, switch expressions |
| 2 | `02-intro-spring` | Spring Boot 4, Spring Initializr, Maven/Gradle, primer endpoint REST |
| 3 | `03-dependency-injection` | `@Component`, `@Service`, `@Repository`, `@Autowired`, `@Bean`, `@Configuration` |
| 4 | `04-spring-mvc-rest` | `@RestController`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@PathVariable` |
| 5 | `05-configuracion` | `application.yml`, `@ConfigurationProperties`, `@Value`, perfiles (`@Profile`) |
| 6 | `06-base-datos-jdbc` | `DataSource`, `JdbcTemplate`, H2, consultas CRUD, transacciones `@Transactional` |
| 7 | `07-jpa-hibernate` | `@Entity`, `@Id`, `@GeneratedValue`, `@Column`, `JpaRepository`, `@Query`, `@OneToMany` |

## Nivel 2 — Intermedio (Días 8–17)

| # | Carpeta | Tema |
|---|---------|------|
| 8 | `08-migraciones-bd` | Flyway, Liquibase, versionado de esquemas, `V1__init.sql`, buenas prácticas BD |
| 9 | `09-mapeo-dtos-mapstruct` | MapStruct, Entity to DTO, separación de capas, `@Mapper` |
| 10 | `10-validacion` | `@Valid`, `@NotBlank`, `@Email`, `@Size`, errores de validación, `@Validated` |
| 11 | `11-excepciones` | `@ControllerAdvice`, `@ExceptionHandler`, `ResponseEntity`, códigos HTTP |
| 12 | `12-pruebas-unitarias` | JUnit 5, Mockito, `@SpringBootTest`, `MockMvc`, `WebMvcTest` |
| 13 | `13-seguridad-basica` | Spring Security, `SecurityFilterChain`, `BCryptPasswordEncoder`, formulario login |
| 14 | `14-jwt` | JWT con `jjwt`, `UsernamePasswordAuthenticationToken`, `SecurityContextHolder` |
| 15 | `15-documentacion-api` | `springdoc-openapi`, OpenAPI 3.1, Swagger UI, `@Operation`, `@ApiResponse` |
| 16 | `16-subida-archivos` | `MultipartFile`, almacenamiento local/S3, `Resource`, `@ResponseBody` |
| 17 | `17-cache` | `@EnableCaching`, `@Cacheable`, `@CacheEvict`, `@CachePut`, Redis, Caffeine |

## Nivel 3 — Avanzado / Empresarial (Días 18–28)

| # | Carpeta | Tema |
|---|---------|------|
| 18 | `18-jpa-avanzado` | `@Query` nativas, `Specification`, `Projection`, `@EntityGraph`, `PagingAndSortingRepository` |
| 19 | `19-rest-client` | `RestClient`, `@HttpExchange`, WebClient, consumo de APIs externas declarativo |
| 20 | `20-spring-aop` | AOP, `@Aspect`, `@Before`, `@Around`, creación de anotaciones personalizadas |
| 21 | `21-async` | `@Async`, `CompletableFuture`, `WebAsyncTask`, `DeferredResult` |
| 22 | `22-scheduling` | `@Scheduled`, cron expressions, `@EnableScheduling`, Quartz scheduler |
| 23 | `23-websocket` | WebSocket, STOMP, `@MessageMapping`, `SimpMessagingTemplate`, chat |
| 24 | `24-rest-avanzado` | HATEOAS, `PagedModel`, versionado API, `ETag`, `@RestControllerAdvice` |
| 25 | `25-testing-avanzado` | TestContainers, `@DataJpaTest`, `@WebMvcTest`, `@RestClientTest`, integration tests |
| 26 | `26-docker` | `Dockerfile` multi-stage, `docker-compose`, `.dockerignore`, imágenes optimizadas |
| 27 | `27-ci-cd` | GitHub Actions, Maven `verify`/`deploy`, SonarQube, calidad de código |
| 28 | `28-mail` | `JavaMailSender`, `MimeMessage`, templates Thymeleaf para emails, adjuntos |

## Nivel 4 — Especialización (Días 29–37)

| # | Carpeta | Tema |
|---|---------|------|
| 29 | `29-spring-cloud-config` | `@EnableConfigServer`, Config Client, `@RefreshScope`, bus con RabbitMQ |
| 30 | `30-resilience4j` | Circuit Breaker, `@Retry`, `@RateLimiter`, `@TimeLimiter`, `@Bulkhead` |
| 31 | `31-mensajeria` | RabbitMQ (`@RabbitListener`), Kafka (`@KafkaListener`), `KafkaTemplate` |
| 32 | `32-graphql` | `spring-graphql`, `@QueryMapping`, `@MutationMapping`, `DataLoader`, `@BatchMapping` |
| 33 | `33-security-avanzado` | `@PreAuthorize`, `@PostAuthorize`, `@Secured`, ACL, `MethodSecurityExpressionHandler` |
| 34 | `34-oauth2` | OAuth2 con Keycloak, `@RegisteredOAuth2AuthorizedClient`, Resource Server, JWT |
| 35 | `35-actuator-micrometer` | `spring-boot-starter-actuator`, Micrometer, Prometheus, Grafana dashboards |
| 36 | `36-testcontainers` | TestContainers: PostgreSQL, MySQL, Kafka, Redis, `@ServiceConnection`, `@DynamicPropertySource` |
| 37 | `37-internacionalizacion` | `MessageSource`, `LocaleResolver`, `AcceptHeaderLocaleResolver`, i18n en REST |

## Nivel 5 — Arquitecturas Software (Días 38–42)

| # | Carpeta | Tema |
|---|---------|------|
| 38 | `38-hexagonal` | Puertos y Adaptadores, dominio puro, `@PrimaryPort`, `@SecondaryPort`, infraestructura intercambiable |
| 39 | `39-monolito-modular` | Módulos Maven multi-módulo, shared kernel, comunicación entre bounded contexts |
| 40 | `40-event-driven` | `ApplicationEventPublisher`, Domain Events, SAGA coreográfica, Event Sourcing |
| 41 | `41-microservicios` | Spring Cloud: Eureka (Discovery), Gateway, Config, Resilience4j, Docker Compose |
| 42 | `42-ddd` | Value Objects, Aggregates, Repository pattern, Domain Events, Lenguaje Ubicuo |

## Nivel 6 — Integración Frontend (Días 43–44)

| # | Carpeta | Tema |
|---|---------|------|
| 43 | `43-spring-react` | Spring Boot 4 + React 19 + JWT (access/refresh) + Docker Compose |
| 44 | `44-spring-angular` | Spring Boot 4 + Angular v22 + JWT (access/refresh) + Docker Compose |

## Nivel 7 — DevOps & Enterprise (Días 45–56)

| # | Carpeta | Tema |
|---|---------|------|
| 45 | `45-observabilidad` | OpenTelemetry, logs estructurados (JSON), tracing distribuido (Jaeger/Zipkin) |
| 46 | `46-feature-flags` | Feature toggles, `@ConditionalOnProperty`, LaunchDarkly, rollout gradual |
| 47 | `47-spring-cloud-gateway` | API Gateway, `RouteLocator`, filters, rate limiting, retry |
| 48 | `48-kubernetes` | K8s: Deployments, Services, Ingress, ConfigMap, Secrets, Helm charts |
| 49 | `49-grpc` | gRPC, protobuf, `@GrpcService`, `@GrpcClient`, comunicación inter-servicio |
| 50 | `50-spring-batch` | `@EnableBatchProcessing`, `Job`, `Step`, `ItemReader`, `ItemProcessor`, `ItemWriter` |
| 51 | `51-spring-integration` | Enterprise Integration Patterns, `@MessagingGateway`, canales, `@Transformer`, `@Splitter` |
| 52 | `52-seguridad` | OWASP Top 10, CSP headers, CORS config, SQL Injection prevention, XSS sanitization |
| 53 | `53-cloud-aws` | AWS: RDS, S3, Secrets Manager, SQS, SNS, Elastic Beanstalk ECS |
| 54 | `54-spring-data-rest` | Spring Data REST, `@RepositoryRestResource`, HAL Explorer, `Projection` |
| 55 | `55-spring-shell` | CLI interactiva, `@ShellComponent`, `@ShellMethod`, `@ShellOption`, colores |
| 56 | `56-spring-native` | GraalVM Native Image, AOT compilation, `spring-boot-starter-parent`, buildpacks |

## Nivel 8 — Especialización Moderna (Días 57–61)

| # | Carpeta | Tema |
|---|---------|------|
| 57 | `57-ai-integration` | LLMs, OpenAI/Claude, Spring AI, RAG, streaming SSE, embeddings, vectores |
| 58 | `58-spring-ai` | Spring AI: `ChatClient`, `EmbeddingClient`, `VectorStore`, prompts, tools, outputs |
| 59 | `59-reactive` | Spring WebFlux, `Mono`/`Flux`, R2DBC, MongoDB reactive, Project Reactor operators |
| 60 | `60-spring-modulith` | Spring Modulith: `@ApplicationModule`, testing modular, event publication |
| 61 | `61-rsocket` | RSocket, `@MessageMapping`, `@ConnectMapping`, reactive bidireccional, fire-and-forget |

## Tecnologías y Librerías

| Herramienta | Uso |
|-------------|-----|
| **Spring Boot 4** | Framework principal (auto-configuración, starters, producción) |
| **Spring Framework 7** | Core: IoC, DI, MVC, AOP, eventos |
| **Spring Data JPA** | ORM y acceso a datos relacionales |
| **Spring Security** | Autenticación, autorización, JWT, OAuth2 |
| **Spring Cloud** | Microservicios: Config, Gateway, Discovery, Circuit Breaker |
| **Spring GraphQL** | API GraphQL con Spring |
| **Spring Batch** | Procesamiento por lotes |
| **Spring Integration** | Enterprise Integration Patterns |
| **Spring Modulith** | Módulos y arquitectura modular |
| **Spring AI** | Integración con LLMs e IA generativa |
| **Spring Shell** | CLI interactivas |
| **Spring Data REST** | Exposición REST automática desde repositorios |
| **Hibernate / JPA** | Mapeo objeto-relacional |
| **Flyway / Liquibase** | Migraciones de base de datos |
| **Maven / Gradle** | Build tools y gestión de dependencias |
| **JUnit 5 + Mockito** | Testing unitario |
| **TestContainers** | Testing con contenedores Docker |
| **REST Assured** | Testing de APIs REST |
| **Docker** | Contenerización |
| **Kubernetes** | Orquestación de contenedores |
| **Resilience4j** | Resiliencia: Circuit Breaker, Retry, Rate Limiter |
| **RabbitMQ** | Mensajería AMQP |
| **Apache Kafka** | Streaming de eventos |
| **Redis** | Caché y almacenamiento en memoria |
| **Micrometer / Prometheus** | Métricas y monitoreo |
| **OpenTelemetry** | Trazas distribuidas |
| **Jaeger / Zipkin** | Tracing distribuido |
| **OpenAPI 3.1 (springdoc)** | Documentación de APIs |
| **GraphQL** | Lenguaje de consulta para APIs |
| **gRPC** | RPC de alto rendimiento con protobuf |
| **RSocket** | Comunicación reactiva bidireccional |
| **GraalVM Native Image** | Compilación AOT, native ejecutables |
| **Thymeleaf** | Templates HTML para servidor |
| **React (Nivel 6)** | Frontend React 19 + Spring Boot |
| **Angular (Nivel 6)** | Frontend Angular v22 + Spring Boot |
| **Project Reactor** | Programación reactiva (Mono/Flux) |
| **Lombok** | Reducción de boilerplate |
| **MapStruct** | Mapeo entre DTOs y entidades |
| **GitHub Actions** | CI/CD pipelines |

## Cómo usar este repositorio

```bash
# 1. Clona o crea la carpeta raíz
cd spring

# 2. Cada carpeta contiene su propio proyecto Spring Boot
cd 03-dependency-injection
mvn spring-boot:run   # Maven
# o
gradle bootRun        # Gradle

# 3. Para proyectos con Docker:
cd 41-microservicios
docker compose up

# 4. Para proyectos frontend-backend (Niveles 6):
cd 43-spring-react
docker compose up      # Lanza backend + frontend

# 5. Sigue el orden numérico recomendado
```

## Metodología

1. **Lee en profundidad** el README de la carpeta (explicación teórica extensa, casos de uso empresarial y resolución de 'edge cases' con diagramas).
2. **Analiza** el código del proyecto (totalmente comentado y basado en escenarios del mundo real).
3. **Modifica** y experimenta.
4. **Completa** los ejercicios propuestos para dominar el concepto.

> **Regla de Oro del Roadmap**: Todos los temas se tratan con profundidad técnica. No encontrarás resúmenes superficiales, sino implementaciones precisas y orientadas a producción.

---

> **Spring v4 Mastery Roadmap — Aprende haciendo, construye como profesional.**
