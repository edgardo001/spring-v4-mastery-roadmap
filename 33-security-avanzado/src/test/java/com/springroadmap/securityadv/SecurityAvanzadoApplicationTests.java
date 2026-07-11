package com.springroadmap.securityadv;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de arranque del contexto Spring.
 *
 * <p>Si el contexto no carga (por bean mal configurado, dependencia
 * faltante, etc.), este test falla y avisa antes de correr el resto.</p>
 */
@SpringBootTest
class SecurityAvanzadoApplicationTests {

    @Test
    void contextLoads() {
        // Vacio a proposito: basta con que Spring levante el ApplicationContext.
    }
}
