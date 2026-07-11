package com.springroadmap.gateway.filter;

import com.springroadmap.gateway.config.RouteConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimitFilter — limita las requests por IP usando Token Bucket.
 *
 * Algoritmo Token Bucket:
 *   - Cada IP tiene un "cubo" con N tokens (capacidad = 10 req/s).
 *   - Cada request CONSUME 1 token.
 *   - Cada segundo, el cubo se recarga hasta la capacidad máxima.
 *   - Si al llegar la request no hay tokens → 429 Too Many Requests.
 *
 * Analogía:
 *   Un parquímetro. Puedes usarlo 10 veces por segundo. Si insertas
 *   la moneda 11 el mismo segundo, se traba. Al pasar el segundo, se
 *   destraba y puedes usarlo de nuevo.
 *
 * PREGUNTA DE ALUMNO — "¿Qué es ConcurrentHashMap?"
 *   Un HashMap thread-safe. Múltiples requests concurrentes pueden
 *   leer/escribir sin corromper los datos ni bloquear todo el mapa.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - ANTES: HashMap + synchronized bloque por acceso (más lento).
 *   - AHORA: ConcurrentHashMap.computeIfAbsent (atómico, sin lock global).
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final int capacity;

    public RateLimitFilter(RouteConfig routeConfig) {
        this.capacity = routeConfig.getRateLimit().getRequestsPerSecond();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String ip = clientIp(request);
        Bucket bucket = buckets.computeIfAbsent(ip, k -> new Bucket(capacity));

        if (bucket.tryConsume()) {
            chain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP {}", ip);
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"rate_limit_exceeded\"}");
        }
    }

    /**
     * clientIp — extrae la IP del request. En producción hay que
     * revisar cabeceras X-Forwarded-For (detrás de load balancer),
     * pero acá usamos el remoteAddr directo por simplicidad.
     */
    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Bucket — cubo de tokens por IP.
     * Refill: 1 vez por segundo restablecemos al máximo.
     *
     * Nota: implementación simple no thread-perfecta a nivel micro-
     * segundo, pero suficiente para demo y para el test unitario del
     * módulo. Un gateway real usaría Guava RateLimiter o Bucket4j.
     */
    static class Bucket {
        private final int capacity;
        private int tokens;
        private long lastRefillMillis;

        Bucket(int capacity) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.lastRefillMillis = System.currentTimeMillis();
        }

        synchronized boolean tryConsume() {
            refillIfNeeded();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refillIfNeeded() {
            long now = System.currentTimeMillis();
            if (now - lastRefillMillis >= 1000L) {
                tokens = capacity;
                lastRefillMillis = now;
            }
        }
    }
}
