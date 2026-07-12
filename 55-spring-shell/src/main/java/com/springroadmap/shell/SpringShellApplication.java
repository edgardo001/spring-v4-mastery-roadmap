package com.springroadmap.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del modulo 55.
 *
 * No arranca Tomcat (solo starter base): al terminar el
 * {@link com.springroadmap.shell.runner.ShellRunner}, el proceso termina.
 */
@SpringBootApplication
public class SpringShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringShellApplication.class, args);
    }
}
