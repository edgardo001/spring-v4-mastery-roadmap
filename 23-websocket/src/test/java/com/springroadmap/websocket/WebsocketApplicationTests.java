package com.springroadmap.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de humo: verifica que el ApplicationContext arranca sin excepciones.
 * Si el WebSocketConfig tuviera un error (destino mal escrito, bean faltante),
 * este test falla al levantar el contexto.
 */
@SpringBootTest
class WebsocketApplicationTests {

    @Test
    void contextLoads() {
        // Sin aserciones: si el contexto no carga, JUnit reporta el error automáticamente.
    }
}
