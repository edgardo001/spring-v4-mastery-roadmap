package com.springroadmap.dtos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: verifica que el contexto de Spring arranca correctamente.
 * Si MapStruct no genera EmployeeMapperImpl, este test falla porque
 * EmployeeController no puede resolver su dependencia.
 */
@SpringBootTest
class MapeoDtosMapstructApplicationTests {

    @Test
    void contextLoads() {
        // Sin body: si el contexto no arranca, JUnit falla el test.
    }
}
