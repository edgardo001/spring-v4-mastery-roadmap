# 18 — JPA Avanzado (`@Query`, Projections, `@EntityGraph`, Pagination)

## Propósito
Aprender las cuatro herramientas que separan el CRUD ingenuo del acceso a datos profesional en Spring Data JPA: **queries personalizadas** (`@Query`), **proyecciones** (solo columnas necesarias), **carga selectiva de relaciones** (`@EntityGraph`) y **paginación** (`Pageable`).

## Problema que resuelve
El CRUD básico del módulo 07 falla en producción por tres razones:
1. **N+1 queries**: al listar 1000 productos y acceder a `product.getCategory().getName()`, Hibernate emite 1001 selects. La app se cae bajo carga.
2. **Payloads gigantes**: devolver la entidad completa (con todas sus relaciones) por un endpoint de autocompletar es un desperdicio de I/O y memoria.
3. **OOM en listados**: `findAll()` con una tabla de millones de filas revienta la JVM. Sin `Pageable`, no hay defensa.

## Cómo lo resuelve
- `@Query` — permite JPQL/SQL nativo con parámetros vinculados (sin inyección SQL).
- **Interface Projections** — Spring genera un `SELECT` solo de las columnas que expone la interfaz. Menos datos, más rápido.
- `@EntityGraph` — le dice a Spring que en ESA query específica cargue relaciones LAZY con un `LEFT JOIN FETCH`, en la misma sentencia SQL. Adiós N+1.
- `Pageable` / `Page<T>` — Spring Data maneja `LIMIT`/`OFFSET` (+ un `SELECT COUNT(*)` para el total) de forma transparente.

## Por qué aprenderlo
Todo microservicio real de catálogo, e-commerce, banca o marketplace usa estos cuatro patrones. Sin ellos, tu app no pasa de la demo.

```mermaid
flowchart LR
    Client["Cliente HTTP"] -->|"GET /api/products?q=lap&page=0&size=10"| Ctrl["ProductController"]
    Ctrl -->|"Pageable"| Repo["ProductRepository"]
    Repo -->|"@Query / @EntityGraph"| Hib["Hibernate"]
    Hib -->|"SQL con JOIN y LIMIT"| H2["H2 in-memory"]
    style Ctrl fill:#4CAF50,color:#fff
    style Repo fill:#2196F3,color:#fff
    style Hib fill:#FF9800,color:#fff
    style H2 fill:#9C27B0,color:#fff
```

## Glosario Básico
| Término | Explicación |
|---|---|
| `@Query` | Anotación que permite escribir JPQL o SQL nativo en un método del repositorio. |
| **JPQL** | Java Persistence Query Language. Se parece a SQL pero opera sobre entidades (`Product p`), no tablas. |
| **Projection** | Interfaz con getters que representa una vista parcial de la entidad. Spring materializa solo esos campos. |
| `@EntityGraph` | Define qué relaciones LAZY se cargan eagerly en UNA query específica (LEFT JOIN FETCH). |
| **N+1** | Problema: 1 query para el listado + N queries adicionales por cada elemento al acceder a una relación LAZY. |
| `Pageable` | Contrato de Spring Data que encapsula `page`, `size`, `sort`. |
| `Page<T>` | Resultado de una query paginada. Incluye contenido + total + total de páginas. |
| `BigDecimal` | Tipo Java para dinero. Precisión exacta (a diferencia de `double`). |

## Conceptos

### 1. `@Query` con JPQL y parámetros vinculados
```java
@Query("SELECT p FROM Product p WHERE p.price > :min")
List<Product> findExpensive(@Param("min") BigDecimal min);
```
- **Qué es**: una consulta escrita a mano en JPQL, ligada a un método del repositorio.
- **Por qué importa**: cuando los method-name queries (`findByPriceGreaterThan`) no bastan (por complejidad, joins, o legibilidad), `@Query` es tu escape hatch.
- **Analogía**: pedirle al bibliotecario "traeme todos los libros publicados después de 2020" con una nota específica en vez de usar el índice general.
- **Casos empresariales**: reportes, cálculos agregados, filtros con múltiples condiciones dinámicas.

### 2. Interface Projections
```java
public interface ProductSummary {
    Long getId();
    String getName();
}
List<ProductSummary> findAllProjectedBy();
```
- **Qué es**: interfaz cuyos getters mapean 1-a-1 a propiedades de la entidad. Spring genera un `SELECT id, name FROM products` en runtime.
- **Por qué importa**: menos I/O, menos RAM, respuestas HTTP más chicas. Vital en endpoints de autocompletar, dropdowns, listados ligeros.
- **Analogía**: pedir la TARJETA DE PRESENTACIÓN en lugar del expediente completo.

### 3. `@EntityGraph` — anti N+1
```java
@EntityGraph(attributePaths = {"category"})
Optional<Product> findWithCategoryById(Long id);
```
- **Qué es**: hint a Hibernate para que en ESTA query cargue la relación `category` con un `LEFT JOIN FETCH`.
- **Por qué importa**: elimina el N+1 sin sacrificar la política LAZY global. Puedes tener `fetch = LAZY` en la entidad (rápido por defecto) y aún así cargar la relación DONDE la necesitas.
- **Edge case**: si accedes a una relación LAZY fuera de la transacción sin @EntityGraph, obtienes `LazyInitializationException`. Documentado en MEMORY.md desde el módulo 07.

