package com.springroadmap.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa la tabla `books` en la base de datos.
 *
 * Analogía: cada instancia de `Book` es una FICHA de biblioteca. La clase es
 * el formato del formulario (título, autor, año); cada fila en la tabla
 * `books` es una ficha impresa con datos concretos. Hibernate es el
 * bibliotecario que transporta las fichas entre tu memoria (Java) y el
 * archivador físico (la base de datos).
 *
 * Notas didácticas:
 * - Es una **clase**, no un `record`. JPA/Hibernate requiere un constructor
 *   sin argumentos (visible como mínimo a nivel `protected`) para poder
 *   crear el objeto vacío por *reflection* y luego rellenar los campos.
 *   Los `record` son inmutables y no exponen constructor vacío, por eso no
 *   sirven como `@Entity` con generación de ID automática.
 * - No exponemos setters: practicamos "inmutabilidad relativa". Para
 *   modificar un libro se crea uno nuevo, o se agregan métodos de negocio
 *   controlados (no en este ejemplo mínimo).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 *   // ANTES: POJO con getters/setters + constructor vacío + all-args.
 *   //   Muchas líneas de boilerplate.
 *   // AHORA (JPA): seguimos necesitando clase con no-args por Hibernate,
 *   //   pero podemos omitir setters y quedarnos con getters + constructor
 *   //   público con todos los campos. Java 21 no cambia la obligación de
 *   //   JPA; el `record` NO aplica aquí por lo dicho arriba.
 * </pre>
 */
@Entity
@Table(name = "books")
public class Book {

    /**
     * Clave primaria. `@GeneratedValue(strategy = IDENTITY)` delega la
     * generación a la columna AUTO_INCREMENT de la base de datos (H2 lo
     * soporta igual que MySQL/PostgreSQL).
     *
     * Usamos `Long` (wrapper) y NO `long` primitivo: un libro nuevo aún no
     * guardado tendrá `id == null`, señal inequívoca para Hibernate de que
     * hay que hacer INSERT. Un `long` primitivo empezaría en 0 y confundiría.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String author;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    /**
     * Constructor sin argumentos requerido por JPA. `protected` en lugar de
     * `public` para desalentar su uso fuera del framework: los desarrolladores
     * deben usar el constructor con argumentos, mientras que Hibernate (que
     * usa reflection) sí puede acceder a este.
     */
    protected Book() {
        // No-op: Hibernate lo invoca y luego rellena campos por reflection.
    }

    /**
     * Constructor de negocio. No incluye `id` porque lo genera la BD.
     */
    public Book(String title, String author, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    // Solo getters — no setters — para forzar disciplina de inmutabilidad.
    // PREGUNTA DE ALUMNO — "¿cómo actualizo un libro sin setters?"
    //   En este módulo introductorio, se elimina el registro y se crea uno
    //   nuevo, o se agregan métodos de negocio controlados. Para
    //   actualizaciones en el controlador usaremos un nuevo `Book` con el
    //   mismo `id` (asignado vía reflection en el servicio) o un método
    //   `updateFrom(Book)` — en este ejemplo el controller construye un
    //   Book nuevo y lo persiste con `save`.
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Integer getPublicationYear() { return publicationYear; }

    /**
     * Setter interno EXCLUSIVAMENTE para el update del controller. Es
     * `package-private` (sin modificador) para que no lo llame nadie fuera
     * del paquete `domain`... pero sí lo puede llamar el mismo paquete si
     * lo movemos. Alternativa: método `withId(Long)` que retorna nueva
     * instancia. Para simplicidad del ejemplo, exponemos este setter mínimo.
     */
    void assignId(Long id) {
        this.id = id;
    }
}
