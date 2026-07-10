## 07 — Spring Data JPA e Hibernate (Mapeo y Relaciones)

### Propósito
Aprender a mapear objetos Java a tablas de bases de datos relacionales usando JPA e Hibernate, gestionar su ciclo de vida y realizar consultas complejas mediante `JpaRepository` y `@Query`, incluyendo el manejo profundo de relaciones bidireccionales como `@OneToMany` y `@ManyToOne`.

### Problema que resuelve
Antes de JPA y los frameworks ORM, los desarrolladores dependían de JDBC puro. Esto implicaba abrir y cerrar conexiones manualmente, gestionar excepciones `SQLException` chequeadas, y sobre todo, escribir consultas SQL en texto plano esparcidas por todo el código, para luego tener que mapear manualmente fila por fila y columna por columna los resultados de la base de datos hacia objetos Java. Esto producía un código repetitivo (boilerplate), altamente frágil ante mínimos cambios en el esquema de la base de datos, propenso a inyecciones SQL y extremadamente difícil de mantener y escalar. Además, resolver relaciones entre diferentes tablas implicaba realizar y cruzar múltiples llamadas o joins manuales complejos.

### Cómo lo resuelve
Spring Data JPA, utilizando Hibernate como motor subyacente, implementa el patrón de diseño ORM (Object-Relational Mapping). Permite definir la estructura de la base de datos directamente en las clases de dominio usando anotaciones estandarizadas (como `@Entity` y `@Column`). Automáticamente, el framework genera de forma dinámica y segura las sentencias SQL (DML y DDL) en tiempo de ejecución. Con `JpaRepository`, el programador adquiere un arsenal de operaciones CRUD de bases de datos sin tener que programar su lógica interna, y permite definir relaciones orientadas a objetos, haciendo que el acceso a datos sea natural, seguro y robusto.

### Por qué aprenderlo
A nivel corporativo, el ecosistema de JPA e Hibernate constituye el estándar dominante de facto en arquitecturas empresariales sobre la plataforma Java para persistencia de bases de datos relacionales (MySQL, PostgreSQL, Oracle, SQL Server). Independientemente de la adopción creciente de ecosistemas NoSQL, el 90% de los datos críticos financieros, transaccionales, gubernamentales y de identidad residen en bases de datos relacionales tradicionales. Dominar el mapeo y, en especial, comprender cómo Hibernate traduce los objetos a SQL en segundo plano (para prevenir problemas graves de rendimiento), te separará de los desarrolladores junior y te consolidará como un ingeniero backend solvente.

```mermaid
graph TD
    A[Aplicación Spring Boot / Controllers / Services] -->|Inyecta| B(Spring Data JPA - Repositorios)
    B -->|Extiende| C{JpaRepository}
    B -->|Contiene Consultas| D[Métodos Derivados / @Query]
    
    C -->|Delega a| E(Hibernate - Motor ORM)
    D -->|Delega a| E
    
    E -->|Genera SQL Seguro Pre-compilado| F[(Base de Datos Relacional)]
    
    subgraph Mapeo Objeto-Relacional ORM
        G[@Entity Clase Java] <.->|Traducción Bidireccional| H[Tabla SQL]
        I[@Id / @GeneratedValue] <.->|Mapeo| J[Clave Primaria / Auto Increment]
        K[@OneToMany / @ManyToOne] <.->|Mapeo| L[Claves Foráneas - Foreign Keys]
    end
    
    E -.-> Mapeo Objeto-Relacional ORM
```

