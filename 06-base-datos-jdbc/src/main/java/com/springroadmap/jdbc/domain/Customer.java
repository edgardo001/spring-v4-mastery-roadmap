package com.springroadmap.jdbc.domain;

/**
 * Entidad de dominio Customer, modelada como un `record` de Java 21.
 *
 * Un `record` es una forma compacta de declarar una clase inmutable con
 * campos finales: el compilador genera automáticamente el constructor,
 * los getters (llamados como el campo, sin "get"), equals, hashCode y
 * toString. Es ideal para DTOs y objetos de valor.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 *   // ANTES: POJO manual con getters/setters/equals/hashCode (~40 líneas):
 *   public class Customer {
 *       private final Long id;
 *       private final String name;
 *       private final String email;
 *       public Customer(Long id, String name, String email) { ... }
 *       public Long getId()     { return id; }
 *       public String getName() { return name; }
 *       public String getEmail(){ return email; }
 *       // + equals, hashCode, toString...
 *   }
 *
 *   // AHORA: una línea.
 *   public record Customer(Long id, String name, String email) {}
 * </pre>
 *
 * PREGUNTA DE ALUMNO — "¿Puedo dejar el id en null al crear un Customer nuevo?"
 *   Sí. Al insertar un nuevo customer pasamos id=null y la BD lo genera.
 *   Al leer, el RowMapper llena el id con el valor real.
 */
public record Customer(Long id, String name, String email) {
}
