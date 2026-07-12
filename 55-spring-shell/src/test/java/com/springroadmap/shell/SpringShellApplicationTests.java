package com.springroadmap.shell;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(args = "--no-shell")
class SpringShellApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el ApplicationContext arranca con todos los beans
        // (MathCommands, PromptProvider, ShellRunner) y que --no-shell
        // corta el REPL sin bloquear el test.
    }
}
