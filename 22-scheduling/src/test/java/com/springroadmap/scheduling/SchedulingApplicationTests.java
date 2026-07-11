package com.springroadmap.scheduling;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de humo: verifica que el contexto Spring arranque sin errores.
 *
 * Si algún bean está mal configurado, @EnableScheduling falla al aplicar,
 * o el classpath está roto, este test se cae. Es la primera línea de
 * defensa antes de tests más específicos.
 *
 * PREGUNTA DE ALUMNO — "¿Este test también dispara las tareas @Scheduled?"
 *   Sí. Al arrancar el contexto se activa el scheduler y las tareas
 *   empiezan a correr en background. Como el test termina rápidamente,
 *   probablemente solo se ejecuten 0 o 1 veces. Para probarlas en serio
 *   usamos HeartbeatServiceTest.
 */
@SpringBootTest
class SchedulingApplicationTests {

    @Test
    void contextLoads() {
        // Sin aserciones: si el contexto no carga, JUnit reporta el fallo.
    }
}
