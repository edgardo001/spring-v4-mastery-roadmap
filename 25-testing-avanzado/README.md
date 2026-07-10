## 25 — Testing Avanzado (TestContainers y WireMock)

### Propósito
Llevar tus pruebas automatizadas al siguiente nivel. Aprender a hacer **Verdaderos Tests de Integración** usando `TestContainers` (para levantar bases de datos reales en Docker durante los tests) y `WireMock` (para simular el comportamiento de APIs de terceros que consumes).

### Problema que resuelve
En el Módulo 12 aprendimos a usar H2 (una base de datos en memoria) para los tests de integración. Esto tiene un problema grave llamado "Falso Positivo":
- **H2 no es PostgreSQL ni MySQL**: Si usas funciones nativas de Postgres (como JSONB, arreglos o búsquedas de texto), tus tests en H2 van a fallar (porque no tiene esa sintaxis) o peor, pasarán en H2 pero fallarán en producción.
- **APIs de Terceros Caídas**: Si tu aplicación llama a la API de Stripe, tus tests no pueden hacer llamadas HTTP reales a Stripe. Te cobrarían dinero, y si Stripe se cae, tus tests fallan (Flaky Tests).

### Cómo lo resuelve
- **TestContainers**: Es una librería Java que se conecta a tu Docker local. Al ejecutar `mvn test`, levanta un contenedor *real* de PostgreSQL, ejecuta tus tests contra él, y lo destruye al terminar. **Pruebas contra la misma base de datos que usarás en Producción.**
- **WireMock**: Levanta un servidor HTTP falso en un puerto aleatorio durante el test. Le dices: "Si alguien llama a `/stripe/charge`, responde 200 OK con este JSON". Tu código se conecta a WireMock pensando que es Stripe.

### Por qué aprenderlo
TestContainers se ha convertido en el estándar de facto de la industria. Empresas serias ya no usan H2 para tests de integración. Saber configurar estas dos herramientas demuestra madurez y es un diferenciador brutal para tu perfil como Senior.

```mermaid
graph TD
    A["mvn test"] --> B["TestContainers Inicia"]
    B --> C["🐳 Levanta Contenedor Postgres (Puerto Random)"]
    C --> D["@SpringBootTest arranca conectándose a Postgres"]
    
    A --> E["WireMock Inicia"]
    E --> F["🌐 Levanta Servidor Falso en localhost:8089"]
    
    D --> G["Tu Código Llama a 'Stripe'"]
    G --> F
    F -->> D: Devuelve JSON Simulado
    
    D --> H["Tu Código Guarda en BD"]
    H --> C
    
    D --> I["Tests Pasan ✅"]
    I --> J["TestContainers Destruye Postgres 💥"]
```

---

### Glosario Básico

#### `TestContainers`
Librería Java que expone una API para instanciar contenedores Docker desechables y ligeros para usar durante los tests de integración. 

#### `@Testcontainers` y `@Container`
Anotaciones de JUnit 5 que manejan el ciclo de vida del contenedor (lo inician antes de los tests y lo matan al finalizar).

#### `@DynamicPropertySource`
Anotación de Spring que permite sobreescribir las variables del `application.yml` en tiempo de ejecución. Sirve para inyectarle a Spring la URL y contraseña dinámicas que TestContainers generó al levantar la BD.

#### `WireMock`
Una herramienta para simular APIs HTTP (Mapeo de Stubs). Captura peticiones HTTP salientes y responde con datos preconfigurados.

---

### Conceptos

#### 1. Tests de Integración con TestContainers (PostgreSQL)
- **Qué es** — Abandonar H2. Levantar un Postgres real en un contenedor Docker, correr Flyway para crear las tablas, e insertar datos.
- **Por qué importa** — Garantiza que tus queries complejas y dialectos específicos funcionarán al 100% en producción.
- **Código** — Configuración completa:
  ```xml
  <!-- En pom.xml -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-testcontainers</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>postgresql</artifactId>
      <scope>test</scope>
  </dependency>
  ```
  ```java
  @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
  @Testcontainers // Activa la integración con Docker
  public class UserRepositoryIntegrationTest {
  
      // Crea y arranca el contenedor de Postgres (Versión 15)
      @Container
      static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
  
      // Inyecta las credenciales dinámicas a Spring Data JPA
      @DynamicPropertySource
      static void configureProperties(DynamicPropertyRegistry registry) {
          registry.add("spring.datasource.url", postgres::getJdbcUrl);
          registry.add("spring.datasource.username", postgres::getUsername);
          registry.add("spring.datasource.password", postgres::getPassword);
          // Flyway correrá automáticamente al conectar a esta BD
      }
  
      @Autowired
      private UserRepository userRepository;
  
      @Test
      void shouldSaveAndFindUserInRealPostgres() {
          // Act
          userRepository.save(new User("Edgardo", "test@test.com"));
          Optional<User> user = userRepository.findByEmail("test@test.com");
          
          // Assert
          assertTrue(user.isPresent());
          assertEquals("Edgardo", user.get().getName());
      }
  }
  ```
- **Analogía** — Usar H2 es como practicar boxeo contra una sombra. Usar TestContainers es meter a un sparring real al ring (pero que desaparece mágicamente cuando suena la campana).