### Glosario Básico
- **JPA (Java Persistence API / Jakarta Persistence)**: Es la especificación (las reglas, las interfaces) oficial de Java para mapear objetos a bases de datos relacionales. No contiene lógica de ejecución, sólo el contrato de cómo deben usarse las anotaciones.
- **Hibernate**: Es el framework o librería principal que *implementa* las reglas de JPA. Es el motor real bajo el capó que lee las anotaciones, transforma el estado de los objetos a sentencias SQL y se comunica con la base de datos a través del Driver JDBC.
- **ORM (Object-Relational Mapping)**: La técnica y el concepto global de convertir, transformar y vincular datos entre un paradigma orientado a objetos (Java) y un paradigma relacional (SQL).
- **`@Entity`**: Decorador a nivel de clase. Le indica inequívocamente a Hibernate: "Esta clase es una tabla y sus instancias son registros/filas en la base de datos".
- **`@Id`**: Decorador a nivel de campo. Señaliza cuál atributo de la clase representa la Clave Primaria (Primary Key - PK) de la tabla. Todo `@Entity` debe tener un `@Id`.
- **`@GeneratedValue`**: Decorador que acompaña a `@Id` indicando que el desarrollador no asignará este valor manualmente, sino que la base de datos generará la secuencia (por ejemplo, mediante columnas AUTO_INCREMENT o secuencias PostgreSQL).
- **`@Column`**: Decorador a nivel de campo que permite refinar y especificar propiedades intrínsecas de la columna SQL subyacente (nombre en la base de datos, longitud, restricciones de no-nulos, unicidad).
- **`JpaRepository<T, ID>`**: Interfaz genérica de Spring Data JPA que dota a tu aplicación de capacidades inmediatas de base de datos (guardar, actualizar, borrar, paginar y buscar) para el tipo de Entidad `T` con clave primaria de tipo `ID`.
- **`@Query`**: Anotación que se utiliza dentro de los repositorios para redactar consultas a la medida utilizando JPQL (Java Persistence Query Language) o lenguaje SQL nativo, brindando control absoluto sobre la búsqueda.
- **`JPQL (Java Persistence Query Language)`**: Lenguaje de consultas orientado a objetos similar a SQL, pero opera sobre Clases y Atributos Java en lugar de Tablas y Columnas SQL. Hibernate lo traduce a SQL nativo automáticamente en tiempo de ejecución.
- **`@OneToMany` y `@ManyToOne`**: Decoradores utilizados en equipo para definir una asociación (relación de uno a muchos) entre dos entidades, que resultará en la creación de una Foreign Key en la base de datos.
- **Lazy Loading (Carga Perezosa)**: Comportamiento por el cual Hibernate retrasa la consulta y carga de una relación (por ejemplo, los hijos de un padre) hasta el instante exacto en que el código intenta acceder a ellos, mejorando notablemente el rendimiento.

### Conceptos

#### 1. Mapeo Básico de Entidades (`@Entity`, `@Id`, `@GeneratedValue`, `@Column`)
- **Qué es**: Es la acción fundamental del ORM. Consiste en declarar una clase POJO (Plain Old Java Object) y enriquecerla con decoradores JPA para que cada campo privado se acople intrínsecamente a la estructura de una base de datos. Hibernate rastrea los cambios que ocurren en esta clase (Dirty Checking) para realizar actualizaciones sin necesidad de invocar explícitamente sentencias UPDATE.
- **Por qué importa**: Desacopla a la aplicación de un motor de base de datos específico (vendor lock-in). Puedes cambiar tu base de datos de H2 (desarrollo) a PostgreSQL (producción) simplemente modificando las configuraciones (URL, driver y dialecto) y las entidades funcionarán idénticamente, ya que Hibernate generará el SQL adecuado para el nuevo motor.
- **Código**:

