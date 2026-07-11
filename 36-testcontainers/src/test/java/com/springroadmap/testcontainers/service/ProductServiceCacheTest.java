package com.springroadmap.testcontainers.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración contra un Redis REAL en contenedor Docker.
 *
 * <p><b>REQUIERE Docker Desktop.</b> Marcado con <code>@Disabled</code> por la
 * misma razón que <code>ProductRepositoryPostgresTest</code>: <code>@Testcontainers</code>
 * arranca el contenedor antes de que un <code>Assumptions.assumeTrue</code>
 * pueda saltar la clase (ver MEMORY.md del módulo 25).</p>
 *
 * <p>Para activar: elimina la anotación <code>@Disabled</code> y ejecuta
 * <code>mvn test -Dtest=ProductServiceCacheTest</code>.</p>
 *
 * <p>Analogía: Redis es como una libreta ultra-rápida junto al cajero.
 * Este test verifica que podemos escribir y leer en esa libreta REAL.</p>
 */
@Disabled("Requiere Docker Desktop para Redis. Para activar: elimina @Disabled y ejecuta 'mvn test'.")
@SpringBootTest
@Testcontainers
class ProductServiceCacheTest {

    /**
     * Contenedor Redis 7 alpine con el puerto 6379 expuesto.
     * Testcontainers mapea internamente 6379 a un puerto libre del host.
     */
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    /**
     * Ajusta host y puerto de Redis en el Environment de Spring antes de crear
     * el bean <code>RedisConnectionFactory</code>.
     */
    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void writesAndReadsFromRealRedis() {
        // Arrange & Act: escribe una clave en el Redis real.
        redisTemplate.opsForValue().set("product:1:name", "Café Premium");

        // Assert: la clave viaja de vuelta intacta.
        String value = redisTemplate.opsForValue().get("product:1:name");
        assertThat(value).isEqualTo("Café Premium");
    }
}

/*
 * ============================================================================
 * ANTES (mocks) vs AHORA (Redis real vía Testcontainers)
 * ============================================================================
 * ANTES: se mockeaba StringRedisTemplate con Mockito.
 *   - No detectaba errores de serialización, TTL, ni comandos Redis inválidos.
 *
 * AHORA: contenedor Redis 7 real.
 *   - Mismo comportamiento que producción (mismos comandos, mismos errores).
 * ============================================================================
 */
