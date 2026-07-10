package com.springroadmap.migrations.web;

import com.springroadmap.migrations.domain.Author;
import com.springroadmap.migrations.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorControllerTest {

    private AuthorRepository authorRepository;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authorRepository = mock(AuthorRepository.class);
        AuthorController controller = new AuthorController(authorRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllRetornaListaDeAutores() throws Exception {
        Author a1 = new Author("Gabriel Garcia Marquez");
        Author a2 = new Author("Isabel Allende");
        when(authorRepository.findAll()).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Gabriel Garcia Marquez"));
    }

    @Test
    void getByIdRetornaAutorCuandoExiste() throws Exception {
        Author a = new Author("Isabel Allende");
        when(authorRepository.findById(eq(2L))).thenReturn(Optional.of(a));

        mockMvc.perform(get("/api/authors/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Isabel Allende"));
    }

    @Test
    void getByIdRetorna404CuandoNoExiste() throws Exception {
        when(authorRepository.findById(eq(99L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/authors/99"))
                .andExpect(status().isNotFound());
    }
}
