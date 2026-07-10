package com.springroadmap.migrations;

import com.springroadmap.migrations.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MigracionesBdApplicationTests {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void contextLoads() {
        // Si Flyway no ejecuto las migraciones, JPA validate falla y el contexto no carga.
        assertThat(authorRepository).isNotNull();
    }
}
