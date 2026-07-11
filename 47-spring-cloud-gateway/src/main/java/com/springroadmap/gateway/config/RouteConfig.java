package com.springroadmap.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RouteConfig — configuración type-safe de las rutas del gateway.
 *
 * Analogía:
 *   Es la libreta de direcciones de la recepción del edificio. Dice
 *   "si el visitante pide 'ventas' (/api/users), llévalo al piso 5
 *   (jsonplaceholder.typicode.com/users)".
 *
 * PREGUNTA DE ALUMNO — "¿Qué es @ConfigurationProperties?"
 *   Es una anotación que mapea propiedades de application.yml a un
 *   objeto Java. En vez de tener @Value("${gateway.routes...}") por
 *   todas partes, agrupamos toda la config en una clase type-safe.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué usar Map en vez de List?"
 *   Map<pathPrefix, targetUrl> permite buscar el destino en O(log n)
 *   (con TreeMap) o iterar sobre las entradas. Además el YAML se lee
 *   naturalmente como key: value.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - ANTES (clase POJO):
 *       public class RouteConfig {
 *           private Map<String,String> routes = new HashMap<>();
 *           public Map<String,String> getRoutes(){ return routes; }
 *           public void setRoutes(Map<String,String> r){ this.routes=r; }
 *       }
 *   - AHORA: Podríamos usar un `record` PERO @ConfigurationProperties
 *     con setters es más flexible (soporta binding relajado). Aquí
 *     mantenemos la clase POJO tradicional para claridad didáctica.
 */
@ConfigurationProperties(prefix = "gateway")
public class RouteConfig {

    /** path-prefix -> URL destino. Ej: "/api/users" -> "https://jsonplaceholder.typicode.com/users". */
    private Map<String, String> routes = new LinkedHashMap<>();

    /** Configuración de rate limit. */
    private RateLimit rateLimit = new RateLimit();

    public Map<String, String> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, String> routes) {
        this.routes = routes;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    /**
     * Sub-configuración del rate limit.
     * requestsPerSecond define el tamaño y ritmo de recarga del token bucket.
     */
    public static class RateLimit {
        private int requestsPerSecond = 10;

        public int getRequestsPerSecond() {
            return requestsPerSecond;
        }

        public void setRequestsPerSecond(int requestsPerSecond) {
            this.requestsPerSecond = requestsPerSecond;
        }
    }

    /**
     * findTarget — busca la ruta que matchea al path del request.
     * Retorna null si ninguna ruta configurada aplica.
     *
     * Estrategia: startsWith sobre el prefijo. Iteramos porque el mapa
     * es pequeño (unas decenas de rutas típicamente).
     */
    public String findTarget(String requestPath) {
        // ANTES (Java 8): for (Map.Entry<String,String> e : routes.entrySet()) { ... }
        // AHORA (Java 21): mismo for; podríamos usar streams pero for es más claro.
        for (Map.Entry<String, String> entry : routes.entrySet()) {
            if (requestPath.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
