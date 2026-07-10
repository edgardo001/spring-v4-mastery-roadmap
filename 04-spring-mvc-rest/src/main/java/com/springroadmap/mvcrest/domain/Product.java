package com.springroadmap.mvcrest.domain;

import java.math.BigDecimal;

/**
 * Entidad de dominio: Producto.
 *
 * PROPÓSITO
 * ---------
 * Representa un producto simple con id, nombre y precio. En este módulo el
 * "almacén" es un Map en memoria (no hay base de datos todavía).
 *
 * ANALOGÍA
 * --------
 * Piensa en cada Product como la ETIQUETA de un producto en una estantería
 * de supermercado: un código de barras (id), un rótulo (name) y un precio.
 *
 * ANTES (Java 8) — POJO clásico
 * -----------------------------
 *   public class Product {
 *       private Long id;
 *       private String name;
 *       private BigDecimal price;
 *
 *       public Product() {}
 *       public Product(Long id, String name, BigDecimal price) {
 *           this.id = id; this.name = name; this.price = price;
 *       }
 *       public Long getId() { return id; }
 *       public void setId(Long id) { this.id = id; }
 *       public String getName() { return name; }
 *       public void setName(String name) { this.name = name; }
 *       public BigDecimal getPrice() { return price; }
 *       public void setPrice(BigDecimal price) { this.price = price; }
 *       // equals(), hashCode(), toString() a mano o con Lombok...
 *   }
 *
 * AHORA (Java 21) — record en 1 línea
 * -----------------------------------
 *   public record Product(Long id, String name, BigDecimal price) {}
 *
 * El compilador genera automáticamente:
 *   - Constructor canónico (Long, String, BigDecimal).
 *   - Accessors: id(), name(), price()  (¡ojo: NO getId(), sino id()!).
 *   - equals() basado en los 3 campos.
 *   - hashCode() basado en los 3 campos.
 *   - toString() legible: "Product[id=1, name=Cafe, price=2500]".
 * Además el record es INMUTABLE: no hay setters. Para "modificar" creas otro.
 *
 * PREGUNTA DE ALUMNO — "¿por qué BigDecimal y no double para el precio?"
 *   Porque double es binario y pierde precisión con decimales (0.1 + 0.2
 *   NO da 0.3 exacto). En dinero eso es inaceptable. BigDecimal usa
 *   representación decimal exacta.
 */
public record Product(Long id, String name, BigDecimal price) {
    // Cuerpo intencionalmente vacío: el compilador genera todo lo necesario.
}
