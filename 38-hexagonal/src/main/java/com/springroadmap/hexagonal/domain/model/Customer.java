package com.springroadmap.hexagonal.domain.model;

/**
 * Modelo de dominio 'Customer' (Cliente).
 *
 * REGLA HEXAGONAL: este archivo NO tiene un solo 'import' de Spring, JPA, Jackson,
 * ni de ninguna infraestructura. Es Java 100% puro. Podrías copiarlo tal cual a
 * un proyecto sin frameworks y seguiría funcionando.
 *
 * Analogía del mundo real: es la "ficha de papel" del cliente en el banco.
 * No sabe si vive en un archivador (BD relacional), en la nube (MongoDB) o en
 * un cajón (memoria). Solo describe QUÉ ES un cliente.
 *
 * ¿Qué es un 'record'?
 *   - Introducido en Java 16. Es una clase INMUTABLE, compacta, para transportar datos.
 *   - Genera automáticamente: constructor, getters (id(), name(), email()), equals, hashCode y toString.
 *   - No puedes agregarle setters: sus campos son 'final' por diseño.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES (clase POJO clásica ~30 líneas):
 *     public class Customer {
 *         private final Long id;
 *         private final String name;
 *         private final String email;
 *         public Customer(Long id, String name, String email) {
 *             this.id = id; this.name = name; this.email = email;
 *         }
 *         public Long getId() { return id; }
 *         public String getName() { return name; }
 *         public String getEmail() { return email; }
 *         // equals, hashCode y toString a mano...
 *     }
 *   AHORA (Java 21, 1 línea):
 *     public record Customer(Long id, String name, String email) { }
 *
 * PREGUNTA DE ALUMNO — "¿Por qué el modelo no tiene @Entity?"
 *   Porque @Entity es de JPA (infraestructura). Si lo pongo, mi dominio "sabe" que
 *   existe una BD relacional. Eso rompe la arquitectura hexagonal: el corazón
 *   dependería del enchufe. En su lugar, en el adaptador de persistencia crearíamos
 *   una 'CustomerJpaEntity' aparte y mapearíamos de/hacia este record.
 */
public record Customer(Long id, String name, String email) {
    // El cuerpo del record puede quedar vacío. Si quisiéramos validar en el
    // constructor, agregaríamos un "constructor compacto":
    //   public Customer {
    //       if (name == null || name.isBlank()) throw new IllegalArgumentException("name requerido");
    //   }
}
