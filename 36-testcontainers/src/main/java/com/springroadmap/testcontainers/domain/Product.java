package com.springroadmap.testcontainers.domain;

// JPA — Java Persistence API. Estas anotaciones dicen a Hibernate cómo mapear
// la clase Product a una tabla SQL.
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad Product — representa un producto en la base de datos.
 *
 * <p>Analogía del mundo real: es la "ficha" en el catálogo de una tienda.
 * Cada ficha tiene un identificador único (id), un nombre y un precio.</p>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por qué BigDecimal y no double para el precio?"
 *   Porque <code>double</code> tiene errores de coma flotante (ej: 0.1 + 0.2 = 0.30000000000000004).
 *   En dinero eso es INACEPTABLE. <code>BigDecimal</code> es exacto.</p>
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * Identificador único auto-generado por la BD (SERIAL en Postgres, IDENTITY en H2).
     * <code>@GeneratedValue(strategy = IDENTITY)</code>: delega la generación a la BD.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    /**
     * Constructor sin argumentos REQUERIDO por JPA (Hibernate lo usa por reflexión).
     * <code>protected</code> — se puede ampliar a public, pero protected desincentiva
     * su uso desde código de negocio.
     */
    protected Product() {
    }

    /**
     * Constructor de negocio: fuerza a que un Product siempre nazca con nombre y precio.
     */
    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    // Getters y setters clásicos. NO usamos Lombok (regla del roadmap).

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;   // pattern matching Java 21
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

/*
 * ============================================================================
 * ANTES (Java 8) vs AHORA (Java 21)
 * ============================================================================
 * ANTES (Java 8): casting explícito en equals.
 *   if (!(o instanceof Product)) return false;
 *   Product other = (Product) o;
 *   return Objects.equals(id, other.id);
 *
 * AHORA (Java 21): pattern matching evita el cast.
 *   if (!(o instanceof Product other)) return false;
 *   return Objects.equals(id, other.id);
 *
 * ANTES (Java 8): @Entity requería import javax.persistence.
 * AHORA (Jakarta EE 9+): import jakarta.persistence.
 * ============================================================================
 */
