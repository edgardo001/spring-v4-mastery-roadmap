package com.springroadmap.testadv.repository;

import com.springroadmap.testadv.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para la entidad {@link Book}.
 *
 * Analogía del mundo real:
 *   El "bibliotecario" que sabe guardar, buscar y borrar libros del estante.
 *   Nosotros NO implementamos nada: Spring genera la clase concreta en tiempo de arranque
 *   usando proxies dinámicos.
 *
 * Palabras clave explicadas:
 *   - {@code interface}: contrato, no implementación. Spring pone la implementación por ti.
 *   - {@code extends JpaRepository<Book, Long>}: hereda métodos {@code save()}, {@code findById()},
 *     {@code findAll()}, {@code deleteById()}, paginación, y más.
 *     El primer tipo es la Entity; el segundo, el tipo del @Id.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES — Escribir un DAO a mano con {@code EntityManager.persist()} + {@code find()} + queries HQL manuales.
 *   AHORA — Una interfaz vacía. Spring Data JPA cubre el 90% de los casos por convención.
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    // Sin métodos custom por ahora: JpaRepository ya nos da todo lo necesario para el CRUD.
}
