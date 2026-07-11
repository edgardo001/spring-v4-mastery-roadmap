## 24 — REST Avanzado (Paginación, Filtros, Versionamiento y HATEOAS)

### Propósito
Aprender a diseñar APIs REST verdaderamente profesionales escalables. Esto incluye cómo manejar grandes volúmenes de datos con paginación, cómo filtrar listas de forma dinámica, cómo evolucionar la API sin romper aplicaciones antiguas (Versionamiento) y cómo agregar enlaces hipermedia (HATEOAS).

### Problema que resuelve
- **El problema de `findAll()`**: Si tu tabla de usuarios tiene 10 millones de registros, un `GET /users` tumbará tu base de datos y causará un `OutOfMemoryError` en el servidor al intentar transformar 10 millones de objetos a JSON de un solo golpe.
- **Ruptura de Contratos**: Si necesitas cambiar la estructura del JSON del usuario (ej. dividir `name` en `firstName` y `lastName`), romperás automáticamente el Frontend web y las aplicaciones móviles instaladas en los teléfonos de tus clientes que siguen esperando el campo `name`.
- **Filtros Inflexibles**: Crear un endpoint para cada filtro (`findByEmail`, `findByName`, `findByAgeAndStatus`) genera un código inmanejable.

### Cómo lo resuelve
- **Paginación (`Pageable`)**: Spring Data JPA soporta de caja la paginación a nivel SQL (`LIMIT` y `OFFSET`), pidiendo solo los registros necesarios.
- **Versionamiento (`/v1/`, `/v2/`)**: Mantienes múltiples versiones de tus endpoints simultáneamente para dar tiempo a los clientes de migrar.
- **Filtros Dinámicos (Specifications)**: Permite que el frontend arme consultas dinámicas (ej: "tráeme mayores de 18 que estén activos").
- **HATEOAS**: Añade enlaces al JSON para que el cliente "descubra" qué acciones puede hacer con ese recurso, tal como navegamos por HTML en la web.

### Por qué aprenderlo
Estos son los estándares de la industria para las APIs maduras. Si presentas una prueba técnica para Senior Backend y haces un simple `findAll()` sin paginar, te descalifican al instante. Las APIs públicas de empresas como Github, Stripe o Twitter aplican rigurosamente estos conceptos.

```mermaid
graph TD
    A["Cliente Frontend"] -->|GET /api/v1/users?page=1&size=50&sort=name,asc| B["UserController"]
    B --> C["Spring resuelve 'Pageable' automáticamente"]
    C --> D["UserService"]
    D --> E["UserRepository.findAll(Pageable)"]
    E -->|Genera Query LIMIT 50 OFFSET 50| F["Base de Datos"]
    F -->> E: Retorna 50 Registros
    E -->> D: Retorna Page<User> (con meta-datos)
    D -->> B: Transforma a PagedModel (HATEOAS)
    B -->> A: JSON con los 50 usuarios + Total Pages + Links Prev/Next

    style C fill:#339af0,color:#fff
    style E fill:#ffa94d,color:#fff
    style F fill:#ff6b6b,color:#fff
```

---

### Glosario Básico

#### `Pageable`
Interfaz de Spring Data que abstrae la información de paginación solicitada por el cliente (número de página, tamaño de página y criterios de ordenación).

#### `Page<T>`
Clase que retorna Spring Data JPA. Además de los resultados de la página actual, contiene metadatos vitales para el frontend: `totalPages`, `totalElements`, `isFirst`, `isLast`.

#### `Versionamiento de API`
Estrategia para crear cambios incompatibles (breaking changes). Las formas más comunes son por URI (`/api/v1/users`), por Header (`Accept-Version: v1`) o por Content-Type (`application/vnd.company.v1+json`).

#### `HATEOAS (Hypermedia as the Engine of Application State)`
Nivel más alto de madurez REST (Richardson Maturity Model Nivel 3). El servidor incluye enlaces de navegación (Links) junto a los datos. Ej: Si pides un Usuario, el JSON incluye un enlace para "suspender-usuario". Si el usuario ya está suspendido, ese enlace no se devuelve.

---

### Conceptos

