package com.springroadmap.springreact.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CorsConfig: habilita CORS globalmente para los origenes del frontend React
 * en desarrollo (Vite = 5173, Create React App = 3000).
 *
 * ANALOGIA: CORS es el "control de acceso" del navegador. Si el frontend vive
 * en http://localhost:5173 y llama a http://localhost:8080, el navegador
 * bloquea la respuesta A MENOS que el servidor devuelva los headers
 * Access-Control-Allow-Origin. Este bean los agrega.
 *
 * NOTA (MEMORY.md 43/44-frontend): en dev es mas comodo usar el proxy de Vite
 * (`server.proxy` en vite.config.ts) para redirigir /api al backend y evitar CORS
 * por completo. Este CorsConfig existe para el caso en que el frontend hace
 * fetch directo al puerto 8080.
 *
 * ANTES (Java 8): implementar WebMvcConfigurerAdapter (deprecated).
 * AHORA (Java 21): implementar la interface WebMvcConfigurer con default methods.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
