package com.springroadmap.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo 06 (CRUD sobre H2 usando JdbcTemplate).
 *
 * Analogía: esta clase es como el "interruptor de la luz" del edificio.
 * Al ejecutarla, Spring Boot:
 *   1. Levanta el servidor Tomcat embebido (por spring-boot-starter-web).
 *   2. Autoconfigura un DataSource HikariCP apuntando a H2 (por
 *      spring-boot-starter-jdbc + el driver H2 en runtime).
 *   3. Ejecuta schema.sql y data.sql para dejar la BD lista.
 *   4. Crea el bean JdbcTemplate y lo inyecta al CustomerRepository.
 *
 * PREGUNTA DE ALUMNO — "¿Dónde se crea el JdbcTemplate?"
 *   Lo crea automáticamente Spring Boot cuando detecta un DataSource en
 *   el contexto. Nosotros solo lo pedimos por constructor y él aparece.
 */
@SpringBootApplication
public class BaseDatosJdbcApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BaseDatosJdbcApplication.class, args);
    }
}
