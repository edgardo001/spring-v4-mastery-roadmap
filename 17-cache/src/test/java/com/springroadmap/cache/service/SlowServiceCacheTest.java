package com.springroadmap.cache.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

/**
 * Verifica el comportamiento REAL del caché a través del proxy de Spring.
 *
 * Requiere @SpringBootTest (no un test unitario "puro"), porque solo el contexto
 * de Spring aplica el aspecto @Cacheable. Instanciar new SlowService() saltaría
 * el proxy y el caché no funcionaría.
 */
@SpringBootTest
class SlowServiceCacheTest {

    @Autowired
    private SlowService service;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void reset() {
        // Limpiamos contador y caché para aislar cada test.
        service.resetCallCount();
        cacheManager.getCache("items").clear();
    }

    @Test
    void segundaLlamadaVieneDeCache() {
        service.getItem(1L);
        service.getItem(1L);

        // Solo una ejecución real; la segunda salió del caché.
        assertThat(service.getCallCount()).isEqualTo(1);
    }

    @Test
    void invalidateFuerzaNuevaEjecucion() {
        service.getItem(1L);           // ejecuta real -> counter = 1, cachea
        service.invalidate(1L);        // borra entrada del caché
        service.getItem(1L);           // vuelve a ejecutar real -> counter = 2

        assertThat(service.getCallCount()).isEqualTo(2);
    }
}