#### 1. Paginación y Ordenación (Pagination & Sorting)
- **Qué es** — Limitar la cantidad de resultados por petición. En Spring, inyectas la interfaz `Pageable` en tu controlador y se la pasas a JPA.
- **Por qué importa** — Protege la CPU/RAM de tu servidor, protege la Base de Datos y hace que la pantalla del usuario cargue rápido.
- **Código** — Paginación simple y efectiva:
  ```java
  // 1. Repositorio (Ya incluye métodos findAll(Pageable) al heredar JpaRepository)
  public interface ProductRepository extends JpaRepository<Product, Long> {
      // También sirve para tus queries personalizadas
      Page<Product> findByCategoria(String categoria, Pageable pageable);
  }
  
  // 2. Controlador
  @RestController
  @RequestMapping("/api/v1/products")
  public class ProductController {
  
      private final ProductRepository repository;
  
      // Spring captura mágicamente query params como ?page=0&size=20&sort=precio,desc
      @GetMapping
      public ResponseEntity<Page<Product>> getProducts(
              @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.ASC) 
              Pageable pageable) {
          
          Page<Product> products = repository.findAll(pageable);
          return ResponseEntity.ok(products);
      }
  }
  ```
  **Respuesta JSON resultante:**
  ```json
  {
    "content": [
      { "id": 1, "nombre": "Laptop", "precio": 1000 },
      { "id": 2, "nombre": "Mouse", "precio": 50 }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalPages": 15,
    "totalElements": 150,
    "last": false,
    "first": true,
    "empty": false
  }
  ```

#### 2. Versionamiento por URI
- **Qué es** — Agregar el número de versión (v1, v2) a la URL. Si necesitas romper un contrato de datos, creas el controlador `v2`, mantienes el `v1` intacto por meses, y avisas a los clientes de la migración.
- **Código** — Evolución del controlador:
  ```java
  // Controlador Antiguo (Sigue funcionando)
  @RestController
  @RequestMapping("/api/v1/users")
  public class UserV1Controller {
      
      @GetMapping("/{id}")
      public UserV1Dto getUser(@PathVariable Long id) {
          return new UserV1Dto("John Doe"); // Retorna el nombre completo
      }
  }
  
  // Controlador Nuevo (Nueva estructura JSON)
  @RestController
  @RequestMapping("/api/v2/users")
  public class UserV2Controller {
      
      @GetMapping("/{id}")
      public UserV2Dto getUser(@PathVariable Long id) {
          // Rompe compatibilidad: divide el nombre
          return new UserV2Dto("John", "Doe"); 
      }
  }
  ```

#### 3. Búsqueda y Filtros Dinámicos (Specifications)
- **Qué es** — A veces el frontend te envía diferentes filtros (buscar por nombre, Y por estado, O solo por estado). Hacer métodos fijos en JPA es imposible. Usas `Specification` de JPA para armar queries `WHERE` de forma condicional y dinámica.
- **Código**:
  ```java
  // 1. Heredar JpaSpecificationExecutor
  public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
  }
  
  // 2. Construir la Especificación Dinámica
  public class OrderSpecification {
      public static Specification<Order> getFiltros(String status, Double minAmount) {
          return (root, query, criteriaBuilder) -> {
              List<Predicate> predicates = new ArrayList<>();
              
              if (status != null && !status.isEmpty()) {
                  predicates.add(criteriaBuilder.equal(root.get("status"), status));
              }
              if (minAmount != null) {
                  predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
              }
              
              // Une todos los IFs con un "AND"
              return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
          };
      }
  }
  
  // 3. Uso en el Controller
  @GetMapping("/search")
  public Page<Order> searchOrders(
          @RequestParam(required = false) String status,
          @RequestParam(required = false) Double minAmount,
          Pageable pageable) {
          
      Specification<Order> specs = OrderSpecification.getFiltros(status, minAmount);
      
      // Hace: SELECT * FROM orders WHERE status = ? AND amount >= ? LIMIT 10 OFFSET 0
      return orderRepository.findAll(specs, pageable);
  }
  ```

