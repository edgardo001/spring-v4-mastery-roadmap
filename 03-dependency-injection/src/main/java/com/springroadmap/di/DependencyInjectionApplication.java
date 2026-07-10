// Package raíz del módulo (debe coincidir con la carpeta física).
package com.springroadmap.di;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 *
 * Esta clase reproduce el patrón del módulo 02: una sola anotación
 * (@SpringBootApplication) más un método main. Lo NUEVO del módulo 03 no
 * está aquí — está en las otras clases (@Service, @Repository, @Controller,
 * @Configuration) donde veremos cómo Spring las conecta automáticamente.
 *
 * PREGUNTA DE ALUMNO — "¿Cuándo se CREAN los @Service/@Repository?"
 *   Justo aquí, cuando SpringApplication.run(...) arranca el contexto:
 *     1. Escanea el paquete `com.springroadmap.di` y sus subpaquetes.
 *     2. Encuentra las clases marcadas con @Service, @Repository,
 *        @RestController, @Configuration.
 *     3. Las instancia UNA sola vez (singletons por defecto).
 *     4. Resuelve las dependencias del constructor y las inyecta.
 *     5. Cuando llega un HTTP request, ya está todo cableado.
 */
@SpringBootApplication
public class DependencyInjectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DependencyInjectionApplication.class, args);
    }
}
