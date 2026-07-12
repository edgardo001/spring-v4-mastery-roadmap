package com.springroadmap.shell.runner;

import com.springroadmap.shell.command.MathCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * REPL casero que simula el ciclo de Spring Shell:
 * <ol>
 *   <li>Imprime un prompt.</li>
 *   <li>Lee una linea de stdin.</li>
 *   <li>Parsea el primer token como nombre de comando y el resto como argumentos.</li>
 *   <li>Despacha al bean correspondiente.</li>
 *   <li>Repite hasta {@code exit}.</li>
 * </ol>
 *
 * <p>Se salta el bucle interactivo si:</p>
 * <ul>
 *   <li>Se pasa {@code --no-shell} como argumento (usado por tests).</li>
 *   <li>{@code System.console()} es {@code null} y no hay entrada disponible
 *       (ej: ejecucion en CI sin stdin).</li>
 * </ul>
 */
@Component
public class ShellRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ShellRunner.class);

    private final MathCommands math;
    private final PromptProvider promptProvider;

    public ShellRunner(MathCommands math, PromptProvider promptProvider) {
        this.math = math;
        this.promptProvider = promptProvider;
    }

    @Override
    public void run(String... args) throws Exception {
        for (String a : args) {
            if ("--no-shell".equals(a)) {
                log.info("ShellRunner deshabilitado por --no-shell");
                return;
            }
        }
        // Si no hay stdin (por ejemplo, corriendo en test/CI), no entrar al REPL.
        if (System.in.available() <= 0 && System.console() == null) {
            log.info("Sin stdin disponible: ShellRunner no entra al REPL");
            return;
        }
        loop(new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)),
             System.out);
    }

    /**
     * Bucle REPL desacoplado de stdin/stdout para poder testearlo.
     *
     * @return codigo devuelto por el ultimo comando (0 si fue {@code exit}).
     */
    public int loop(BufferedReader in, java.io.PrintStream out) throws IOException {
        out.println("Spring Shell (variante REPL) - escribe 'help' o 'exit'");
        String line;
        while (true) {
            out.print(promptProvider.getPrompt());
            out.flush();
            line = in.readLine();
            if (line == null) {
                return 0;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] tokens = line.split("\\s+");
            String cmd = tokens[0];
            try {
                switch (cmd) {
                    case "exit", "quit" -> {
                        out.println("bye");
                        return 0;
                    }
                    case "help" -> out.println("Comandos: add <a> <b> | mul <a> <b> | help | exit");
                    case "add" -> {
                        requireArgs(tokens, 2);
                        out.println(math.add(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
                    }
                    case "mul" -> {
                        requireArgs(tokens, 2);
                        out.println(math.mul(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
                    }
                    default -> out.println("Comando desconocido: " + cmd + " (prueba 'help')");
                }
            } catch (RuntimeException ex) {
                out.println("ERROR: " + ex.getMessage());
            }
        }
    }

    private static void requireArgs(String[] tokens, int expected) {
        if (tokens.length - 1 < expected) {
            throw new IllegalArgumentException(
                "faltan argumentos: se esperaban " + expected + ", vinieron " + (tokens.length - 1));
        }
    }
}
