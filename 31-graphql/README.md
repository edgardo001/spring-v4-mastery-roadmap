## 31 — Construyendo APIs con GraphQL (Spring GraphQL)

### Propósito
Aprender a construir APIs utilizando **GraphQL**, un lenguaje de consultas para APIs que soluciona los mayores problemas de REST al permitir que los clientes (Frontend/Móvil) soliciten exactamente los datos que necesitan, ni un campo más, ni un campo menos.

### Problema que resuelve
En una API REST clásica, tienes los problemas de Over-fetching y Under-fetching:
- **Over-fetching (Traer de más)**: Si una App Móvil solo necesita mostrar el `nombre` y la `foto` de un Usuario, al llamar a `GET /api/users/1` la API REST le devuelve un JSON gigante con 50 campos (email, dirección, roles, fecha de registro). Estás desperdiciando ancho de banda y batería del teléfono.
- **Under-fetching (Traer de menos)**: Si necesitas mostrar el Usuario, y también una lista de sus últimos 3 Pedidos, REST a menudo te obliga a hacer dos peticiones distintas (`GET /users/1` y `GET /users/1/orders`). Estas "llamadas extra" (Round-trips) en redes móviles lentas matan la experiencia de usuario.

### Cómo lo resuelve
GraphQL expone un **único endpoint** (`/graphql`). El Frontend envía una "Consulta" (Query) especificando exactamente qué quiere.
Ejemplo de consulta enviada por el frontend:
```graphql
query {
  usuario(id: 1) {
    nombre
    foto
    pedidos(limite: 3) {
      total
    }
  }
}
```
El servidor de Spring parsea esto y devuelve *exactamente* esa estructura JSON. Solucionamos Over y Under-fetching de un solo golpe.

### Por qué aprenderlo
Gigantes tecnológicos como Facebook (quien lo inventó), GitHub y Netflix usan GraphQL intensivamente. Para aplicaciones con muchísimas interfaces distintas (Web, iOS, Android, SmartTV), GraphQL permite que un solo Backend sirva a todos los frontends sin tener que crear endpoints REST a la medida para cada uno (`/api/mobile/user`, `/api/web/user`).

```mermaid
graph TD
    A["Frontend Web"] -->|"Pide Usuario + Posts"| B["/graphql (Único Endpoint)"]
    C["App iOS"] -->|"Pide solo Nombre del Usuario"| B
    
    B --> D["Spring GraphQL (Controller)"]
    
    D -->|"Resuelve Datos"| E[(Base de Datos)]
    E -->> D: Resultados completos
    
    D -->|"Filtra a medida"| A
    D -->|"Filtra a medida"| C
    
    style B fill:#e535ab,color:#fff
    style D fill:#51cf66,color:#fff
```

---

### Glosario Básico

#### `Schema` (.graphqls)
El "contrato" de tu API. Un archivo de texto donde defines fuertemente los Tipos (`type Usuario`), Consultas (`type Query`) y Modificaciones (`type Mutation`). Si algo no está en el Schema, el cliente no puede pedirlo.

#### `Query` (Consulta)
Operación GraphQL utilizada para **leer** datos (El equivalente a `GET` en REST).

#### `Mutation` (Mutación)
Operación GraphQL utilizada para **modificar** datos (crear, actualizar, borrar). (El equivalente a `POST/PUT/DELETE` en REST).

#### `@QueryMapping` y `@MutationMapping`
Anotaciones de Spring Boot que conectan un método Java con un campo definido en el archivo de Schema.

#### `GraphiQL`
Una interfaz web integrada (similar a Swagger) que permite probar tus consultas GraphQL visualmente, con autocompletado y documentación generada automáticamente a partir de tu Schema.

---

### Conceptos

#### 1. Definiendo el Esquema (Schema First)
- **Qué es** — Spring Boot for GraphQL usa el enfoque "Schema-First". Primero escribes el contrato en un archivo de esquema, y luego escribes el código Java que cumpe ese contrato.
- **Por qué importa** — El Schema actúa como documentación viviente. Los desarrolladores Frontend pueden empezar a trabajar leyendo el schema sin siquiera mirar tu código Java.
- **Código** — Crear archivo `src/main/resources/graphql/schema.graphqls`:
  ```graphql
  # Definición de Entidades
  type Libro {
      id: ID! # El "!" significa que el campo es obligatorio (non-null)
      titulo: String!
      paginas: Int
      autor: Autor!
  }
  
  type Autor {
      id: ID!
      nombre: String!
  }
  
  # Todas las operaciones de LECTURA van aquí
  type Query {
      obtenerLibro(id: ID!): Libro
      listarLibros: [Libro] # Retorna una lista de libros
  }
  
  # Operaciones de ESCRITURA
  type Mutation {
      crearLibro(titulo: String!, paginas: Int!, autorId: ID!): Libro
  }
  ```