```java
package com.springroadmap.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

/**
 * @Entity: Registra esta clase en el Contexto de Persistencia de Hibernate.
 * @Table: (Opcional) permite que el nombre de la tabla difiera del de la clase.
 *         Buena práctica: Poner nombres de tabla en plural y minúsculas (convención estándar SQL).
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor // OBLIGATORIO: Hibernate requiere un constructor vacío para hidratar el objeto (vía Reflection)
@AllArgsConstructor
@Builder
public class Producto {

    /**
     * @Id: Define la Clave Primaria.
     * @GeneratedValue: strategy = GenerationType.IDENTITY significa que usaremos
     *                  el "auto_increment" propio de la base de datos (MySQL/PostgreSQL/H2).
     *                  Otras opciones: SEQUENCE, TABLE, AUTO. (IDENTITY suele ser el más común y seguro).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column: Configuración fina de la columna.
     * nullable = false (No permite NULLs en base de datos)
     * unique = true (Añade un índice UNIQUE, no pueden existir 2 productos con igual código)
     * length = 50 (VARCHAR(50))
     */
    @Column(name = "codigo_sku", nullable = false, unique = true, length = 50)
    private String codigoSku;

    @Column(nullable = false, length = 150)
    private String nombre;

    /**
     * Precision y scale es excelente para datos monetarios (DECIMAL(10,2))
     * NUNCA usar Double/Float para dinero por problemas de precisión de punto flotante.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    /**
     * Propiedad temporal que NO queremos guardar en base de datos.
     * Usamos @Transient (perteneciente a jakarta.persistence).
     */
    @jakarta.persistence.Transient
    private Boolean enOfertaTemporal;
    
    // =========================================================================
    // EDGE CASES (Casos de Error Frecuentes)
    // =========================================================================
    // 1. InstantiationException: Ocurre si omites @NoArgsConstructor. Hibernate
    //    necesita crear la clase vacía antes de llenarla con la data extraída de SQL.
    // 2. Uso de primitivos: Preferir `Long id` (Wrapper object) sobre `long id` (primitivo).
    //    Si el producto es nuevo, un `Long` será `null`, lo que indica a Hibernate que
    //    es una entidad no guardada. Un primitivo iniciará en `0`, confundiendo al ORM
    //    quien creerá que intentas actualizar el registro con id=0.
    // 3. Modificación del esquema accidental: Si en application.yml usas `ddl-auto: update`
    //    en PRODUCCIÓN, Hibernate podría bloquear/alterar tablas arruinando la base de datos.
    //    En producción SIEMPRE se debe usar `validate` o `none` y migrar con Flyway/Liquibase.
}
```
- **Analogía**: Imagina el `@Entity` como un formulario en blanco para empadronamiento gubernamental y Hibernate como el funcionario público. El formulario (`Clase`) detalla el nombre y apellido. Cuando tú llenas un formulario en Java, el funcionario (Hibernate) transfiere esos datos precisos hacia la gigantesca estantería metálica blindada (Base de Datos). El `@Id` es sencillamente el Número de Identificación (DNI o Pasaporte) impreso y autogenerado por la estantería.
- **Casos de Uso Empresariales**: El core de cualquier sistema de dominio transaccional. Por ejemplo, mapear entidades financieras como `CuentaBancaria`, `TarjetaCredito`, `Usuario`, garantizando la pureza de los datos mediante `nullable=false` o `unique=true` en los correos y documentos de identidad.

#### 2. Repositorios Mágicos y Consultas Personalizadas (`JpaRepository` y `@Query`)
- **Qué es**: Spring Data introduce un concepto revolucionario. Simplemente creas una Interfaz (no una clase) extendiendo `JpaRepository`, y Spring automáticamente, en tiempo de ejecución, fabricará una implementación proxy que inyectará en tus Servicios (`@Service`). Para búsquedas condicionales, puedes usar Query Methods (nombrando metódicamente las funciones) o usar `@Query`.
- **Por qué importa**: Reduce el código (boilerplate) de acceso a datos en un 80%. Ya no hay apertura de cursores de BD, ResultSet iterables, mapeos manuales de objetos, ni gestión explícita de transacciones para lecturas. Si el negocio pide "buscar los clientes que tengan un plan premium", se hace con un método que ocupa una sola línea.
- **Código**:

