package com.springroadmap.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el ApplicationContext de Spring arranca sin errores.
 *
 * ¿Que valida?
 *   - Todos los @Bean se instancian.
 *   - No hay dependencias circulares.
 *   - No falta ningun @Autowired.
 *
 * ANTES (Java 8 / Spring 4 clasico):
 *   @RunWith(SpringJUnit4ClassRunner.class)
 *   @ContextConfiguration(classes = MessagingApplication.class)
 *
 * AHORA (JUnit 5 + Spring Boot):
 *   Una anotacion: @SpringBootTest.
 */
@SpringBootTest
class MessagingApplicationTests {

    @Test
    void contextLoads() {
        // Cuerpo vacio a proposito.
        // Si el contexto no cargara, JUnit fallaria antes de llegar aqui.
    }
}
