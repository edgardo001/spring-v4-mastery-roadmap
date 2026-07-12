package com.springroadmap.datarest.repository;

import com.springroadmap.datarest.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repositorio de Book expuesto como REST HAL en <code>/api/books</code>.
 *
 * <p>La relacion ManyToOne con Author se expone como sub-recurso HAL:
 * GET /api/books/1/author devuelve el autor del libro 1.</p>
 */
@RepositoryRestResource(path = "books", collectionResourceRel = "books")
public interface BookRepository extends JpaRepository<Book, Long> {
}
