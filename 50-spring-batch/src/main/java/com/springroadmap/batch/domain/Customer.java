package com.springroadmap.batch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA Customer - fila destino que el Writer del batch va a persistir.
 *
 * Analogía: es la "etiqueta" que pegamos al sobre antes de guardarlo en la caja
 * de la bodega (tabla CUSTOMER).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - Antes: POJO con getters/setters manuales de 40 líneas.
 *   - Ahora: podríamos usar 'record' pero JPA aún exige clases mutables con
 *     constructor sin argumentos, así que mantenemos POJO clásico + setters.
 *
 * PREGUNTA DE ALUMNO — "¿por qué necesita un constructor vacío?"
 *   JPA/Hibernate crea instancias por reflexión al leer filas de la BD.
 *   Para eso llama primero al constructor sin argumentos, luego setea los campos.
 */
@Entity                           // Marca esta clase como tabla en la BD.
@Table(name = "CUSTOMER")         // Nombre físico de la tabla (opcional).
public class Customer {

    @Id                                                    // Clave primaria.
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // H2 autoincrementa.
    private Long id;

    private String name;
    private String email;

    /** Constructor sin argumentos REQUERIDO por JPA. */
    public Customer() {
    }

    /** Constructor de conveniencia para el ItemProcessor. */
    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // -------- Getters y setters (JPA los usa vía reflexión) --------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
