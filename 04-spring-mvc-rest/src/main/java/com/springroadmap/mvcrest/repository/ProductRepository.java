package com.springroadmap.mvcrest.repository;

import com.springroadmap.mvcrest.domain.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio en memoria de productos (sin base de datos).
 *
 * PROPÓSITO
 * ---------
 * Simular el acceso a datos con un {@code Map<Long,Product>} thread-safe.
 * En el módulo 07 (JPA/Hibernate) reemplazaremos esto por un JpaRepository.
 *
 * ANALOGÍA
 * --------
 * Piensa en el Map como los CASILLEROS de un gimnasio: cada casillero tiene
 * un número (id) y guarda un objeto (Product). AtomicLong es el dispensador
 * de tickets numerados que asegura que dos personas no reciban el mismo id.
 *
 * ANTES (Java 8)
 * --------------
 *   private final Map<Long, Product> store = new HashMap<>();
 *   private long sequence = 0L;
 *   public synchronized Product save(Product p) {
 *       long id = ++sequence;
 *       Product saved = new Product(id, p.getName(), p.getPrice());
 *       store.put(id, saved);
 *       return saved;
 *   }
 *
 * AHORA (Java 21 + concurrencia moderna)
 * --------------------------------------
 *   private final Map<Long, Product> store = new ConcurrentHashMap<>();
 *   private final AtomicLong sequence = new AtomicLong(0);
 *   public Product save(Product p) {
 *       long id = sequence.incrementAndGet();
 *       Product saved = new Product(id, p.name(), p.price());
 *       store.put(id, saved);
 *       return saved;
 *   }
 *
 * PREGUNTA DE ALUMNO — "¿qué es @Repository?"
 *   Es un @Component especializado: le dice a Spring "esta clase es un bean
 *   y además representa la capa de acceso a datos". En proyectos con JPA,
 *   además traduce excepciones SQL a DataAccessException.
 */
@Repository
public class ProductRepository {

    // ConcurrentHashMap: Map thread-safe (varios hilos HTTP pueden leer/escribir sin corrupción).
    private final Map<Long, Product> store = new ConcurrentHashMap<>();

    // AtomicLong: contador thread-safe. incrementAndGet() es atómico (no hay carreras).
    private final AtomicLong sequence = new AtomicLong(0);

    /**
     * Constructor: precarga 2 productos para que la API tenga datos de demo.
     * En un caso real esto lo haría un CommandLineRunner o Flyway.
     */
    public ProductRepository() {
        save(new Product(null, "Cafe 250g", new BigDecimal("3990")));
        save(new Product(null, "Cuaderno A4", new BigDecimal("1990")));
    }

    /**
     * Devuelve TODOS los productos. Retorna una copia inmutable para que el
     * consumidor no pueda mutar el estado interno del repositorio.
     *
     * ANTES:  new ArrayList<>(store.values())
     * AHORA:  List.copyOf(store.values())   // inmutable, más semántico.
     */
    public List<Product> findAll() {
        return List.copyOf(store.values());
    }

    /**
     * Busca por id. Retorna Optional para que el llamador maneje explícitamente
     * el "no encontrado" en lugar de recibir null y reventar con NPE.
     *
     * ANTES:   Product p = store.get(id);
     *          if (p == null) throw ...;
     * AHORA:   return Optional.ofNullable(store.get(id));
     *          // luego:  repo.findById(id).orElseThrow(...)
     */
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Guarda un producto. Reglas:
     *   - Si el id es null → es un CREATE: asigna id nuevo y persiste.
     *   - Si el id NO es null → es un UPDATE (o alta forzada con id conocido).
     * Retorna el producto FINAL almacenado (con id ya asignado si era nuevo).
     */
    public Product save(Product product) {
        // PREGUNTA DE ALUMNO — "¿por qué crear otro Product y no modificar el que llegó?"
        //   Porque Product es un record: es inmutable, no tiene setters.
        //   La forma de "cambiar el id" es crear un objeto nuevo con el id nuevo.
        Long id = (product.id() == null) ? sequence.incrementAndGet() : product.id();
        Product toStore = new Product(id, product.name(), product.price());
        store.put(id, toStore);
        return toStore;
    }

    /**
     * Elimina por id. Retorna true si borró algo, false si el id no existía.
     * Esto le permite al controller responder 204 vs 404 sin hacer 2 lookups.
     */
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }
}
