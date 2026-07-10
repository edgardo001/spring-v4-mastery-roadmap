package com.springroadmap.di.repository;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test UNITARIO puro del UserRepository (Map en memoria).
 *
 * Reforzamos el patrón Optional:
 *   - Optional.isPresent() cuando encontramos algo.
 *   - Optional.empty() cuando no existe (nunca devolvemos null).
 */
class UserRepositoryTest {

    @Test
    void findByEmail_conEmailExistente_devuelveNombre() {
        UserRepository repository = new UserRepository();

        Optional<String> result = repository.findByEmail("ada@example.com");

        assertTrue(result.isPresent());
        assertEquals("Ada Lovelace", result.get());
    }

    @Test
    void findByEmail_conEmailInexistente_devuelveOptionalVacio() {
        UserRepository repository = new UserRepository();

        Optional<String> result = repository.findByEmail("desconocido@example.com");

        assertFalse(result.isPresent());
    }
}