```java
package com.springroadmap.jpa.repository;

import com.springroadmap.jpa.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring Boot crea mágicamente la implementación de esta Interfaz.
 * Producto: El tipo de Entidad que gestionará este repositorio.
 * Long: El tipo de dato definido como @Id en la Entidad Producto.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // =========================================================================
    // 1. QUERY METHODS (Consultas Derivadas del Nombre)
    // =========================================================================
    
    // Spring parsea el nombre del método: "findBy" + "CodigoSku"
    // Genera: SELECT p.* FROM productos p WHERE p.codigo_sku = ?
    Optional<Producto> findByCodigoSku(String codigoSku);

    // Búsqueda cruzada usando condicionales AND, LessThan, etc.
    // Genera: SELECT p.* FROM productos p WHERE p.precio < ? AND p.nombre LIKE ?
    List<Producto> findByPrecioLessThanAndNombreContaining(BigDecimal precioMaximo, String nombre);

    // =========================================================================
    // 2. @QUERY con JPQL (Java Persistence Query Language)
    // =========================================================================
    
    /**
     * Si el nombre del método queda excesivamente largo o la consulta es compleja (Joins, Aggregations),
     * usamos JPQL. Escribimos orientándonos en los OBJETOS (Producto) y sus variables Java (p.precio),
     * NO en la base de datos (productos / p.codigo_sku).
     */
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :min AND :max ORDER BY p.precio DESC")
    List<Producto> buscarProductosPorRangoPrecioSeguro(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    // =========================================================================
    // 3. @QUERY con NATIVE SQL
    // =========================================================================
    
    /**
     * Para aprovechar características únicas del motor de base de datos (ej. Postgres LTree,
     * MySQL JSON Search) que JPA/JPQL no contemplan, se usa SQL nativo.
     * nativeQuery = true desactiva el parseador interno de JPQL y lo manda directo a la BD.
     */
    @Query(value = "SELECT * FROM productos p WHERE p.nombre REGEXP :regexPattern", nativeQuery = true)
    List<Producto> buscarConExpresionRegularNativa(@Param("regexPattern") String regexPattern);
    
    // =========================================================================
    // EDGE CASES (Casos de Error Frecuentes)
    // =========================================================================
    // 1. Inyección SQL: NUNCA concatenes Strings para construir queries en Spring Data
    //    (ej. "... WHERE nombre = " + variable). SIEMPRE usa Parámetros Vinculados (@Param(":nombre"))
    //    para asegurar el sanitizado de variables.
    // 2. Tipear mal el Query Method: Si escribes `findByCodgoSku(String c)`, Spring arrojará un error 
    //    en tiempo de arranque `PropertyReferenceException: No property codgoSku found...` impidiendo
    //    que el servidor levante. ¡Es seguro porque falla temprano!
}
```
- **Analogía**: El `JpaRepository` es equivalente al Menú en una pizzería inteligente. Los métodos derivados (`findByNombre`) son las pizzas predeterminadas en el menú, que el cocinero sabe cómo preparar al instante sin que le des receta. Si quieres una pizza exótica no listada, le entregas una nota detallada (usando la notación `@Query`) donde especificas tus propios ingredientes de manera estandarizada y segura.
- **Casos de Uso Empresariales**: Generación de reportes de alta complejidad, endpoints de búsqueda cruzada o filtrado dinámico para listados frontend (ej: e-commerces que necesitan filtros rápidos de categoría, marca y rango de precio), y la obtención de estadísticos agregados sin cargar toda la base de datos a la memoria de la API.

#### 3. Relaciones Bidireccionales (`@OneToMany` y `@ManyToOne`)
- **Qué es**: Una forma de mapear y conectar conceptualmente registros de diferentes tablas reflejando su dependencia. Por ejemplo, en una arquitectura empresarial, un `Departamento` (Recursos Humanos, Ventas, TI) aglomera en su interior a múltiples `Empleados`.
- **Por qué importa**: Representa el tejido conectivo del desarrollo de software backend. Gestionar las relaciones correctamente permite aprovechar las características transaccionales (guardar a un padre y todos sus hijos automáticamente - `Cascade`), además de cuidar el rendimiento mediante la Carga Perezosa (Lazy Loading). Hacerlo mal te llevará a consumir 100% de CPU del servidor base de datos a causa del infame problema `N+1 Queries`.
- **Código**:

```java
package com.springroadmap.jpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departamentos")
@Getter
@Setter
@NoArgsConstructor
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * RELACIÓN LADO PADRE (Uno a Muchos)
     * 
     * `mappedBy = "departamento"`: Indica que esta clase NO posee la llave foránea físicamente, 
     * sino que es la propiedad `departamento` ubicada en la clase hija `Empleado` quien domina y gestiona.
     * `cascade = CascadeType.ALL`: Las operaciones persisten en cascada. Si borro este Departamento, 
     * se borran todos sus empleados de la BD (CUIDADO en la vida real, usar con extremo juicio).
     * `orphanRemoval = true`: Si remuevo un Empleado de esta Lista (Java), se ejecutará
     * un DELETE de ese Empleado en la base de datos automáticamente.
     */
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Empleado> empleados = new ArrayList<>(); // Inicializar SIEMPRE para evitar NullPointerException.

    // =========================================================================
    // BUENA PRÁCTICA: MÉTODOS DE SINCRONIZACIÓN (Helper Methods)
    // Dado que es una relación Bidireccional en memoria (Java), 
    // hay que mantener ambos lados sincronizados antes de persistir.
    // =========================================================================
    public void agregarEmpleado(Empleado empleado) {
        empleados.add(empleado);
        empleado.setDepartamento(this);
    }
    
    public void removerEmpleado(Empleado empleado) {
        empleados.remove(empleado);
        empleado.setDepartamento(null);
    }
}
```