#### 4. Nivel HATEOAS (Enlaces)
- **Qué es** — Agregar la dependencia `spring-boot-starter-hateoas`. Envuelve tus DTOs en `EntityModel` y les agrega enlaces que el cliente puede seguir.
- **Código**:
  ```java
  @GetMapping("/{id}")
  public EntityModel<UserDto> getUser(@PathVariable Long id) {
      UserDto user = userService.findById(id);
      
      EntityModel<UserDto> resource = EntityModel.of(user);
      
      // self link: GET /api/v1/users/5
      resource.add(WebMvcLinkBuilder.linkTo(
          WebMvcLinkBuilder.methodOn(UserController.class).getUser(id)
      ).withSelfRel());
      
      // action link: DELETE /api/v1/users/5
      if (user.isActive()) {
          resource.add(WebMvcLinkBuilder.linkTo(
              WebMvcLinkBuilder.methodOn(UserController.class).deactivateUser(id)
          ).withRel("deactivate"));
      }
      
      return resource;
  }
  ```

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Retornar `List` con `Pageable` | Usas `List<T> findAll(Pageable p)` | Funciona y limita los resultados, PERO no ejecuta la consulta `COUNT(*)` necesaria para saber el total de páginas. Cambia el retorno a `Page<T>`. |
| Paginación lenta en tablas grandes | El `COUNT(*)` que hace `Page<T>` es lentísimo (escaneo completo) en tablas con 50+ millones de filas | Usa `Slice<T>` en vez de `Page<T>`. `Slice` solo verifica si hay una "página siguiente" (trae LIMIT+1) sin hacer COUNT total. Ideal para "Scroll Infinito" en apps móviles. |
| Sort Property Exception | El Frontend envía `?sort=nombre_cliente`, pero en la Entity Java la variable se llama `nombreCliente` | `Pageable` sort mapéa directamente contra las propiedades de la clase Java, no de las columnas SQL. Usa el nombre exacto de la propiedad. |
| URL Versioning vs Headers | Dudas de cuál estrategia elegir | URI Versioning (`/v1/`) rompe un poco el purismo REST (un recurso no debería cambiar de URL por versión), pero es la más adoptada por su facilidad de cacheo en CDNs y documentación Swagger. |

---

### Ejercicios
1. Crea un endpoint `/api/v1/employees` que soporte `Pageable`.
2. Llena la base de datos con 20 empleados. Haz una petición CURL pasando `?size=5&page=1` (recuerda que la paginación inicia en 0). Observa el JSON de respuesta.
3. Haz otra petición pasando `?sort=salario,desc`. Verifica que vengan ordenados.
4. Implementa una búsqueda dinámica con `Specification` para filtrar empleados por `departamento` y un salario mayor a `X`. Combina esto con la paginación.
5. **(Avanzado)** Cambia la firma del repositorio para que devuelva `Slice<Employee>`. Llama al endpoint. Observa que el JSON resultante ya no tiene `totalPages` ni `totalElements`, pero sí tiene `hasNext`. Es mucho más ligero para la Base de Datos.

### Cómo ejecutar
```bash
cd 24-rest-avanzado
mvn spring-boot:run

# Paginar 5 resultados de la página 0, ordenados por id descendente
curl "http://localhost:8080/api/v1/orders?page=0&size=5&sort=id,desc"

# Buscar con filtros dinámicos y paginados
curl "http://localhost:8080/api/v1/orders/search?status=PENDIENTE&minAmount=100.0&page=0&size=10"
```

### Archivos del Proyecto (esta implementación)
| Archivo | Propósito |
|---------|-----------|
| `domain/Product.java` | Record inmutable (id, name, price, version). `version` alimenta el ETag. |
| `repository/ProductRepository.java` | Repositorio in-memory con `ConcurrentHashMap` y 20 productos precargados. Devuelve `Page<Product>`. |
| `controller/ProductController.java` | Paginación (`Pageable`) + ETag/If-None-Match + versionado por header (`X-API-Version`). |
| `application.yml` | Hardening + defaults de paginación (`default-page-size`, `max-page-size`). |

---

## Antes vs Ahora

### Paginación

**ANTES (Servlet / JDBC crudo):**
```java
int page = Integer.parseInt(req.getParameter("page"));
int size = Integer.parseInt(req.getParameter("size"));
int offset = page * size;
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM products ORDER BY id LIMIT ? OFFSET ?");
ps.setInt(1, size);
ps.setInt(2, offset);
// ... y aparte otro SELECT COUNT(*) para saber totalPages. Y armar el JSON a mano.
```