#### 2. Mocking APIs Externas con WireMock
- **Qué es** — Tu código hace un `RestClient.post()` a una URL. Para el test, cambias esa URL para que apunte a `localhost:8089` (WireMock). WireMock escucha y responde como si fuera la API externa.
- **Código** — Simular a Github o Stripe:
  ```xml
  <!-- En pom.xml -->
  <dependency>
      <groupId>org.wiremock</groupId>
      <artifactId>wiremock-standalone</artifactId>
      <version>3.3.1</version>
      <scope>test</scope>
  </dependency>
  ```
  ```java
  @SpringBootTest
  public class PaymentIntegrationTest {
  
      private WireMockServer wireMockServer;
  
      @Autowired
      private PaymentService paymentService; // Este servicio usa RestClient
  
      @BeforeEach
      void setup() {
          // Iniciar servidor falso en puerto 8089
          wireMockServer = new WireMockServer(8089);
          wireMockServer.start();
          WireMock.configureFor("localhost", 8089);
          
          // Configurar el "Stub" (Regla de respuesta)
          stubFor(post(urlEqualTo("/v1/charges")) // Si me llega un POST a esta ruta
              .willReturn(aResponse()             // Responderé esto:
                  .withStatus(200)
                  .withHeader("Content-Type", "application/json")
                  .withBody("{\"id\": \"ch_123\", \"status\": \"succeeded\"}")));
      }
  
      @AfterEach
      void teardown() {
          wireMockServer.stop();
      }
  
      @Test
      void shouldProcessPaymentSuccessfully() {
          // ACT: El paymentService llamará a localhost:8089/v1/charges
          // WireMock atrapará la llamada y devolverá el JSON que configuramos arriba
          PaymentResult result = paymentService.chargeCard("tok_visa", 500);
          
          // ASSERT
          assertTrue(result.isSuccess());
          assertEquals("ch_123", result.getTransactionId());
          
          // VERIFY: WireMock permite comprobar si la llamada HTTP realmente ocurrió
          verify(postRequestedFor(urlEqualTo("/v1/charges"))
                  .withRequestBody(matchingJsonPath("$.amount", equalTo("500"))));
      }
  }
  ```
  *Nota: En el `application-test.yml`, debes sobreescribir la URL de Stripe para que apunte a `http://localhost:8089`.*

#### 3. TestContainers Connection Details (Novedad Spring Boot 3.1+)
- **Qué es** — Spring Boot 3.1 introdujo `@ServiceConnection`. ¡Ya no necesitas usar `@DynamicPropertySource`! Spring detecta el contenedor y autoconfigura la base de datos mágicamente.
- **Código** — Spring Boot 3.1+ (La forma moderna):
  ```java
  @SpringBootTest
  @Testcontainers
  public class ModernPostgresTest {
  
      // @ServiceConnection lee el tipo de contenedor e inyecta la URL/Credenciales a JPA
      @Container
      @ServiceConnection
      static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
  
      @Autowired
      private UserRepository repo;
      
      @Test
      void simpleTest() {
          repo.count(); // Ya está conectado!
      }
  }
  ```

#### 4. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| No se puede levantar TestContainers | No tienes Docker corriendo en tu máquina | Asegúrate de tener Docker Desktop o el demonio de Docker arrancado antes de ejecutar `mvn test`. |
| Lentitud extrema de los tests | Levantar/Matar el contenedor en cada clase de prueba | Hacer que el contenedor sea "Singleton" (estático) para que levante una vez por ejecución de Maven, o usar el patrón de contenedor compartido. |
| TestContainers choca con H2 | Tienes H2 en el classpath y JPA elige H2 en lugar del Postgres de Docker | Retira H2 de las dependencias o configura explícitamente el driver `org.postgresql.Driver` en las properties dinámicas. |
| WireMock responde 404 | El `RestClient` configurado llama a la URL real de producción (`https://api.stripe...`) | Tienes que asegurarte de inyectar `http://localhost:PUERTO` al RestClient durante el perfil de test (vía properties). |

---

### Ejercicios
1. Asegúrate de tener Docker corriendo en tu PC. Añade la dependencia de `spring-boot-testcontainers` y `postgresql`.
2. Crea un `@SpringBootTest` usando `@ServiceConnection` con un `PostgreSQLContainer`. Verifica que el test crea un usuario en la BD de Docker.
3. Instala la dependencia de WireMock. Crea un servicio que consulte una API de clima (usando `RestClient`).
4. En tu test de WireMock, crea el stub que devuelva un JSON falso con `{"temp": 25}` cuando se consulte `/weather`.
5. Valida que tu servicio retorne `25` durante el test sin conectarse realmente a la API externa de clima.

### Cómo ejecutar
```bash
cd 25-testing-avanzado

# Debes tener Docker encendido. Esto descargará la imagen de Postgres y correrá los tests.
mvn test
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Dependencias: `testcontainers`, `postgresql`, `wiremock`. |
| `src/test/.../UserIntegrationTest.java` | Test levantando Postgres con `@ServiceConnection`. |
| `src/test/.../WeatherApiTest.java` | Test usando WireMockServer para engañar al `RestClient`. |
| `application-test.yml` | Variables sobreescritas para el entorno de test (ej: URLs hacia Wiremock). |
