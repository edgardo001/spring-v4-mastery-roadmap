package com.springroadmap.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test humo: verifica que el contexto de Spring arranca sin errores.
 *
 * Si esta prueba pasa, quiere decir que:
 *  - El schema.graphqls es sintacticamente valido.
 *  - Los @QueryMapping / @MutationMapping cubren todos los campos root del schema.
 *  - Todos los beans (repository, controller) se pueden crear.
 *
 * PREGUNTA DE ALUMNO - "Un test vacio sirve de algo?"
 *   Muchisimo. El 90 por ciento de los errores de configuracion (schemas rotos,
 *   dependencias circulares, beans mal declarados) revientan en el arranque del
 *   ApplicationContext. Este 'contextLoads' los detecta sin escribir logica.
 */
@SpringBootTest
class GraphqlApplicationTests {

    @Test
    void contextLoads() {
        // Cuerpo vacio a proposito: el simple hecho de que @SpringBootTest
        // arranque el contexto ya es la assertion.
    }
}
