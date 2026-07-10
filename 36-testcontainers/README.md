## 36 — TestContainers Avanzado (Redis, Kafka y LocalStack)

### Propósito
Llevar tus pruebas de integración al máximo nivel simulando el ecosistema completo de Producción localmente. Aprenderemos a levantar servicios externos complejos (como un Message Broker de Kafka, un Caché de Redis o servicios nativos de AWS) usando Testcontainers.

### Problema que resuelve
En el Módulo 25 usamos Testcontainers para levantar una base de datos PostgreSQL y probar consultas SQL. Pero una aplicación empresarial moderna raramente depende de una sola base de datos.
- ¿Cómo pruebas tu código `@KafkaListener` si no tienes un servidor Kafka?
- ¿Cómo pruebas tu lógica de Caché sin instalar Redis en tu PC local?
- ¿Cómo pruebas el código que sube imágenes a Amazon S3 (`amazon-s3-sdk`) sin crear un bucket real que te cueste dinero en AWS?
Los "Mocks" (usar Mockito) son insuficientes para esto. Mockear Kafka no te dirá si la serialización de tu mensaje JSON funciona de verdad en la red.

### Cómo lo resuelve
Con módulos especializados de **Testcontainers**. Te permiten iniciar contenedores de Kafka, Redis y LocalStack (un simulador de AWS) antes de que corran tus tests. La aplicación se conectará a ellos exactamente como lo haría en Producción, validando la serialización, reconexiones y flujos asíncronos reales.

### Por qué aprenderlo
Aprender a testear flujos distribuidos y asíncronos separa a un desarrollador intermedio de un Arquitecto. Una vez domines Testcontainers para el stack completo, nunca más dirás "Funcionaba en mi máquina y falló en Producción".

```mermaid
graph TD
    A["mvn verify"] --> B["Testcontainers (Docker)"]
    
    B --> C["Levanta PostgreSQL 🐳"]
    B --> D["Levanta Apache Kafka 🐳"]
    B --> E["Levanta Redis 🐳"]
    
    A --> F["Spring Boot @SpringBootTest"]
    
    F -->|Conecta (JPA)| C
    F -->|Produce/Consume mensajes| D
    F -->|Guarda/Lee Caché| E
    
    F -->|"Tests Finalizan"| G["Testcontainers apaga TODO 💥"]

    style B fill:#339af0,color:#fff
    style G fill:#ff6b6b,color:#fff
```

---

### Glosario Básico

#### `GenericContainer`
La clase base de Testcontainers. Si no existe una librería oficial para el servicio que quieres (ej. no hay librería "Testcontainers Redis"), puedes usar esta clase para correr CUALQUIER imagen Docker (ej. `redis:7-alpine`).

#### `LocalStack`
Una herramienta (y contenedor) fabulosa que simula los servicios en la nube de Amazon Web Services (S3, SQS, SNS, DynamoDB) en tu propia computadora, sin costo.

#### `Awaitility`
Librería Java indispensable para tests de integración asíncronos. Permite al test "esperar hasta 5 segundos a que aparezca un mensaje en la BD", ideal para probar Kafka.

---

### Conceptos

#### 1. Testcontainers con Redis (Uso de GenericContainer)
- **Qué es** — Redis no tiene un módulo "especial" en la librería de Testcontainers Java. Debemos usar la clase cruda `GenericContainer` y exponer su puerto.
- **Código** — Integrando Redis a tus pruebas:
  ```java
  @SpringBootTest
  @Testcontainers
  public class CacheIntegrationTest {
  
      // Usamos GenericContainer y le decimos qué puerto interno queremos exponer (6379)
      @Container
      static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
              .withExposedPorts(6379);
  
      @DynamicPropertySource
      static void redisProperties(DynamicPropertyRegistry registry) {
          // El host suele ser "localhost", pero el puerto será uno aleatorio (ej. 32591)
          registry.add("spring.data.redis.host", redis::getHost);
          registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
      }
  
      @Autowired
      private ProductService productService; // Servicio con @Cacheable
  
      @Test
      void shouldCacheProductInRedis() {
          // Llama al servicio (hace consulta pesada y lo guarda en Redis del contenedor)
          Product p1 = productService.getProduct(1L);
          
          // La segunda llamada no debe tocar la Base de Datos ficticia, sino ir al Redis de Docker
          Product p2 = productService.getProduct(1L);
          assertEquals(p1, p2);
      }
  }
  ```

