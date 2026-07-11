package com.springroadmap.graphql;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * BookRepository - almacen en memoria de libros.
 *
 * Analogia: es un cajon de fichas de biblioteca. Al arrancar la aplicacion,
 * el cajon ya trae 3 fichas precargadas. Se puede consultar todo el cajon,
 * buscar por id, o agregar una ficha nueva (que recibe el siguiente numero
 * correlativo automatico).
 *
 * PREGUNTA DE ALUMNO - "Por que @Repository y no @Component?"
 *   Ambas registran la clase como bean en el contexto de Spring, pero
 *   @Repository comunica INTENCION: "esta clase habla con la persistencia".
 *   Ademas, Spring convierte excepciones de base de datos a su jerarquia
 *   propia cuando corresponde. Aqui es memoria pura, pero mantenemos la
 *   convencion.
 *
 * PREGUNTA DE ALUMNO - "Que es AtomicLong y por que no un long normal?"
 *   Si dos hilos agregaran libros al mismo tiempo, un 'long++' podria
 *   producir dos ids iguales. AtomicLong.incrementAndGet() garantiza que
 *   cada llamada retorna un numero distinto, aunque haya concurrencia.
 *
 * ANTES (Java 8) vs AHORA (Java 21)
 * ---------------------------------
 *   ANTES:  List<Book> books = new ArrayList<Book>();  // repetir el tipo
 *           books.add(new Book(1L, "Clean Code", "Robert C. Martin"));
 *   AHORA:  private final List<Book> books = new ArrayList<>();  // 'diamond'
 *           books.add(new Book(1L, "Clean Code", "Robert C. Martin"));
 *
 *   ANTES (buscar por id):
 *     for (Book b : books) {
 *         if (b.getId().equals(id)) return b;
 *     }
 *     return null;
 *
 *   AHORA (streams + Optional):
 *     return books.stream()
 *                 .filter(b -> b.id().equals(id))
 *                 .findFirst();
 */
@Repository
public class BookRepository {

    // 'final' = la referencia a la lista no se puede reasignar (el contenido si cambia).
    private final List<Book> books = new ArrayList<>();

    // Generador thread-safe de ids. Empieza en 3 porque precargamos 3 libros con id 1,2,3.
    private final AtomicLong sequence = new AtomicLong(3);

    /**
     * Constructor: se ejecuta cuando Spring crea el bean al arrancar.
     * Aprovechamos para precargar 3 libros de ejemplo.
     */
    public BookRepository() {
        books.add(new Book(1L, "Clean Code", "Robert C. Martin"));
        books.add(new Book(2L, "Effective Java", "Joshua Bloch"));
        books.add(new Book(3L, "Domain-Driven Design", "Eric Evans"));
    }

    /** Devuelve una copia defensiva para que el caller no modifique el estado interno. */
    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    /**
     * Busca por id.
     * Retorna Optional<Book> en lugar de null: obliga al consumidor a
     * decidir explicitamente que hacer si no existe.
     */
    public Optional<Book> findById(Long id) {
        // stream() convierte la lista en un flujo perezoso.
        // filter(...) deja pasar solo elementos que cumplen la condicion.
        // 'b -> b.id().equals(id)' es una expresion lambda (funcion anonima).
        // findFirst() toma el primer match, envuelto en Optional.
        return books.stream()
                .filter(b -> b.id().equals(id))
                .findFirst();
    }

    /**
     * Crea un libro nuevo. El id se asigna automaticamente.
     */
    public Book save(String title, String author) {
        Book nuevo = new Book(sequence.incrementAndGet(), title, author);
        books.add(nuevo);
        return nuevo;
    }
}
