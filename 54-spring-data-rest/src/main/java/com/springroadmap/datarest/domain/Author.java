package com.springroadmap.datarest.domain;

// Anotaciones JPA (Jakarta Persistence): mapean la clase Java a una tabla SQL.
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Author (autor de libros).
 *
 * <p>Analogia: cada fila de la tabla 'authors' es como una ficha de biblioteca
 * con el nombre del autor. Java "traduce" esa fila a un objeto de esta clase.</p>
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 * // Antes: POJO clasico con private + getters + setters + constructor vacio.
 * // Ahora: podriamos usar 'record', pero JPA aun exige clases mutables con
 * // constructor sin argumentos, asi que mantenemos el estilo clasico.
 * </pre>
 */
// @Entity le dice a JPA/Hibernate: "esta clase se mapea a una tabla".
@Entity
// @Table permite fijar el nombre de la tabla; sin esto usaria 'author' (singular).
@Table(name = "authors")
public class Author {

    // @Id marca la clave primaria de la tabla.
    @Id
    // @GeneratedValue con IDENTITY delega la generacion del id a la BD (autoincrement).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Columna simple; Hibernate infiere el nombre 'name' y tipo VARCHAR.
    private String name;

    // Constructor SIN argumentos: OBLIGATORIO para JPA (Hibernate crea la instancia por reflexion).
    protected Author() {
    }

    // Constructor conveniente para crear autores en codigo o tests.
    public Author(String name) {
        this.name = name;
    }

    // Getters y setters: Spring Data REST + Jackson los usan para serializar a JSON.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
