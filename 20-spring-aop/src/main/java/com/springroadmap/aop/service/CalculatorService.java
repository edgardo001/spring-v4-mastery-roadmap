package com.springroadmap.aop.service;

import org.springframework.stereotype.Service;

import com.springroadmap.aop.annotation.Loggable;

/**
 * CalculatorService — Lógica de negocio "pura" del ejemplo.
 *
 * <p><b>Analogía:</b> es un chef en la cocina. El chef sólo cocina (suma o resta).
 * No lleva la caja registradora (contador) ni el cronómetro. Esos aparatos los tiene
 * el gerente (LoggingAspect) al otro lado de la puerta.
 *
 * <p>El método {@link #add(int, int)} está marcado con {@code @Loggable}: el aspecto
 * lo interceptará. El método {@link #sub(int, int)} NO lo está: pasa "invisible" para
 * el aspecto. Esto demuestra el <b>control granular</b> que da la anotación custom.
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b>
 * <pre>
 *   // ANTES: la clase seguía exactamente igual. AOP existía desde Spring 2.x.
 *   // AHORA: nada nuevo en la sintaxis para AOP; sí puedes usar "var" en el caller.
 * </pre>
 */
// PREGUNTA DE ALUMNO — "¿por qué @Service y no @Component?"
//   @Service es una especialización semántica de @Component. Marca la clase como
//   'capa de servicio' (lógica de negocio). Funcionalmente hoy son iguales para
//   Spring, pero comunican intención a otros desarrolladores.
@Service
public class CalculatorService {

    /**
     * Suma dos enteros. Interceptado por LoggingAspect.
     * @param a primer sumando.
     * @param b segundo sumando.
     * @return a + b.
     */
    @Loggable
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * Resta dos enteros. NO interceptado (no lleva @Loggable), para demostrar el
     * filtrado del pointcut.
     */
    public int sub(int a, int b) {
        return a - b;
    }
}
