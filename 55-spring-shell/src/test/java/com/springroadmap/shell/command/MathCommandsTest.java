package com.springroadmap.shell.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MathCommandsTest {

    private final MathCommands math = new MathCommands();

    @Test
    void addSumaDosEnteros() {
        assertThat(math.add(2, 3)).isEqualTo(5);
    }

    @Test
    void mulMultiplicaDosEnteros() {
        assertThat(math.mul(2, 3)).isEqualTo(6);
    }
}
