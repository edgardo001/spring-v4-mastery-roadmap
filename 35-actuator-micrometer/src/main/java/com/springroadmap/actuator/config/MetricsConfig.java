package com.springroadmap.actuator.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de metricas Micrometer.
 *
 * ANALOGIA: el {@link MeterRegistry} es una "central de sensores" y cada {@link Counter}
 * es un sensor especifico (como el contador de pasajeros de un torniquete).
 *
 * QUE ES un Counter?
 *   Una metrica monotonicamente creciente (solo sube o se resetea). Ideal para contar
 *   eventos: pedidos creados, errores, logins, etc.
 *
 * POR QUE se declara aqui?
 *   Registrar el Counter una sola vez, en el arranque, evita crear instancias duplicadas
 *   en cada request. Micrometer garantiza que si el mismo nombre + tags ya existe,
 *   te devuelve el mismo Counter.
 *
 * ANTES (JMX MBeans manuales) vs AHORA (Micrometer):
 *   ANTES:
 *     public class OrdersMBean implements OrdersMBeanMXBean {
 *         private final AtomicLong count = new AtomicLong();
 *         public long getCount() { return count.get(); }
 *         public void increment() { count.incrementAndGet(); }
 *     }
 *     // + registro manual en MBeanServer + JMX exporter para Prometheus
 *   AHORA:
 *     Counter c = registry.counter("app.orders.created");
 *     c.increment();  // Micrometer se encarga del resto (Prometheus, JMX, StatsD, etc.)
 */
@Configuration
public class MetricsConfig {

    /**
     * Registra un Counter llamado "app.orders.created".
     * <p>
     * Micrometer traduce puntos a guiones bajos al exportarlo a Prometheus:
     *   {@code app.orders.created} -> {@code app_orders_created_total} en /actuator/prometheus.
     * <p>
     * PALABRAS CLAVE:
     * - {@code @Bean}: le dice a Spring "gestiona esta instancia como un objeto reutilizable".
     * - Parametro {@code MeterRegistry}: Spring lo inyecta automaticamente (Boot autoconfigura
     *   uno de tipo Prometheus porque el jar micrometer-registry-prometheus esta en classpath).
     */
    @Bean
    public Counter ordersCounter(MeterRegistry registry) {
        // .counter(name) es idempotente: si ya existe, devuelve el mismo.
        return registry.counter("app.orders.created");
    }
}