#### 2. Testcontainers con Apache Kafka
- **Qué es** — Levantar Kafka localmente suele requerir ZooKeeper y mucha RAM. Con la clase `KafkaContainer` de Testcontainers, se hace en 2 líneas.
- **Código** — Integración Asíncrona:
  ```xml
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>kafka</artifactId>
      <scope>test</scope>
  </dependency>
  <!-- Awaitility es CLAVE para testing asíncrono -->
  <dependency>
      <groupId>org.awaitility</groupId>
      <artifactId>awaitility</artifactId>
      <scope>test</scope>
  </dependency>
  ```
  ```java
  @SpringBootTest
  @Testcontainers
  public class MessagingIntegrationTest {
  
      @Container
      @ServiceConnection // Autoconfigura mágicamente la URI de Kafka para Spring Boot
      static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
  
      @Autowired
      private OrderProducer producer;
  
      @Autowired
      private OrderRepository repository; // DB para verificar que el Consumer hizo su trabajo
  
      @Test
      void shouldProduceAndConsumeMessage() {
          // ACT: Enviamos un mensaje al contenedor Kafka
          producer.sendOrder("ORD-999");
          
          // ASSERT: El Consumer corre en otro hilo y demora milisegundos.
          // Si hiciéramos assertEquals() aquí, fallaría porque la DB aún no tiene el dato.
          // Usamos Awaitility para evaluar continuamente durante un máximo de 5 segundos.
          Awaitility.await()
              .atMost(Duration.ofSeconds(5))
              .untilAsserted(() -> {
                  Optional<Order> order = repository.findByCode("ORD-999");
                  assertTrue(order.isPresent());
                  assertEquals("PROCESADO", order.get().getStatus());
              });
      }
  }
  ```

#### 3. Simulación de AWS con LocalStack
- **Qué es** — Supongamos que tu app sube avatares a Amazon S3. Con `LocalStackContainer`, levantas un S3 falso localmente y pruebas tu código del AWS SDK Java v2 real contra él.
- **Código**:
  ```xml
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>localstack</artifactId>
      <scope>test</scope>
  </dependency>
  ```
  ```java
  @SpringBootTest
  @Testcontainers
  public class S3IntegrationTest {
  
      @Container
      static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1"))
              .withServices(LocalStackContainer.Service.S3); // Solo levantamos el servicio S3
  
      @DynamicPropertySource
      static void awsProperties(DynamicPropertyRegistry registry) {
          // Redirigimos el Endpoint de AWS oficial hacia el LocalStack de Docker
          registry.add("aws.s3.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
          registry.add("aws.credentials.access-key", localStack::getAccessKey);
          registry.add("aws.credentials.secret-key", localStack::getSecretKey);
      }
      
      // Aquí escribirías tu test que inyecta S3Client y sube un archivo
  }
  ```

#### 4. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Los tests de Kafka fallan por "Timing" (A veces pasan, a veces no - Flaky Tests) | Usaste `Thread.sleep(2000)` para darle tiempo al consumidor. Si el PC está lento, toma 3 segundos y el test falla. | NUNCA usar `Thread.sleep()` en tests. Usar siempre `Awaitility.await().untilAsserted()`. |
| Lentitud masiva | Iniciar Postgres, Redis y Kafka en cada clase de prueba (`@SpringBootTest`). | Usa el patrón **Singleton Container**. Extiende todas tus clases de Test de una clase base abstracta (`BaseIntegrationTest`) donde declaras los contenedores estáticos (sin `@Container`). Así solo arrancarán 1 vez para todo el conjunto de tests de Maven. |
| `@ServiceConnection` no funciona con Redis | GenericContainer no provee suficiente meta-información a Spring. | Para Redis usando GenericContainer, mantén el uso clásico de `@DynamicPropertySource`. |

---

### Ejercicios
1. Crea un proyecto con dependencias de Spring Data Redis y Kafka. Añade Testcontainers a la carpeta test.
2. Escribe una clase base abstracta `AbstractIntegrationTest` que levante un contenedor Redis y un contenedor Kafka (Patrón Singleton).
3. Haz que tus clases de test hereden de esta clase.
4. Escribe un test para probar la escritura y lectura en caché (usando `RedisTemplate` inyectado por Spring, el cual estará conectado al contenedor).
5. Implementa Awaitility (agrégalo al pom) y haz un test enviando un mensaje a Kafka, y esperando 3 segundos a que una variable en memoria cambie de estado.

### Cómo ejecutar
```bash
cd 36-testcontainers

# Asegúrate de tener Docker Engine encendido
mvn clean test
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | `testcontainers-kafka`, `testcontainers-localstack`, `awaitility`. |
| `src/test/../KafkaIntegrationTest.java` | Test asíncrono comprobando el consumo de Kafka. |
| `src/test/../RedisIntegrationTest.java` | Test de `GenericContainer` para probar el sistema de Caché. |
