package com.springroadmap.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de humo: valida que el contexto de Spring carga sin fallar.
 * Si un bean está mal configurado, este test explota primero.
 */
@SpringBootTest
class JpaHibernateApplicationTests {

    @Test
    void contextLoads() {
        // El único assert implícito: si el contexto no arranca, el test falla.
    }
}
