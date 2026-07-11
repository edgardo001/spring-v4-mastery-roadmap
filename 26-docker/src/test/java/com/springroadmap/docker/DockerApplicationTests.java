package com.springroadmap.docker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * "Smoke test": verifica que el contexto de Spring arranca sin errores.
 *
 * @SpringBootTest levanta la aplicacion completa (mismo camino que el
 * metodo main) SIN abrir el puerto HTTP. Si algun bean estuviera mal
 * configurado o alguna dependencia del pom faltara, este test fallaria
 * al cargar el contexto.
 *
 * En el flujo de CI/CD (modulo 27) este test se ejecuta ANTES de
 * construir la imagen Docker: si el contexto no carga, no tiene
 * sentido gastar tiempo empaquetando la imagen.
 */
@SpringBootTest
class DockerApplicationTests {

    /**
     * Test intencionalmente vacio. Si Spring logra crear el
     * ApplicationContext sin lanzar excepcion, el test pasa.
     */
    @Test
    void contextLoads() {
        // Sin assert. Sin excepcion = exito.
    }
}
