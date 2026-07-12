package com.springroadmap.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Modulo 59 - Aplicacion reactiva basada en Spring WebFlux + R2DBC.
 *
 * IMPORTANTE: este proyecto NO incluye spring-boot-starter-web. Boot detecta
 * unicamente webflux y arranca Reactor Netty (event loop no bloqueante) en
 * lugar de Tomcat.
 */
@SpringBootApplication
public class ReactiveApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }
}
