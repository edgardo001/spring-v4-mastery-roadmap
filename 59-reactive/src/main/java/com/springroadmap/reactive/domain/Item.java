package com.springroadmap.reactive.domain;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entidad mapeada a la tabla items via Spring Data R2DBC.
 *
 * Se usa un POJO clasico (no record) para permitir que R2DBC popule
 * el id auto-generado tras el save() sin necesidad de builders adicionales.
 */
@Table("items")
public class Item {

    @Id
    private Long id;
    private String name;
    private BigDecimal price;

    public Item() {
    }

    public Item(final Long id, final String name, final BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }
}
