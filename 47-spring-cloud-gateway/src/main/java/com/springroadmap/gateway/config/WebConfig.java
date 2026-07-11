package com.springroadmap.gateway.config;

import com.springroadmap.gateway.filter.GatewayFilter;
import com.springroadmap.gateway.filter.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.client.RestClient;

/**
 * WebConfig — registra los filtros y el RestClient del gateway.
 *
 * Analogía:
 *   Es el organigrama de recepción. Define QUIÉN atiende primero
 *   (RateLimit → GatewayFilter) y en qué orden.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué el orden importa?"
 *   El rate limit debe ejecutarse ANTES del proxy. Si un atacante
 *   dispara 1000 req/s, queremos rechazarlas rápido, sin gastar
 *   recursos abriendo conexiones al backend.
 *
 * PREGUNTA DE ALUMNO — "¿Qué es RestClient?"
 *   El sucesor moderno de RestTemplate (desde Spring 6.1). API
 *   fluida (.get().uri().retrieve()) y no requiere WebFlux.
 */
@Configuration
public class WebConfig {

    /**
     * RestClient bean — cliente HTTP que usa el GatewayFilter para
     * hacer proxy al backend.
     * NOTA (MEMORY.md módulo 19): en Boot 4.1.0 NO existe autoconfig
     * de RestClient.Builder; hay que construirlo aquí.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    /**
     * Registro del RateLimitFilter con orden alto (se ejecuta primero).
     * Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE. Menor número = antes.
     */
    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(RateLimitFilter filter) {
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        reg.addUrlPatterns("/*");
        return reg;
    }

    /**
     * Registro del GatewayFilter con orden posterior.
     * Se ejecuta DESPUÉS del rate limit.
     */
    @Bean
    public FilterRegistrationBean<GatewayFilter> gatewayFilterRegistration(GatewayFilter filter) {
        FilterRegistrationBean<GatewayFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        reg.addUrlPatterns("/*");
        return reg;
    }
}
