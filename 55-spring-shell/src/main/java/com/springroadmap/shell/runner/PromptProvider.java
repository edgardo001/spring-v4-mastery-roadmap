package com.springroadmap.shell.runner;

import org.springframework.stereotype.Component;

/**
 * Equivalente al {@code PromptProvider} de Spring Shell.
 *
 * En la variante oficial devolveria un {@code AttributedString} con colores
 * ANSI. Aqui devolvemos un String plano para evitar dependencias de JLine.
 */
@Component
public class PromptProvider {

    public String getPrompt() {
        return "roadmap> ";
    }
}