### 4. `Pageable` / `Page<T>`
```java
Page<Product> findByNameContaining(String q, Pageable pageable);
```
- **Qué es**: paginación built-in de Spring Data. `PageRequest.of(0, 10)` = primera página de 10 elementos.
- **Por qué importa**: sin esto, `findAll()` en una tabla de millones te tumba la JVM (regla #17 de AGENTS.md).

## Antes vs Ahora (SQL crudo + JOIN manual → `@EntityGraph` + `Pageable`)
| Aspecto | ANTES (JDBC crudo / Java 8) | AHORA (Spring Data + Java 21) |
|---|---|---|
| Query con parámetros | `PreparedStatement ps = con.prepareStatement("SELECT * FROM products WHERE price > ?"); ps.setBigDecimal(1, min); ResultSet rs = ps.executeQuery();` | `@Query("SELECT p FROM Product p WHERE p.price > :min") List<Product> findExpensive(@Param("min") BigDecimal min);` |
| JOIN manual (N+1 evasion) | `SELECT p.*, c.* FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?` + mapeo manual columna a campo | `@EntityGraph(attributePaths = "category") Optional<Product> findWithCategoryById(Long id);` |
| Paginación | `LIMIT ? OFFSET ?` calculado a mano + query de COUNT aparte | `Page<Product> findByNameContaining(String q, Pageable pageable);` |
| DTO ligero | Loop manual: `while (rs.next()) { list.add(new ProductDto(rs.getLong("id"), rs.getString("name"))); }` | `List<ProductSummary> findAllProjectedBy();` (interfaz con getters). |
| Manejo de null | `if (product == null) return 404;` | `Optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build())` |

## FAQ del Alumno
- **¿Qué diferencia hay entre JPQL y SQL?** JPQL opera sobre entidades JPA (`Product p`, `p.price`), SQL opera sobre tablas y columnas (`products`, `price`). Hibernate traduce JPQL → SQL específico del motor (H2, MySQL, Postgres).
- **¿Por qué usar `Optional` en `findWithCategoryById`?** Porque el producto puede no existir. `Optional` obliga al llamador a considerar ese caso — más seguro que devolver `null`.
- **¿Puedo hacer LAZY todo y usar `@EntityGraph` siempre?** Sí, es la estrategia recomendada: LAZY por defecto (barato) + `@EntityGraph` local a las queries donde SÍ necesitas la relación.
- **¿Por qué `BigDecimal` y no `double` para el precio?** Porque `0.1 + 0.2 != 0.3` en punto flotante. Con dinero eso equivale a perder centavos. `BigDecimal` es exacto.
- **¿Qué es una proyección "de interfaz"?** Una interfaz con getters que Spring implementa en runtime con un proxy. Solo se materializan las columnas que expone.
- **¿`Page<T>` cuesta más que `List<T>`?** Sí — Spring emite un `SELECT COUNT(*)` adicional para el total. Si no necesitas el total, usa `Slice<T>` (más barato).
- **¿Qué es la arroba `@`?** Se llama anotación. Es metadata que Spring/JPA lee en tiempo de arranque para saber qué hacer con la clase o método.
- **¿Por qué el método se llama `findAllProjectedBy` con esa palabra rara?** Spring Data reconoce el sufijo `...ProjectedBy` como "quiero una proyección". Si el tipo de retorno es una interfaz de proyección, Spring genera el SELECT parcial.

## Ejercicios
1. Agrega un método `Page<Product> findByCategoryId(Long categoryId, Pageable pageable)` y expónelo en un endpoint.
2. Crea una proyección `ProductWithCategoryName` que exponga `id`, `name` y `categoryName` (usa `@Value("#{target.category.name}")`).
3. Agrega un `@Query` nativo (`nativeQuery = true`) para calcular el precio promedio por categoría.
4. Prueba a comentar `@EntityGraph` en `findWithCategoryById` y observa qué pasa al acceder a `product.getCategory().getName()` fuera de transacción (LazyInitializationException).

## Cómo ejecutar
```bash
# Git Bash
./build.sh
java -jar target/jpa-avanzado-1.0.0.jar
```
```powershell
# PowerShell
./build.ps1
java -jar target/jpa-avanzado-1.0.0.jar
```
```bash
# Endpoints
curl "http://localhost:8080/api/products?q=lap&page=0&size=10"
curl "http://localhost:8080/api/products/1"
```

## Archivos del Proyecto
| Archivo | Propósito |
|---|---|
| `pom.xml` | Dependencias Maven (Spring Boot 4.1.0, Web, Data JPA, H2, Test). |
| `build.sh` / `build.ps1` | Scripts que fijan JAVA_HOME al JDK 21 portable y ejecutan `mvn package`. |
| `src/main/java/.../JpaAvanzadoApplication.java` | Bootstrap Spring Boot. |
| `src/main/java/.../domain/Category.java` | Entidad `categories` (id, name). |
| `src/main/java/.../domain/Product.java` | Entidad `products` con `@ManyToOne(LAZY)` a Category. |
| `src/main/java/.../repository/ProductRepository.java` | `@Query`, `@EntityGraph`, method-name query, projection. |
| `src/main/java/.../repository/ProductSummary.java` | Interface-based projection (id, name). |
| `src/main/java/.../controller/ProductController.java` | Endpoints REST `/api/products`. |
| `src/main/resources/application.yml` | Config H2 + Hibernate + inicialización de data.sql. |
| `src/main/resources/data.sql` | Seed: 2 categorías + 4 productos. |
| `src/test/java/.../JpaAvanzadoApplicationTests.java` | `contextLoads`. |
| `src/test/java/.../repository/ProductRepositoryTest.java` | Tests `@DataJpaTest` de `findExpensive`, projection y paginación. |
| `src/test/java/.../controller/ProductControllerTest.java` | Tests MockMvc standalone del controller. |
