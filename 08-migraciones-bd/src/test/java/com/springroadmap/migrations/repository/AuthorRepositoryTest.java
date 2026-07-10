package com.springroadmap.migrations.repository;

import com.springroadmap.migrations.domain.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void findAllRetornaLosDosAutoresDelSeed() {
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).hasSize(2);
        assertThat(authors).extracting(Author::getName)
                .containsExactlyInAnyOrder("Gabriel Garcia Marquez", "Isabel Allende");
    }
}
