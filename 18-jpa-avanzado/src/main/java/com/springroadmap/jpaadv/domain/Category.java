package com.springroadmap.jpaadv.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa la tabla `categories`.
 *
 * Analogía: la CATEGORÍA es el "estante" (Electrónica, Libros, Ropa). Un
 * producto vive en UN estante — relación N a 1: muchos productos, una
 * categoría.
 *
 * Palabras clave:
 * - `@Entity`: le dice a Hibernate "esta clase se mapea a una tabla".
 * - `@Table(name = "categories")`: nombre exacto de la tabla (por defecto
 *   usaría el nombre de la clase).
 * - `@Id`: marca la clave primaria.
 * - `@GeneratedValue(IDENTITY)`: delega el autoincremento a la BD.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 *   // ANTES: XML de mapeo Hibernate (hbm.xml) por cada entidad.
 *   // AHORA: anotaciones JPA directamente en la clase. Menos ceremonia.
 * </pre>
 *
 * Nota: NO es un `record` porque JPA exige constructor sin argumentos y los
 * records son inmutables sin no-args público — incompatible con Hibernate.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Constructor sin argumentos requerido por Hibernate (usa reflection).
     * `protected` para desalentar su uso desde código de negocio.
     */
    protected Category() {
        // Hibernate lo llama y luego rellena campos por reflection.
    }

    public Category(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
