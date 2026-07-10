package com.springroadmap.jpa.repository;

import com.springroadmap.jpa.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para la entidad `Book`.
 *
 * Es una INTERFAZ (no una clase). Spring Data genera en tiempo de arranque
 * un proxy que implementa todos los métodos de `JpaRepository` (save,
 * findById, findAll, deleteById, count, findAll(Pageable), etc.).
 *
 * Analogía: es un menú de restaurante. Declaras qué platos quieres
 * (métodos), y el "chef" (Spring Data) los cocina automáticamente contra
 * la base de datos.
 *
 * Parámetros genéricos:
 * - `Book`: la entidad gestionada.
 * - `Long`: el tipo de la clave primaria (`@Id`).
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    // No necesitamos declarar nada más para el CRUD básico paginado.
    // Query methods personalizados podrían añadirse aquí más adelante
    // (por ejemplo `List<Book> findByAuthor(String author);`).
}
