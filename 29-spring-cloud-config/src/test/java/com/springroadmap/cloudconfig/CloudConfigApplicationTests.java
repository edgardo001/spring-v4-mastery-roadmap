package com.springroadmap.cloudconfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test — verifica que el ApplicationContext arranca sin fallos.
 *
 * <p>Analogia: es como girar la llave del auto y comprobar que enciende;
 * no probamos que vaya rapido, solo que <i>arranca</i>.
 *
 * <p>PREGUNTA DE ALUMNO — "¿por que no hay assertions?"
 *   Si el contexto no puede levantar (falta un bean, mal-configurado,
 *   YAML invalido, etc.), {@code @SpringBootTest} falla al inicializar
 *   y el test se marca como rojo. No necesitamos {@code assertTrue}.
 */
// @SpringBootTest carga el ApplicationContext completo (todos los beans).
@SpringBootTest
class CloudConfigApplicationTests {

    // @Test le dice a JUnit 5 que este metodo es un caso de prueba.
    @Test
    void contextLoads() {
        // Cuerpo vacio intencionalmente: la validacion es que llegue hasta aqui.
    }
}
