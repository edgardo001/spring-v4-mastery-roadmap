package com.springroadmap.testadv.domain;

// Anotaciones JPA (Jakarta Persistence). Traducen esta clase Java a una tabla SQL.
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Book — representa una fila en la tabla {@code books}.
 *
 * Analogía del mundo real:
 *   Un libro en una biblioteca tiene: signatura (id), título y autor.
 *   Esta clase es la "ficha" del libro que la BD guarda por nosotros.
 *
 * Palabras clave explicadas:
 *   - {@code @Entity}: le dice a JPA "esta clase se persiste".
 *   - {@code @Table(name = "books")}: nombre de la tabla física. Sin esto, JPA usaría el nombre de la clase.
 *   - {@code @Id}: la propiedad que actúa como clave primaria (PK).
 *   - {@code @GeneratedValue(IDENTITY)}: la BD auto-genera el número (autoincrement en Postgres/H2).
 *   - {@code @Column(nullable = false)}: restricción NOT NULL en la columna.
 *   - {@code protected Book()}: constructor vacío requerido por JPA (usa reflexión para instanciar).
 *
 * Nota (según AGENTS.md): NO exponemos setters ni la Entity al @RequestBody. En esta versión
 * mínima el Controller manda un Book creado con el constructor público. Para un caso empresarial
 * se usaría un BookDto (ver módulo 09-mapeo-dtos-mapstruct).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES — POJO con getters/setters generados por IDE + hashCode/equals manuales.
 *   AHORA — Se podría usar un `record` como DTO, PERO no como @Entity (JPA exige constructor
 *   sin argumentos y campos mutables). Por eso mantenemos clase clásica para la Entity.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    /**
     * Constructor sin argumentos requerido por JPA/Hibernate.
     * Es {@code protected} para que fuera del paquete solo se pueda crear con datos válidos.
     */
    protected Book() {
        // vacío a propósito
    }

    /**
     * Constructor de negocio: obliga a que todo Book tenga título y autor.
     * El id lo asigna la BD, por eso no es parámetro.
     */
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // Getters — no exponemos setters para preservar el invariante "un libro tiene título y autor".
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
}
