package com.springroadmap.di.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test de integración de AppConfig.
 *
 * A diferencia de los tests unitarios de Service/Repository, aquí SÍ
 * usamos @SpringBootTest porque estamos probando que Spring registra
 * correctamente el @Bean Clock declarado en AppConfig.
 *
 * @Autowired sobre el campo `clock` le dice a Spring:
 *   "Cuando crees la instancia de este test, busca en el contexto un bean
 *    de tipo Clock y asígnamelo aquí". Si no existe el bean, la carga
 *    del contexto (y por tanto el test) falla.
 */
@SpringBootTest
class AppConfigTest {

    @Autowired
    private Clock clock;

    @Test
    void clockBean_estaRegistradoYSeInyecta() {
        assertNotNull(clock);
        assertTrue(clock.instant().toEpochMilli() > 0);
    }
}
