package com.springroadmap.testcontainers.repository;

import com.springroadmap.testcontainers.domain.Product;
// JpaRepository: interfaz mágica de Spring Data. Solo con extenderla, Spring
// genera automáticamente las implementaciones de save, findById, findAll, delete, etc.
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de Product.
 *
 * <p>Analogía: es como decirle a Spring "quiero un archivador de Products".
 * Spring construye el archivador (la implementación) sin que tú escribas SQL.</p>
 *
 * <p>PREGUNTA DE ALUMNO — "¿dónde está el código de save()?"
 *   Spring Data lo GENERA en tiempo de ejecución mediante un proxy dinámico.
 *   Nunca lo verás como archivo .java.</p>
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Sin métodos custom: los heredados de JpaRepository son suficientes para el ejemplo.
}

/*
 * ============================================================================
 * ANTES (Java 8 / Spring 3) vs AHORA (Spring Data 4)
 * ============================================================================
 * ANTES: tenías que implementar el DAO a mano.
 *   public class ProductDao {
 *       @PersistenceContext private EntityManager em;
 *       public Product save(Product p) { em.persist(p); return p; }
 *       public Product findById(Long id) { return em.find(Product.class, id); }
 *       ...
 *   }
 *
 * AHORA: una interfaz vacía basta.
 *   public interface ProductRepository extends JpaRepository<Product, Long> {}
 * ============================================================================
 */
