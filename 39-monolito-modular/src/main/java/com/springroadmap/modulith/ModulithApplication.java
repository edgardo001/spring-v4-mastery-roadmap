package com.springroadmap.modulith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicacion principal - Monolito Modular.
 *
 * Antes vs Ahora:
 * - Antes: monolito espagueti, un solo paquete, servicios llamandose directamente,
 *   dependencias circulares, imposible extraer un modulo.
 * - Ahora: paquetes por dominio (orders, notifications) con API publica minima,
 *   detalles en 'internal', comunicacion desacoplada via eventos de Spring.
 */
@SpringBootApplication
public class ModulithApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithApplication.class, args);
    }
}
