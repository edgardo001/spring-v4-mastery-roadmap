package com.springroadmap.aop.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.springroadmap.aop.service.CalculatorService;

/**
 * LoggingAspectTest — Verifica que el aspecto SÍ intercepta métodos @Loggable y NO
 * intercepta los que no lo llevan.
 *
 * <p>Requiere {@code @SpringBootTest} (no {@code standaloneSetup}) porque el
 * auto-proxy de Spring AOP sólo se activa cuando hay un contexto Spring completo.
 */
@SpringBootTest
class LoggingAspectTest {

    @Autowired
    private CalculatorService service;

    @Autowired
    private LoggingAspect loggingAspect;

    @BeforeEach
    void resetCounter() {
        // Aisla el estado entre tests para evitar interferencias.
        loggingAspect.reset();
    }

    @Test
    void addIsIntercepted_incrementsCounterOncePerCall() {
        // Act: 3 llamadas a add() (método @Loggable).
        int r1 = service.add(2, 3);
        int r2 = service.add(10, 20);
        int r3 = service.add(-1, 1);

        // Assert: los resultados deben ser correctos y el aspecto debe haber
        // registrado exactamente 3 invocaciones.
        assertThat(r1).isEqualTo(5);
        assertThat(r2).isEqualTo(30);
        assertThat(r3).isEqualTo(0);
        assertThat(loggingAspect.callCount()).isEqualTo(3);
    }

    @Test
    void subIsNotIntercepted_counterStaysAtThree() {
        // Arrange: 3 llamadas a add() para dejar el contador en 3.
        service.add(1, 1);
        service.add(1, 1);
        service.add(1, 1);
        assertThat(loggingAspect.callCount()).isEqualTo(3);

        // Act: llamar a sub() (NO lleva @Loggable) no debe mover el contador.
        int r = service.sub(10, 4);

        // Assert
        assertThat(r).isEqualTo(6);
        assertThat(loggingAspect.callCount())
                .as("sub() no está anotado con @Loggable, el aspecto no debe dispararse")
                .isEqualTo(3);
    }
}
