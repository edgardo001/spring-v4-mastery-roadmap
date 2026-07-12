package com.springroadmap.rsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del modulo 61-rsocket.
 *
 * <p>Al arrancar, Spring Boot detecta {@code spring-boot-starter-rsocket} y
 * levanta un servidor RSocket sobre TCP en el puerto configurado en
 * {@code application.yml} (7000). Ademas, WebFlux/Netty levanta el servidor
 * HTTP en 8080 para exponer el bridge REST.</p>
 */
@SpringBootApplication
public class SpringRoadmapApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SpringRoadmapApplication.class, args);
    }
}
