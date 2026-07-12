package com.springroadmap.datarest.repository;

import com.springroadmap.datarest.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repositorio de Author expuesto automaticamente como endpoint REST HAL.
 *
 * <p>Analogia: es un menu de restaurante que se genera solo. No escribimos
 * un Controller ni un Service: Spring Data REST publica GET/POST/PUT/DELETE
 * en <code>/api/authors</code> con paginacion, HAL links y HATEOAS.</p>
 *
 * <p>Endpoints generados automaticamente (base-path = /api):</p>
 * <ul>
 *   <li>GET    /api/authors           - listado paginado HAL</li>
 *   <li>GET    /api/authors/{id}      - detalle con _links.self</li>
 *   <li>POST   /api/authors           - crear (201 Created + Location)</li>
 *   <li>PUT    /api/authors/{id}      - reemplazar</li>
 *   <li>PATCH  /api/authors/{id}      - actualizar parcial</li>
 *   <li>DELETE /api/authors/{id}      - borrar</li>
 * </ul>
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 * // Antes: escribias AuthorController + AuthorService + AuthorRepository (3 clases, 100+ lineas).
 * // Ahora: 1 interface + 1 anotacion + JpaRepository = CRUD REST completo gratis.
 * </pre>
 */
// @RepositoryRestResource personaliza la exposicion: path de la URL y nombre del recurso HAL.
// path="authors" -> URL /api/authors
// collectionResourceRel="authors" -> clave "_embedded.authors" en la respuesta HAL.
@RepositoryRestResource(path = "authors", collectionResourceRel = "authors")
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Sin metodos. Spring Data REST genera todo el CRUD automaticamente.
}
