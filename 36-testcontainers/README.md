## 36 — Testcontainers (Postgres + Redis reales en tus tests)

### Propósito
Aprender a levantar **múltiples contenedores Docker reales** (Postgres + Redis) durante los tests de integración con Testcontainers, en lugar de mocks o bases de datos "fake" en memoria.

### Problema que resuelve
Cuando pruebas contra H2 en lugar de Postgres, se te escapan:
- Diferencias sutiles de SQL (`ILIKE`, `JSONB`, `RETURNING`).
- Errores de casteo de tipos (`BIGSERIAL` vs `IDENTITY`).
- Comportamientos de índices y collations.
- Comandos de Redis mockeados que no fallan cuando la sintaxis real fallaria.

Resultado clásico: **"en mi máquina funciona"**, pero en producción se rompe.

### Cómo lo resuelve
Testcontainers arranca contenedores Docker **reales** justo antes del test y los apaga cuando termina. Combinado con `@DynamicPropertySource`, Spring apunta el datasource al contenedor recién creado.

### Por qué aprenderlo
Es el **estándar de la industria** para tests de integración modernos. Empresas como Netflix, Uber y ING lo usan a diario. Va en tu CV.

```mermaid
graph TD
    A["Test JUnit 5"] -->|@Testcontainers| B["Testcontainers Extension"]
    B -->|arranca| C["Contenedor Postgres 16"]
    B -->|arranca| D["Contenedor Redis 7"]
    E["@DynamicPropertySource"] -->|inyecta URL| F["Spring Environment"]
    F --> G["Spring Boot Context"]
    G -->|usa datasource real| C
    G -->|usa cache real| D

    style C fill:#336791,color:#fff
    style D fill:#DC382D,color:#fff
    style G fill:#6DB33F,color:#fff
```

### Glosario Básico
| Término | Explicación |
|---|---|
| `@Testcontainers` | Anotación JUnit 5 que activa el ciclo de vida de los `@Container`. |
| `@Container` | Marca un contenedor Docker gestionado por Testcontainers. |
| `PostgreSQLContainer<>` | Wrapper específico para Postgres con `getJdbcUrl()`, `getUsername()`, `getPassword()`. |
| `GenericContainer<>` | Wrapper genérico para cualquier imagen (Redis, Kafka, RabbitMQ). |
| `@DynamicPropertySource` | Registra propiedades **antes** de crear los beans (crítico para el DataSource). |
| `withExposedPorts(6379)` | Testcontainers mapea 6379 interno a un puerto libre del host. Usa `getMappedPort(6379)` para leerlo. |

### Conceptos

#### 1. Ciclo de vida `@Testcontainers` + `@Container static`
- **Qué es:** el `@Container static` arranca UNA vez por clase, ANTES de `@BeforeAll`.
- **Por qué importa:** el contenedor debe estar arriba antes de que Spring intente conectarse. Los beans de DataSource se crean al construir el contexto.
- **Edge case crítico** (ver `MEMORY.md`): NO puedes usar `Assumptions.assumeTrue(dockerAvailable)` para saltar la clase — el contenedor ya intentó arrancar antes de tu `@BeforeAll`. Solución: marcar con `@Disabled("Requiere Docker Desktop")`.

#### 2. `@DynamicPropertySource`
- **Qué es:** un mecanismo para registrar propiedades del `Environment` de Spring en tiempo de arranque.
- **Por qué importa:** el contenedor Postgres expone un puerto random. Sin este mecanismo no sabrías qué URL poner en `application.yml`.
- **Analogía:** es como el conserje del hotel que apunta el número de habitación en la ficha del huésped justo cuando le entrega la llave.

#### 3. Múltiples contenedores en un mismo test
- Puedes tener Postgres + Redis + Kafka en la misma clase. Cada `@Container static` es independiente.
- Testcontainers los arranca en paralelo cuando puede.

### Antes vs Ahora

