package com.springroadmap.files;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test humo: verifica que el ApplicationContext de Spring arranque sin errores.
 * Si algún bean falla al construirse (por ejemplo, un @Value mal escrito),
 * este test falla ANTES de que despleguemos.
 *
 * <p>{@code @SpringBootTest} sin parámetros carga el contexto completo
 * (sin levantar Tomcat de verdad — usa un webEnvironment MOCK por defecto).</p>
 */
@SpringBootTest
class FilesApplicationTests {

    @Test
    void contextLoads() {
        // Cuerpo vacío a propósito: basta con que @SpringBootTest cargue el contexto.
    }
}
