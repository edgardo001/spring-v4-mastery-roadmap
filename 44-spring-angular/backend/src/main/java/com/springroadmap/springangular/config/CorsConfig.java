package com.springroadmap.springangular.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig: habilita CORS globalmente para el origen del frontend Angular
 * en desarrollo (Angular CLI = 4200).
 *
 * ANALOGIA: CORS es el "control de acceso" del navegador. Si Angular vive en
 * http://localhost:4200 y llama a http://localhost:8080, el navegador bloquea
 * la respuesta A MENOS que el servidor devuelva los headers
 * Access-Control-Allow-Origin. Este bean los agrega.
 *
 * NOTA (MEMORY.md 43/44-frontend): en dev es MAS comodo usar `proxy.conf.json`
 * de Angular CLI para redirigir /api al backend y evitar CORS por completo.
 * Este CorsConfig existe para el caso en que Angular hace fetch directo al
 * puerto 8080 (ej. sin proxy, tests, prod).
 *
 * ANTES (Java 8 / AngularJS 1.x): mismo origen -> no habia CORS.
 * AHORA (Java 21 / Angular v22): SPA + API separadas -> CORS obligatorio.
 *
 * ANTES (WebMvcConfigurerAdapter, deprecated en Spring 5) vs
 * AHORA (interface WebMvcConfigurer con default methods).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")  // Angular CLI ng serve
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