```java
package com.springroadmap.jpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empleados")
@Getter
@Setter
@NoArgsConstructor
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;

    /**
     * RELACIÓN LADO HIJO (Muchos a Uno) / LADO DUEÑO (Owning Side)
     * 
     * `fetch = FetchType.LAZY`: ESTO ES CRÍTICO. Si cargas al Empleado de la BD, NO cruces y extraigas
     * automáticamente al Departamento entero a menos que el código haga explícitamente `empleado.getDepartamento()`. 
     * Por defecto en @ManyToOne, Hibernate usa EAGER (carga ansiosa) lo cual destruye el rendimiento. ¡Forzar LAZY!
     * `@JoinColumn`: El nombre físico de la columna llave foránea en la tabla "empleados".
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false) 
    private Departamento departamento;
    
    // =========================================================================
    // EDGE CASES (Casos de Error Frecuentes - ¡Los más graves en Spring!)
    // =========================================================================
    // 1. LazyInitializationException: Si cargas un Empleado, se cierra la transacción,
    //    y luego en el Controller intentas acceder a `empleado.getDepartamento().getNombre()`.
    //    Hibernate intentará buscarlo en la BD, pero la sesión/transacción ya expiró.
    //    Solución: Acceder dentro del `@Transactional` (Service Layer) o cargar usando FETCH JOIN en `@Query`.
    //
    // 2. Problema N+1 Queries (Rendimiento Severo): Si haces `empleadoRepo.findAll()` 
    //    y tienes 1000 empleados, y por cada uno llamas a `getDepartamento()`,
    //    Hibernate ejecutará 1 consulta primaria + 1000 consultas individuales para departamentos = 1001 Queries.
    //    Solución: Usar un @EntityGraph o `@Query("SELECT e FROM Empleado e JOIN FETCH e.departamento")`.
    //
    // 3. Infinite Recursion de JSON al devolver una Entidad Bidireccional en un REST Controller:
    //    Jackson (la librería que convierte a JSON) intentará serializar:
    //    Departamento -> serializa List<Empleado> -> entra a Empleado -> serializa Departamento -> y así al infinito,
    //    reventando la memoria RAM y arrojando StackOverflowError (o HTTP 500: Infinite recursion).
    //    Solución: NUNCA DEVUELVAS ENTIDADES DESDE UN @RestController. Usa y mapea siempre hacia un DTO (Data Transfer Object).
}
```
- **Analogía**: Las Relaciones en bases de datos son como un sistema de adopción de Mascotas (`@OneToMany`). Un Refugio (lado Padre) alberga muchas Mascotas. El Refugio tiene un catálogo, pero es la Mascota quien tiene colgado físicamente en su collar la chapita de "Propiedad del Refugio XYZ" (`@ManyToOne`, Lado dueño de la foránea). Si el refugio cierra (Cascade ALL), el destino de las mascotas está atado a ello. Y si decides adoptar una Mascota, no necesitas consultar al instante el historial completo del refugio (Carga Perezosa / LAZY Loading).
- **Casos de Uso Empresariales**: Arquitectura relacional estricta: Modelar esquemas de Facturación (Cabecera de Factura / Filas de Detalle). Modelar esquemas organizativos corporativos, o esquemas logísticos en microservicios monolíticos (Warehouse tiene Inventario, Inventario pertenece a Productos).

### Ejercicios
1. **Modelado Básico**: Diseña la entidad `Autor` y la entidad `Libro`. Un Autor debe poder escribir varios Libros.
2. **Consultas a medida**: Crea en el `LibroRepository` un `@Query` (usando JPQL) que te devuelva la lista de libros cuyo precio supere los "20 dólares" y que hayan sido publicados después del año 2020.
3. **Optimización Extrema**: Instala datos de prueba masivos (1 padre, 1000 hijos). Intenta recuperar la entidad y visualiza tus propios Logs en la terminal para descubrir cuántos `SELECT` arroja Hibernate. Experimenta el caos del problema `N+1`. Luego, corrígelo mediante un `@Query` implementando la directriz `JOIN FETCH`.
4. **Crash del Controller**: Expón un `@GetMapping` que devuelva `AutorRepository.findAll()`. Abre tu navegador e impacta el endpoint. Contempla el clásico error de Serialización Infinita JSON. Arréglalo creando un `AutorDto` que no contenga referencias circulares. 

### Cómo ejecutar
1. Cerciórate de tener el entorno de desarrollo en Java 21 y Maven instalados. (La base de datos será virtualizada temporalmente en la RAM usando H2, no necesitas instalar MySQL/Postgres externos).
2. Desde tu terminal de comando, navega al interior de este directorio (`07-jpa-hibernate`) y ejecuta:
   ```bash
   mvn spring-boot:run
   ```
