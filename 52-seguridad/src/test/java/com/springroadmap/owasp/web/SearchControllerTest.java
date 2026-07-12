package com.springroadmap.owasp.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springroadmap.owasp.domain.User;
import com.springroadmap.owasp.domain.UserRepository;

class SearchControllerTest {

    private MockMvc mockMvc;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        SearchController controller = new SearchController(userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void searchNormal() throws Exception {
        when(userRepository.findByEmail("ana@test.com"))
            .thenReturn(List.of(new User("ana@test.com", "Ana")));

        mockMvc.perform(get("/api/users/search").param("email", "ana@test.com"))
            .andExpect(status().isOk());
    }

    @Test
    void searchSqlInjectionAttemptIsSafe() throws Exception {
        // Payload clásico de SQL injection. Como usamos query parametrizada,
        // se pasa como valor literal al bind param → NO se ejecuta como SQL.
        String payload = "' OR '1'='1";
        when(userRepository.findByEmail(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/api/users/search").param("email", payload))
            .andExpect(status().isOk());

        // El valor llegó tal cual al repositorio, no se interpretó como SQL.
        verify(userRepository).findByEmail(payload);
    }
}
