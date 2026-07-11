package com.springroadmap.microservices.registry;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test UNITARIO puro (sin Spring): instancia la clase directamente con 'new'.
 * Rapido, aislado, sin autoconfig ni classpath scanning.
 */
class ServiceRegistryTest {

    @Test
    void registerAndListReturnsRegisteredUrl() {
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("pagos", "http://localhost:8081");

        assertThat(registry.listAll()).containsKey("pagos");
        assertThat(registry.getUrls("pagos")).containsExactly("http://localhost:8081");
    }

    @Test
    void nextUrlDoesRoundRobinOverMultipleInstances() {
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("pagos", "http://a:8081");
        registry.register("pagos", "http://b:8081");
        registry.register("pagos", "http://c:8081");

        // Round-Robin: llamadas consecutivas deben rotar por las 3 URLs.
        String u1 = registry.nextUrl("pagos");
        String u2 = registry.nextUrl("pagos");
        String u3 = registry.nextUrl("pagos");
        String u4 = registry.nextUrl("pagos");

        assertThat(List.of(u1, u2, u3)).containsExactlyInAnyOrder(
            "http://a:8081", "http://b:8081", "http://c:8081"
        );
        // La 4a llamada vuelve a la 1a URL (ciclo completo).
        assertThat(u4).isEqualTo(u1);
    }

    @Test
    void nextUrlReturnsNullForUnknownService() {
        ServiceRegistry registry = new ServiceRegistry();
        assertThat(registry.nextUrl("no-existe")).isNull();
    }
}
