package com.springroadmap.datarest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad Book (libro).
 *
 * <p>Analogia: un libro tiene UN autor. En BD lo modelamos con una columna
 * 'author_id' que apunta a la tabla 'authors' (foreign key).</p>
 *
 * <p>Spring Data REST expone esta relacion como un enlace HAL:
 * <code>{"_links": {"author": {"href": "/api/books/1/author"}}}</code>.</p>
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // @ManyToOne = muchos libros pueden pertenecer a un autor.
    // FetchType.LAZY = no carga el autor hasta que alguien lo pida (rendimiento).
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn indica el nombre de la columna FK en la tabla 'books'.
    @JoinColumn(name = "author_id")
    private Author author;

    protected Book() {
    }

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
