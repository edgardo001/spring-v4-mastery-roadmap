package com.springroadmap.gateway.filter;

import com.springroadmap.gateway.config.RouteConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * GatewayFilter — intercepta el request y decide:
 *   - Si el path matchea alguna ruta configurada → hace proxy con RestClient.
 *   - Si no matchea → deja pasar al siguiente filtro / controller.
 *
 * Analogía:
 *   La recepcionista mira la libreta (RouteConfig). Si el visitante
 *   pidió "ventas", ella misma va al piso 5, trae la información y se
 *   la entrega al visitante. El visitante nunca entró al edificio.
 *
 * PREGUNTA DE ALUMNO — "¿Qué es OncePerRequestFilter?"
 *   Un filtro base de Spring que garantiza que se ejecute UNA VEZ por
 *   request (útil cuando hay forwards internos o dispatch async).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - ANTES: try/catch con StringBuilder para armar la URL.
 *   - AHORA: concatenación simple; en Java 21 podríamos usar
 *     String.format o el operador '+' (idéntico).
 */
@Component
public class GatewayFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayFilter.class);

    private final RouteConfig routeConfig;
    private final RestClient restClient;

    /** Constructor injection — patrón recomendado (ver AGENTS.md). */
    public GatewayFilter(RouteConfig routeConfig, RestClient restClient) {
        this.routeConfig = routeConfig;
        this.restClient = restClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Endpoints internos del gateway (/gateway/*) NO se proxean.
        if (path.startsWith("/gateway/")) {
            chain.doFilter(request, response);
            return;
        }

        String target = routeConfig.findTarget(path);
        if (target == null) {
            // Ningún prefijo matchea → dejamos que Spring MVC responda 404.
            chain.doFilter(request, response);
            return;
        }

        log.info("Forwarding {} {} -> {}", request.getMethod(), path, target);

        try {
            // Proxy simple GET (para demo). Un gateway real reenvía todos
            // los métodos + headers + body.
            ResponseEntity<String> upstream = restClient.get()
                    .uri(target)
                    .retrieve()
                    .toEntity(String.class);

            response.setStatus(upstream.getStatusCode().value());
            String contentType = upstream.getHeaders().getFirst("Content-Type");
            if (contentType != null) {
                response.setContentType(contentType);
            } else {
                response.setContentType("application/json");
            }
            response.getWriter().write(upstream.getBody() == null ? "" : upstream.getBody());
        } catch (RestClientException ex) {
            log.error("Upstream error forwarding to {}: {}", target, ex.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"upstream_unavailable\"}");
        }
    }
}
