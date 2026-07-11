package com.springroadmap.jpaadv.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Entidad JPA que representa la tabla `products`.
 *
 * Analogía: cada `Product` es un ARTÍCULO en el catálogo (ej: "Laptop", $999).
 * Vive en UN estante (Category). Muchos productos por estante → @ManyToOne.
 *
 * Palabras clave:
 * - `@ManyToOne(fetch = LAZY)`: relación "muchos a uno". LAZY = no cargar la
 *   Category hasta que alguien llame `getCategory()` explícitamente. Evita
 *   traer datos que quizá no se usan. RIESGO: si accedes a la category
 *   FUERA de una transacción abierta, obtienes `LazyInitializationException`.
 *   Solución: `@EntityGraph` (ver ProductRepository.findWithCategoryById).
 * - `@JoinColumn(name = "category_id")`: nombre exacto de la columna FK.
 * - `BigDecimal`: tipo Java para dinero. NUNCA usar `double`/`float` para
 *   precios (errores de coma flotante: 0.1 + 0.2 != 0.3).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 *   // ANTES: `private double price;` — bug silencioso al sumar centavos.
 *   // AHORA: `private BigDecimal price;` — precisión exacta para dinero.
 * </pre>
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    // precision/scale: hasta 10 dígitos totales, 2 decimales. Ej: 12345678.99.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Relación N a 1 con Category. LAZY = perezosa: la category no se carga
     * automáticamente al hacer `SELECT * FROM products`. Hibernate emite un
     * SELECT extra solo cuando accedes a `product.getCategory().getName()`.
     *
     * PROBLEMA CLÁSICO N+1: si listas 100 productos y accedes a la category
     * de cada uno, Hibernate lanza 1 (products) + 100 (categories) = 101
     * queries. Solución en este módulo: `@EntityGraph(attributePaths="category")`
     * que fuerza un JOIN en la misma query.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Constructor sin argumentos requerido por Hibernate.
     */
    protected Product() {
        // Hibernate reflection.
    }

    public Product(String name, BigDecimal price, Category category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public Category getCategory() { return category; }
}
