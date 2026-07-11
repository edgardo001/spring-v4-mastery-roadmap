package com.springroadmap.jpaadv.repository;

import com.springroadmap.jpaadv.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para `Product`.
 *
 * Analogía: un CATÁLOGO INTELIGENTE. Le pides al bibliotecario "traeme los
 * productos caros" y él construye el SQL, ejecuta, mapea filas a objetos
 * Product y te los entrega.
 *
 * Palabras clave (por qué extendemos `JpaRepository`):
 * - Gratis: `save`, `findById`, `findAll`, `deleteById`, `count`, `existsById`.
 * - Método derivado por nombre (`findByNameContaining`): Spring parsea el
 *   nombre del método y genera la query — sin escribir SQL/JPQL.
 * - `@Query`: JPQL a mano cuando la query es compleja.
 * - `@EntityGraph`: fuerza cargar relaciones LAZY en la MISMA query (JOIN).
 *
 * ANTES (Java 8 / JDBC crudo) vs AHORA (Spring Data + Java 21):
 * <pre>
 *   // ANTES: DAO manual con Connection/PreparedStatement/ResultSet:
 *   //   PreparedStatement ps = con.prepareStatement(
 *   //       "SELECT p.*, c.* FROM products p " +
 *   //       "LEFT JOIN categories c ON p.category_id = c.id " +
 *   //       "WHERE p.id = ?");
 *   //   ps.setLong(1, id); ResultSet rs = ps.executeQuery();
 *   //   // + mapeo manual columna a campo, cerrar recursos, try/catch SQLException...
 *   //
 *   // AHORA:
 *   //   @EntityGraph(attributePaths = "category")
 *   //   Optional<Product> findWithCategoryById(Long id);
 *   //   // Spring genera el JOIN y el mapeo. Cero boilerplate.
 * </pre>
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Query JPQL personalizada — retorna productos con precio > `min`.
     *
     * JPQL (Java Persistence Query Language) NO es SQL: opera sobre entidades
     * y sus campos Java (`Product`, `p.price`), no sobre tablas y columnas.
     * Hibernate traduce a SQL al vuelo.
     *
     * `@Param("min")` vincula el parámetro nombrado `:min` al argumento Java.
     * Evita inyección SQL (parámetros vinculados, nunca concatenados).
     */
    @Query("SELECT p FROM Product p WHERE p.price > :min")
    List<Product> findExpensive(@Param("min") BigDecimal min);

    /**
     * `@EntityGraph(attributePaths = {"category"})`: le dice a Spring Data
     * que en ESTA query específica cargue eagerly la relación `category`
     * mediante un LEFT JOIN FETCH. Resultado: UNA sola query en vez de
     * dos, y sin `LazyInitializationException` si accedes a la category
     * fuera de la transacción.
     *
     * PREGUNTA DE ALUMNO — "¿por qué no cambio `fetch = LAZY` a `EAGER` en
     * el @ManyToOne y me evito el @EntityGraph?"
     *   Porque EAGER es GLOBAL: cada vez que cargues un Product, Hibernate
     *   traerá SIEMPRE la category, incluso en endpoints donde no la
     *   necesitas. @EntityGraph es LOCAL a la query: haces LAZY por defecto
     *   y cargas la relación solo donde realmente la usas. Mejor rendimiento.
     */
    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findWithCategoryById(Long id);

    /**
     * Método derivado del nombre. Spring lo parsea:
     *   findBy + Name + Containing → WHERE p.name LIKE '%?%'
     * Con `Pageable`, retorna un `Page` que incluye contenido, total, etc.
     *
     * Ideal para búsquedas paginadas por texto parcial.
     */
    Page<Product> findByNameContaining(String q, Pageable pageable);

    /**
     * Retorna una lista de proyecciones `ProductSummary` (solo id + name).
     * Spring detecta el tipo de retorno y genera `SELECT p.id, p.name FROM
     * Product p`. Sin cargar `price` ni `category`.
     *
     * Convención del nombre: `findAllProjectedBy` sigue la sintaxis
     * "findAll...ProjectedBy" que Spring Data reconoce.
     */
    List<ProductSummary> findAllProjectedBy();
}
