package com.springroadmap.hexagonal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de carga del contexto. Valida que todos los beans del hexágono se cablean:
 *   - HexagonalApplication (SpringBootApplication).
 *   - CustomerService (@Service, implementa RegisterCustomerUseCase).
 *   - InMemoryCustomerRepository (@Component, implementa CustomerRepositoryPort).
 *   - CustomerController (@RestController, depende de RegisterCustomerUseCase).
 *
 * Si Spring resuelve todas las dependencias sin lanzar NoSuchBeanDefinitionException,
 * la arquitectura hexagonal está correctamente conectada.
 */
@SpringBootTest
class HexagonalApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto arrancó sin excepciones, el test pasa.
    }
}