3. El motor arrancará incrustado en el puerto `8080`.
4. Examina la consola de tu terminal minuciosamente: podrás apreciar la magia negra en acción; verás sentencias reales como `Hibernate: create table ...` impresas en tu consola. Hibernate ha tomado tus clases Java y forjado de la nada un esquema de Base de Datos.

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Configuración del gestor Maven. Trae al ruedo la dependencia principal: `spring-boot-starter-data-jpa`, además del driver de base de datos en memoria para pruebas rápidas `h2` y de la navaja suiza `lombok`. |
| `src/main/resources/application.yml` | El centro neurálgico para definir variables. Aquí se le dictamina a Spring Boot que se conecte a la base H2, se enciende la consola web de H2, y CRUCIALMENTE, se activa la directiva `spring.jpa.show-sql: true` para que todo comando enviado por Hibernate quede evidenciado en los logs. |
| `domain/Departamento.java` | La Entidad "Principal/Padre" del ecosistema relacional bidireccional. Alberga una colección de empleados valiéndose del poderoso mapeo `@OneToMany`. |
| `domain/Empleado.java` | La Entidad "Dependiente/Hijo", dueña de la clave foránea física representada por `@JoinColumn`, que mapea la contraparte valiéndose de `@ManyToOne(fetch = FetchType.LAZY)`. |
| `repository/EmpleadoRepository.java` | Interfaz inyectable provista por Spring Data, abanderada con consultas inferidas por método y una consulta artesanal inyectada con `@Query` (JPQL). |
| `repository/DepartamentoRepository.java` | Repositorio puro CRUD y de mantenimiento transaccional básico para gestionar un departamento matriz. |
| `JpaApplication.java` | Archivo núcleo arrancador del proyecto Spring Boot. |

---

## Implementación Real de Este Módulo (Módulo 07)

Como introducción práctica, este módulo implementa un **CRUD mínimo de `Book`** — sin relaciones bidireccionales aún — para consolidar el mapeo básico `@Entity`, `JpaRepository` y su exposición REST con paginación. Los conceptos de `@OneToMany` / `@ManyToOne` explicados arriba se profundizan en ejercicios y módulos posteriores.

### Alcance implementado
- Entidad `Book` con `@Id`, `@GeneratedValue(IDENTITY)`, campos `title`, `author`, `publicationYear`.
- `BookRepository extends JpaRepository<Book, Long>`.
- `BookController` con GET paginado (`Pageable`), GET por id, POST, PUT, DELETE.
- H2 en memoria + `ddl-auto: create-drop` + `show-sql: true`.

### Coordenadas Maven
- `groupId=com.springroadmap`, `artifactId=jpa-hibernate`, `version=1.0.0`.
- Paquete raíz: `com.springroadmap.jpa`.
- Artefacto: `target/jpa-hibernate-1.0.0.jar`.

### Cómo ejecutar (concretamente)
```bash
# Bash (Git Bash)
./build.sh
java -jar target/jpa-hibernate-1.0.0.jar
```
```powershell
# PowerShell
./build.ps1
java -jar target/jpa-hibernate-1.0.0.jar
```

