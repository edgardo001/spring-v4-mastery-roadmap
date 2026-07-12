package com.springroadmap.shell.command;

import org.springframework.stereotype.Component;

/**
 * "Comandos" matematicos.
 *
 * En la variante oficial de Spring Shell esta clase estaria anotada con
 * {@code @ShellComponent} y cada metodo con {@code @ShellMethod(key = "...")}.
 * Aqui, en la variante fallback compatible con Spring Boot 4.1.0, es un
 * simple {@code @Component} y el {@code ShellRunner} despacha manualmente.
 */
@Component
public class MathCommands {

    /** Suma dos enteros: {@code add 2 3 -> 5}. */
    public int add(int a, int b) {
        return a + b;
    }

    /** Multiplica dos enteros: {@code mul 2 3 -> 6}. */
    public int mul(int a, int b) {
        return a * b;
    }
}
