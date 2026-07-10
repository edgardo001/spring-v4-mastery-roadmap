package com.springroadmap.jpa.repository;

import com.springroadmap.jpa.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de repositorio con `@SpringBootTest`.
 *
 * NOTA (MEMORY.md): En Spring Boot 4.1.0 se eliminó `@DataJpaTest` de
 * `spring-boot-test-autoconfigure`. Usamos `@SpringBootTest` (contexto
 * completo) más `@Transactional` para hacer rollback tras cada test.
 * Es más pesado pero portable entre 3.x y 4.x.
 */
@SpringBootTest
@Transactional
class BookRepositoryTest {

    private final BookRepository repository;

    @Autowired
    BookRepositoryTest(BookRepository repository) {
        this.repository = repository;
    }

    @Test
    void saveAssignsIdAndPersistsFields() {
        Book saved = repository.save(new Book("Clean Code", "Robert C. Martin", 2008));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Clean Code");
        assertThat(saved.getAuthor()).isEqualTo("Robert C. Martin");
        assertThat(saved.getPublicationYear()).isEqualTo(2008);
    }

    @Test
    void findByIdReturnsPersistedBook() {
        Book saved = repository.save(new Book("Effective Java", "Joshua Bloch", 2018));
        Optional<Book> found = repository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getAuthor()).isEqualTo("Joshua Bloch");
    }

    @Test
    void findAllReturnsAllBooks() {
        repository.save(new Book("A", "Author A", 2000));
        repository.save(new Book("B", "Author B", 2001));
        List<Book> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void countReflectsNumberOfSaves() {
        long before = repository.count();
        repository.save(new Book("Domain-Driven Design", "Eric Evans", 2003));
        long after = repository.count();
        assertThat(after).isEqualTo(before + 1);
    }
}
