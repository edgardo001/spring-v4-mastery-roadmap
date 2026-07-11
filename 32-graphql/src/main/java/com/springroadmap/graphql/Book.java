package com.springroadmap.graphql;

/**
 * Book - modelo de dominio inmutable representado como 'record'.
 *
 * Analogia: es la ficha catalografica de un libro en una biblioteca. Una vez
 * escrita, no se cambia; si quieres corregir un dato, imprimes otra ficha.
 *
 * PREGUNTA DE ALUMNO - "Que es un record?"
 *   Es una clase corta introducida en Java 14 (estable en 16) para representar
 *   datos inmutables. El compilador te genera automaticamente:
 *     - un constructor con todos los campos,
 *     - un getter por cada campo (id(), title(), author()),
 *     - equals(), hashCode() y toString().
 *   Todos los campos son 'final' de forma implicita.
 *
 * PREGUNTA DE ALUMNO - "Por que uso Long y no long?"
 *   Long (con L mayuscula) es el objeto envolvente ('wrapper') y admite 'null'.
 *   GraphQL puede recibir un id como texto y convertirlo. long (minuscula) es
 *   el primitivo y NO admite null.
 *
 * ANTES (Java 8) vs AHORA (Java 21)
 * ---------------------------------
 *   ANTES (Java 8) - una clase POJO tipica ocupaba ~35 lineas:
 *     public final class Book {
 *         private final Long id;
 *         private final String title;
 *         private final String author;
 *         public Book(Long id, String title, String author) {
 *             this.id = id;
 *             this.title = title;
 *             this.author = author;
 *         }
 *         public Long getId()       { return id; }
 *         public String getTitle()  { return title; }
 *         public String getAuthor() { return author; }
 *         // equals, hashCode, toString a mano...
 *     }
 *
 *   AHORA (Java 21) - una linea:
 *     public record Book(Long id, String title, String author) {}
 *
 *   Nota: los getters del record NO se llaman getId(), se llaman id().
 */
public record Book(Long id, String title, String author) {
}