**AHORA (Spring Boot 4.1):**
```java
@GetMapping
public Page<Product> list(Pageable pageable) {
    return repository.findAll(pageable);
}
```
Spring resuelve `?page=0&size=5` con `PageableHandlerMethodArgumentResolver` y Jackson serializa el `Page<T>` con `content` + `totalPages` + `totalElements` automáticamente.

### Cache HTTP

**ANTES:** todo request devuelve 200 con el JSON completo — el móvil paga datos aunque el recurso no haya cambiado.

**AHORA:** el servidor manda `ETag: "v1"`. En el siguiente request el cliente añade `If-None-Match: "v1"`. Si el `version` no cambió, respondemos 304 Not Modified **sin cuerpo** — el cliente reutiliza su copia local.

### Versionado

**ANTES:** duplicar controllers (`/api/v1/products`, `/api/v2/products`). Pesado y ensucia el árbol de URLs.

**AHORA (header-based):** mismo endpoint, el cliente pide con `X-API-Version: 2` y recibe una forma distinta del cuerpo (`{ "data": {...} }`). Un único controller decide.

---

## FAQ Alumno

**P: ¿Por qué el test registra `PageableHandlerMethodArgumentResolver` a mano?**
En `MockMvcBuilders.standaloneSetup(...)` NO se cargan los argument resolvers del contexto Spring. Si el controller pide un `Pageable` y no lo registras, MockMvc lanza `IllegalStateException: No primary or single unique constructor found`. Con `.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())` queda resuelto.

**P: ¿Por qué el ETag lleva comillas dobles?**
Es la sintaxis oficial del RFC 7232. Un ETag válido es `"v1"` (con comillas), no `v1`. `ResponseEntity.eTag("...")` te obliga a pasarlas — si te olvidas, lanza `IllegalArgumentException`.

**P: ¿Por qué el repositorio no es una interface como en JPA?**
Este módulo enfoca REST puro, sin BD. Un `ConcurrentHashMap` alcanza para enseñar paginación con `Page/Pageable`. El patrón de la interface + `JpaRepository` lo viste en 07 y 18.

**P: ¿`Page<Product>` no da warning al serializar en Boot 4?**
Sí, si construyes `new PageImpl<>(list)` sin pasar Pageable y total, Spring loguea un warning y en Boot 4 puede fallar la serialización. Por eso siempre usamos `new PageImpl<>(content, pageable, totalElements)`.

**P: ¿Por qué versionar por header y no por URI?**
Es equivalente en robustez. Header-based mantiene los URIs limpios (`/api/products/1` es SIEMPRE ese recurso) — más purismo REST. URI versioning (`/v1/`, `/v2/`) es más cómodo para CDNs y Swagger. Ambos válidos; este módulo enseña el header porque es menos común en tutoriales.

**P: ¿Qué gana el cliente con un 304?**
Ahorra ancho de banda: el body no viaja. Con recursos grandes (imágenes, JSON de 500KB) es la diferencia entre pagar datos móviles o no. El navegador aplica esto solito con la caché HTTP.

**P: ¿Por qué `max-page-size: 100` en el yml?**
Defensa contra DoS: sin el máximo, un cliente puede pedir `?size=1000000` y forzar al servidor a materializar toda la tabla en RAM. `default-page-size` protege cuando el cliente olvida el parámetro.

---

## Cómo ejecutar

```bash
# Build (Windows)
./build.ps1
# Build (Linux/macOS/Git Bash)
./build.sh

java -jar target/rest-avanzado-1.0.0.jar

# Paginación
curl "http://localhost:8080/api/products?page=0&size=5"

# ETag: primera llamada devuelve 200 + header ETag: "v1"
curl -i http://localhost:8080/api/products/1

# Segunda llamada con If-None-Match → 304 Not Modified (sin body)
curl -i -H 'If-None-Match: "v1"' http://localhost:8080/api/products/1

# Versionado por header
curl -H 'X-API-Version: 2' http://localhost:8080/api/products/1
# → {"data":{"id":1,"name":"Product-1","price":10.00,"version":"v1"}}
```