#### 2. Implementando los Controladores (@Controller)
- **Qué es** — Ya no usas `@RestController` ni `@GetMapping`. Usas controladores normales de Spring con las anotaciones de mapeo de GraphQL.
- **Código**:
  ```java
  @Controller // No es @RestController!
  public class LibroController {
  
      private final LibroService service;
      
      public LibroController(LibroService service) { this.service = service; }
  
      // El nombre del método debe coincidir con el campo de 'type Query' del schema.graphqls
      @QueryMapping
      public Libro obtenerLibro(@Argument Long id) {
          return service.findById(id); // Retorna tu objeto Java, Spring lo filtrará como dicte el cliente
      }
  
      @QueryMapping
      public List<Libro> listarLibros() {
          return service.findAll();
      }
  
      @MutationMapping
      public Libro crearLibro(@Argument String titulo, 
                              @Argument Integer paginas, 
                              @Argument Long autorId) {
          return service.save(titulo, paginas, autorId);
      }
  }
  ```

#### 3. Resolviendo Campos Anidados (`@SchemaMapping`)
- **Qué es** — Un campo de tu Schema (`autor` dentro de `Libro`) puede requerir consultar otra tabla. GraphQL es "perezoso": si el cliente Frontend **NO** pide el campo `autor` en su Query, no queremos hacer la consulta de base de datos a la tabla de Autores.
- **Por qué importa** — Aquí es donde brilla el rendimiento de GraphQL. Ejecutas lógica pesada SOLO si el cliente explícitamente pide esa rama de los datos.
- **Código**:
  ```java
  @Controller
  public class LibroController {
      
      // ... otros mapeos ...
  
      /**
       * Si (y solo si) el frontend pide el campo 'autor' del Libro, este método se ejecuta.
       * Spring le inyecta el 'Libro' padre que ya resolvió previamente.
       */
      @SchemaMapping(typeName = "Libro", field = "autor")
      public Autor obtenerAutorDelLibro(Libro libro) {
          // Consultamos a la BD solo porque el cliente nos lo pidió
          return autorService.findById(libro.getAutorId());
      }
  }
  ```

#### 4. Configurando y Probando con GraphiQL
- **Qué es** — Spring Boot puede habilitar una consola web para jugar con tu API, muy similar a Postman.
- **Código** — `application.yml`:
  ```yaml
  spring:
    graphql:
      graphiql:
        enabled: true
        path: /graphiql
  ```
- Para probar, abres el navegador en `http://localhost:8080/graphiql` y escribes:
  ```graphql
  # Petición:
  query {
    obtenerLibro(id: 1) {
      titulo
      autor {
        nombre
      }
    }
  }
  ```

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| El Problema N+1 de GraphQL | Si pides 10 libros y a todos les pides el `autor`, el `@SchemaMapping` se ejecutará 10 veces (10 consultas extras) | Usar `@BatchMapping` en lugar de `@SchemaMapping`. Spring agrupa las peticiones y hace 1 solo SELECT `WHERE autor_id IN (1,2,3)`. (Usa DataLoader por detrás). |
| Error `Field not found in type` | El nombre en Java no coincide exactamente con el archivo `.graphqls`. | Revisa la ortografía. Si quieres nombres distintos, usa `@QueryMapping(name = "nombreEnGraphQL")`. |
| Exponer la Base de Datos completa | GraphQL te permite pedir jerarquías infinitas (`libro -> autor -> libros -> autor`). | Configurar restricciones de profundidad máxima (Max Query Depth) en Spring Security/GraphQL para evitar ataques de Denegación de Servicio (DoS). |
| ¿Qué pasa con los Códigos de Estado HTTP? | Una petición GraphQL SIEMPRE devuelve HTTP 200 OK, incluso si hay un error (ej. Validaciones). | Los errores se devuelven en el cuerpo del JSON dentro de un arreglo llamado `"errors"`. Es un paradigma distinto a REST. |

---

### Ejercicios
1. Añade la dependencia `spring-boot-starter-graphql` al proyecto.
2. Crea la carpeta `src/main/resources/graphql` y dentro el archivo `schema.graphqls`.
3. Define un tipo `Producto` (id, nombre, precio) y un tipo `Query` con `obtenerProducto(id: ID!): Producto`.
4. Crea un `@Controller` con `@QueryMapping` que devuelva datos quemados (mock) para el Producto.
5. Habilita GraphiQL en tu `application.yml`, entra con el navegador y ejecuta la query pidiendo *solamente* el `nombre` (sin el precio). Verifica que la respuesta JSON es exacta.

### Cómo ejecutar
```bash
cd 31-graphql
mvn spring-boot:run

# Abre el navegador en:
# http://localhost:8080/graphiql
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Dependencia: `spring-boot-starter-graphql`. |
| `src/main/resources/graphql/schema.graphqls` | El contrato de la API. Types, Queries y Mutations. |
| `controller/LibroController.java` | Controladores anotados con `@QueryMapping`, `@MutationMapping` y `@SchemaMapping`. |
| `application.yml` | Activación de la interfaz web GraphiQL. |
