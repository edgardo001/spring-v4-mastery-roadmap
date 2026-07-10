package com.springroadmap.jpa.controller;

import com.springroadmap.jpa.domain.Book;
import com.springroadmap.jpa.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración del controller con MockMvc en modo standalone.
 *
 * Standalone significa que no levantamos toda la infraestructura web de
 * Spring — construimos MockMvc directamente sobre la instancia del
 * controller. Es rápido y aísla el test.
 *
 * Usamos `@SpringBootTest` para que el `BookRepository` real (con H2)
 * esté disponible y se inyecte en el controller.
 */
@SpringBootTest
class BookControllerTest {

    private final BookRepository repository;
    private MockMvc mockMvc;

    /**
     * Helper que arma un JSON literal para no depender de ObjectMapper.
     * En Spring Boot 4 la reorganización de módulos hizo que jackson-databind
     * no siempre viaje transitivamente al classpath de test — construir el
     * JSON a mano es más portable y didáctico.
     */
    private static String bookJson(String title, String author, int year) {
        return "{\"title\":\"" + title + "\",\"author\":\"" + author + "\",\"publicationYear\":" + year + "}";
    }

    @Autowired
    BookControllerTest(BookRepository repository) {
        this.repository = repository;
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        BookController controller = new BookController(repository);
        // En modo standalone hay que registrar manualmente el resolver
        // de Pageable, si no MockMvc no sabe cómo construir el argumento.
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void postCreatesBook() throws Exception {
        Book payload = new Book("The Pragmatic Programmer", "Hunt & Thomas", 1999);
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson(payload.getTitle(), payload.getAuthor(), payload.getPublicationYear())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title").value("The Pragmatic Programmer"));
    }

    @Test
    void getByIdReturnsBook() throws Exception {
        Book saved = repository.save(new Book("Refactoring", "Martin Fowler", 1999));
        mockMvc.perform(get("/api/books/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Martin Fowler"));
    }

    @Test
    void getByMissingIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/books/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsPagedResult() throws Exception {
        repository.save(new Book("A", "AA", 2000));
        repository.save(new Book("B", "BB", 2001));
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void putUpdatesBook() throws Exception {
        Book saved = repository.save(new Book("Old Title", "Old Author", 1990));
        Book payload = new Book("New Title", "New Author", 2020);
        mockMvc.perform(put("/api/books/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson(payload.getTitle(), payload.getAuthor(), payload.getPublicationYear())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    void deleteRemovesBook() throws Exception {
        Book saved = repository.save(new Book("Doomed", "Anonymous", 1970));
        mockMvc.perform(delete("/api/books/" + saved.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/books/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}