### Endpoints
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/books?page=0&size=10&sort=title` | Lista paginada |
| GET | `/api/books/{id}` | Detalle (200/404) |
| POST | `/api/books` | Crea libro |
| PUT | `/api/books/{id}` | Reemplaza libro |
| DELETE | `/api/books/{id}` | Borra libro (204/404) |

### Tests incluidos
- `JpaHibernateApplicationTests.contextLoads()` — humo.
- `BookRepositoryTest` con `@DataJpaTest` — save, findById, findAll, count.
- `BookControllerTest` con `@SpringBootTest` + `MockMvcBuilders.standaloneSetup()` — CRUD completo.

---

## Antes vs Ahora (Java 8 → Java 21) aplicado a JPA

| Tema | ANTES (Java 8 / Spring 3.x) | AHORA (Java 21 / Spring Boot 4) |
|------|-----------------------------|---------------------------------|
| Acceso a datos | JDBC crudo: `Connection`, `PreparedStatement`, `ResultSet`, `try/catch SQLException`, mapeo manual fila-a-objeto. | `JpaRepository<Book, Long>`: los métodos CRUD, la paginación y la traducción a SQL vienen gratis. |
| Boilerplate por entidad | ~150 líneas: DAO + mapeo `ResultSet` + gestión de conexión + transacciones manuales. | ~50 líneas: `@Entity` + `interface Repository`. Spring genera el resto. |
| Inyección de dependencias | `@Autowired` en campo (`private BookRepository repo;`), tests que requieren reflection. | Constructor injection (`public BookController(BookRepository r) {...}`), inmutable, testable sin Spring. |
| Datos opcionales | `if (row == null) return null; else ...`. | `Optional<Book>` + `.map(...).orElseGet(...)`. |
| Paginación | Cortar `List<Book>` a mano con offsets. | `Pageable pageable` como parámetro; devuelve `Page<Book>`. |
| Generación de esquema | Scripts `.sql` mantenidos a mano, alineación clase↔tabla frágil. | `ddl-auto: create-drop` en dev, Flyway/Liquibase en prod, entidad como fuente de verdad. |
| Inmutabilidad | POJO con setters expuestos siempre. | Clase JPA con constructor protected sin args + constructor público con campos + solo getters. |

### Comparación de código: JDBC crudo vs Spring Data JPA

```java
// ANTES — JDBC puro (Java 8 style)
public Book findById(Long id) throws SQLException {
    try (Connection c = dataSource.getConnection();
         PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE id = ?")) {
        ps.setLong(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return null;
            Book b = new Book();
            b.setId(rs.getLong("id"));
            b.setTitle(rs.getString("title"));
            b.setAuthor(rs.getString("author"));
            b.setPublicationYear(rs.getInt("publication_year"));
            return b;
        }
    }
}
```

```java
// AHORA — Spring Data JPA
Optional<Book> book = repository.findById(id);
```

Una línea sustituye 15+ líneas, elimina el manejo manual de conexiones y previene fugas de recursos e inyecciones SQL.

---

## FAQ del Alumno

- **¿Qué es un `@Entity`?** Una anotación que le dice a JPA: "esta clase Java corresponde a una tabla". Cada instancia = una fila.
- **¿Por qué la clase `Book` no es un `record` como en otros módulos?** Porque JPA necesita crear el objeto vacío por reflection y luego rellenar los campos. `record` es inmutable y no expone constructor sin argumentos — no sirve como entidad JPA.
- **¿Qué es `JpaRepository`?** Una interfaz genérica que, al extenderla, te regala métodos CRUD (`save`, `findById`, `findAll`, `deleteById`, `count`, `findAll(Pageable)`) sin escribir código.
- **¿Quién implementa esa interfaz si yo solo la declaro?** Spring Data JPA en tiempo de arranque genera un *proxy dinámico* que implementa los métodos delegando a Hibernate.
- **¿Qué es H2?** Una base de datos SQL escrita en Java que puede vivir totalmente en memoria RAM. Ideal para pruebas y ejemplos porque no requiere instalación.
- **¿Qué hace `ddl-auto: create-drop`?** Al arrancar la app, Hibernate crea las tablas leyendo tus `@Entity`. Al parar, borra todo. Perfecto para desarrollo, **prohibido en producción**.
- **¿Qué es `Pageable`?** Un objeto que agrupa página, tamaño y ordenamiento. Spring lo construye automáticamente de los query params `?page=0&size=10&sort=title`.
- **¿Por qué `Long id` y no `long id`?** Porque `Long` puede ser `null`, señalando a Hibernate que la entidad es nueva (INSERT). `long` empezaría en 0 y confundiría al motor.
- **¿Qué es "constructor injection"?** Recibir las dependencias como parámetros del constructor en lugar de anotar campos con `@Autowired`. Ventajas: `final`, testeable, sin nulls sorpresa.
- **¿Por qué `@DataJpaTest` en el test de repositorio y `@SpringBootTest` en el de controller?** `@DataJpaTest` carga solo el slice JPA (rápido, con rollback automático). Los tests del controller necesitan más piezas del contexto, así que usamos `@SpringBootTest` + MockMvc standalone.
- **¿Puedo usar la consola web H2 para inspeccionar los datos?** Sí, cambiando `spring.h2.console.enabled: true`. Está deshabilitada por hardening: en producción es una brecha si se expone.
- **¿Qué pasa si me olvido del constructor sin argumentos en la entidad?** Hibernate lanza `InstantiationException` al arrancar: no puede crear la entidad por reflection.

