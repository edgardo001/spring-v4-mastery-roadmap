package com.springroadmap.shell.runner;

import com.springroadmap.shell.command.MathCommands;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ShellRunnerTest {

    private final ShellRunner runner = new ShellRunner(new MathCommands(), new PromptProvider());

    @Test
    void repl_ejecuta_add_mul_y_sale_con_exit() throws Exception {
        String input = String.join("\n", "add 2 3", "mul 2 3", "exit") + "\n";
        BufferedReader in = new BufferedReader(new StringReader(input));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos, true, StandardCharsets.UTF_8);

        int rc = runner.loop(in, out);

        String output = baos.toString(StandardCharsets.UTF_8);
        assertThat(rc).isZero();
        assertThat(output).contains("5");
        assertThat(output).contains("6");
        assertThat(output).contains("roadmap>");
        assertThat(output).contains("bye");
    }

    @Test
    void repl_maneja_comando_desconocido_sin_romperse() throws Exception {
        String input = "foo\nexit\n";
        BufferedReader in = new BufferedReader(new StringReader(input));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos, true, StandardCharsets.UTF_8);

        int rc = runner.loop(in, out);

        assertThat(rc).isZero();
        assertThat(baos.toString(StandardCharsets.UTF_8)).contains("Comando desconocido");
    }
}
