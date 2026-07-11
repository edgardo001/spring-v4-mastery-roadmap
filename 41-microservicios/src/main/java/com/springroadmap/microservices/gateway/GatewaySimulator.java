package com.springroadmap.microservices.gateway;

import com.springroadmap.microservices.registry.ServiceRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;                    // Cliente HTTP moderno (reemplaza RestTemplate).
import org.springframework.web.client.RestClientResponseException;   // Excepcion cuando el downstream devuelve 4xx/5xx.
import org.springframework.web.servlet.HandlerMapping;                // Constante para leer el path original.

import jakarta.servlet.http.HttpServletRequest; // Servlet request para extraer la ruta.

/**
 * GatewaySimulator: simula un API Gateway (tipo Spring Cloud Gateway o NGINX)
 * que enruta peticiones por nombre de servicio.
 *
 * FLUJO:
 *   1. Cliente llama GET /gateway/pagos/facturas/123
 *   2. Extraemos "pagos" (nombre de servicio) y "/facturas/123" (path restante).
 *   3. Preguntamos al Registry por una URL de "pagos" (Round-Robin).
 *   4. Hacemos GET a esa URL + path restante con RestClient.
 *   5. Devolvemos el body al cliente.
 *
 * LIMITACIONES DE ESTA SIMULACION:
 *   - Solo GET (no POST/PUT/DELETE), sin headers, sin auth, sin timeouts, sin
 *     circuit breaker. En produccion usarias Spring Cloud Gateway o Kong.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *
 *   ANTES:
 *     RestTemplate rest = new RestTemplate();
 *     String body = rest.getForObject(url, String.class);
 *
 *   AHORA:
 *     RestClient client = RestClient.create();
 *     String body = client.get().uri(url).retrieve().body(String.class);
 *     // API fluida encadenable, mas legible.
 */
@RestController
@RequestMapping("/gateway")
public class GatewaySimulator {

    private final ServiceRegistry registry;
    private final RestClient restClient; // RestClient reutilizable (thread-safe).

    public GatewaySimulator(ServiceRegistry registry) {
        this.registry = registry;
        // RestClient.create() = builder con defaults; en tests reales lo mockeas.
        this.restClient = RestClient.create();
    }

    /**
     * Ruta comodin: /gateway/{service}/** captura CUALQUIER path bajo el nombre.
     * '**' en Spring MVC = "cero o mas segmentos", ej: /gateway/pagos/a/b/c.
     */
    @GetMapping("/{service}/**")
    public ResponseEntity<String> proxy(@PathVariable String service,
                                        HttpServletRequest request) {
        // 1) Buscamos una URL para ese servicio (Round-Robin).
        String targetBase = registry.nextUrl(service);
        if (targetBase == null) {
            // Servicio no registrado -> 404 con mensaje explicativo.
            return ResponseEntity.status(404)
                .body("Servicio '" + service + "' no registrado en el discovery.");
        }

        // 2) Extraemos el path REAL que Spring resolvio (incluye lo que capturo el **).
        // La constante BEST_MATCHING_PATTERN_ATTRIBUTE guarda el path completo original.
        String fullPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // fullPath ejemplo: "/pagos/facturas/123" -> quitamos "/pagos" para obtener "/facturas/123".
        String prefix = "/" + service;
        String remainder = fullPath.startsWith(prefix) ? fullPath.substring(prefix.length()) : "";
        if (remainder.isEmpty()) {
            remainder = "/"; // Evita URLs feas tipo "http://host:8081" sin slash.
        }

        // 3) Construimos la URL destino y proxificamos.
        String targetUrl = targetBase + remainder;
        try {
            String body = restClient.get()
                .uri(targetUrl)
                .retrieve()
                .body(String.class); // Cuerpo como String (agnostico de content-type).
            return ResponseEntity.ok(body);
        } catch (RestClientResponseException ex) {
            // Downstream respondio con error (4xx/5xx): reenviamos el status.
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            // Cualquier otro fallo (host inalcanzable, timeout) -> 502 Bad Gateway.
            return ResponseEntity.status(502)
                .body("Bad Gateway: no se pudo contactar '" + targetUrl + "' (" + ex.getMessage() + ")");
        }
    }
}