| Aspecto | ANTES (H2 fake) | AHORA (Testcontainers) |
|---|---|---|
| BD de test | H2 en memoria | Postgres real en Docker |
| Cache de test | `Mockito.mock(RedisTemplate)` | Redis real en Docker |
| Sintaxis SQL | Modo "compatibilidad Postgres" (aproximado) | 100 % la sintaxis de Postgres |
| Detección de bugs | Baja, aparecen en QA/PROD | Alta, aparecen en el laptop |
| Velocidad | Instantáneo | 2 a 5 s de arranque, luego rápido |

Sintaxis Java 21 aplicada:
```java
// ANTES (Java 8): cast explícito
if (o instanceof Product) { Product p = (Product) o; ... }

// AHORA (Java 21): pattern matching
if (o instanceof Product p) { ... }
```

### FAQ del Alumno
- **¿Necesito Docker para compilar el módulo?** No. `mvn package` funciona sin Docker porque los tests que lo requieren están `@Disabled`.
- **¿Cómo activo los tests de Postgres/Redis?** Instala Docker Desktop, elimina la anotación `@Disabled` del test que quieras correr, y ejecuta `mvn test`.
- **¿Por qué `@Disabled` y no `Assumptions.assumeTrue`?** Porque `@Testcontainers` arranca el `@Container static` ANTES de `@BeforeAll`, así que la assumption llegaría tarde. Lección aprendida en el módulo 25.
- **¿Qué es un contenedor Docker?** Una "mini-máquina" aislada que corre software (Postgres, Redis) sin instalarlo en tu SO.
- **¿Por qué `postgres:16-alpine`?** `alpine` es una distribución Linux minúscula (~5 MB), acelera la descarga y el arranque.
- **¿El contenedor sobrevive al test?** No, se destruye al final de la clase de test. Cada corrida empieza limpia.
- **¿Puedo usar Kafka?** Sí, añade `org.testcontainers:kafka` y usa `KafkaContainer`.

### Ejercicios
1. Añade un endpoint `PUT /api/products/{id}` y un test contra Postgres real.
2. Modela un carrito con TTL en Redis y verifica la expiración con Testcontainers.
3. Agrega un tercer contenedor (Kafka) y publica un evento `ProductCreated`.
4. Crea un `@Container` compartido para toda la suite con `@Testcontainers(disabledWithoutDocker = true)`.

### Cómo ejecutar

```powershell
# PowerShell
.\build.ps1
java -jar target\testcontainers-1.0.0.jar
```

```bash
# Git Bash / Linux / macOS
./build.sh
java -jar target/testcontainers-1.0.0.jar
```

**Nota Docker:** el JAR arranca con H2 sin necesidad de Docker. Los tests marcados `@Disabled` requieren Docker Desktop en ejecución para activarse.

Endpoints disponibles:
```
GET    http://localhost:8080/api/products
GET    http://localhost:8080/api/products/{id}
POST   http://localhost:8080/api/products    (body JSON: {"name":"X","price":9.99})
DELETE http://localhost:8080/api/products/{id}
```

### Archivos del Proyecto

| Archivo | Propósito |
|---|---|
| `pom.xml` | Dependencias Spring Boot 4.1.0, Testcontainers BOM 1.20.4, Postgres, Redis, H2. |
| `build.sh` / `build.ps1` | Compilación con toolchain portable (JDK 21 + Maven 3.9.16). |
| `src/main/resources/application.yml` | Config por defecto: H2 en memoria + Redis a `localhost`. |
| `TestcontainersApplication.java` | Punto de entrada Spring Boot. |
| `domain/Product.java` | Entidad JPA (id, name, price BigDecimal). |
| `repository/ProductRepository.java` | Repositorio Spring Data JPA. |
| `controller/ProductController.java` | CRUD REST bajo `/api/products`. |
| `TestcontainersApplicationTests.java` | Smoke test con H2 (siempre verde). |
| `repository/ProductRepositoryPostgresTest.java` | `@Disabled`. Postgres real vía `PostgreSQLContainer`. |
| `service/ProductServiceCacheTest.java` | `@Disabled`. Redis real vía `GenericContainer`. |
