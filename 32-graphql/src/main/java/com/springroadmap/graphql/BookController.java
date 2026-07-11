package com.springroadmap.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * BookController - resolvers GraphQL para el tipo Book.
 *
 * Analogia: es el mozo de un restaurante donde el cliente pide EXACTAMENTE
 * lo que quiere del menu (query GraphQL). El mozo (controller) traduce el
 * pedido a llamadas concretas al chef (BookRepository) y arma el plato.
 *
 * Relacion con el schema (src/main/resources/graphql/schema.graphqls):
 *   type Query {
 *     books: [Book!]!            -> resolvido por books()
 *     bookById(id: ID!): Book    -> resolvido por bookById(id)
 *   }
 *   type Mutation {
 *     addBook(title, author): Book!   -> resolvido por addBook(title, author)
 *   }
 *
 * PREGUNTA DE ALUMNO - "Por que @Controller y no @RestController?"
 *   Con GraphQL NO devolvemos JSON directamente desde metodos anotados con
 *   @GetMapping/@PostMapping. Es la infraestructura de Spring GraphQL la que
 *   recibe el request en /graphql, invoca a los metodos @QueryMapping /
 *   @MutationMapping y arma la respuesta segun el schema. @Controller basta.
 *
 * PREGUNTA DE ALUMNO - "Por que Constructor Injection en vez de @Autowired en el campo?"
 *   - Permite marcar el campo como 'final' (inmutable).
 *   - No requiere reflexion para inyectar en tests.
 *   - Deja explicito que el controller NECESITA el repositorio para funcionar.
 *
 * PREGUNTA DE ALUMNO - "El id llega como String y no como Long?"
 *   Si. En GraphQL 'ID' es un escalar textual. Lo convertimos con
 *   Long.parseLong(id). Si el cliente manda "abc" lanzara NumberFormatException.
 *
 * ANTES (REST tradicional) vs AHORA (GraphQL)
 * -------------------------------------------
 *   ANTES - varias rutas REST y payloads fijos:
 *     GET  /api/books            -> devuelve TODOS los campos siempre (over-fetching)
 *     GET  /api/books/{id}
 *     POST /api/books   { title, author }
 *
 *   AHORA - un unico endpoint POST /graphql; el cliente elige campos:
 *     query { books { title } }                    // solo titulos
 *     query { bookById(id: "1") { id title } }
 *     mutation { addBook(title:"X", author:"Y") { id } }
 */
@Controller
public class BookController {

    // Dependencia inmutable inyectada por constructor.
    private final BookRepository repository;

    /**
     * Constructor injection. Spring detecta este constructor y pasa el bean
     * BookRepository automaticamente.
     */
    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    /**
     * Resolver de la query 'books'.
     * @QueryMapping usa el nombre del metodo ("books") como nombre del campo
     * en el tipo Query del schema.
     */
    @QueryMapping
    public List<Book> books() {
        return repository.findAll();
    }

    /**
     * Resolver de 'bookById(id: ID!)'.
     * @Argument enlaza el parametro del metodo con el argumento del schema.
     * Retorna 'Book' o null; GraphQL lo mapea a null si el Optional viene vacio.
     */
    @QueryMapping
    public Book bookById(@Argument String id) {
        // orElse(null) es aceptable aqui porque el schema declara Book (nullable),
        // no Book! (non-null). Si estuviera marcado non-null, deberiamos lanzar
        // una excepcion cuando no exista.
        return repository.findById(Long.parseLong(id)).orElse(null);
    }

    /**
     * Resolver de 'addBook(title, author): Book!'.
     * El schema declara el retorno como Book! (obligatorio), asi que save()
     * DEBE devolver siempre un Book.
     */
    @MutationMapping
    public Book addBook(@Argument String title, @Argument String author) {
        return repository.save(title, author);
    }
}
