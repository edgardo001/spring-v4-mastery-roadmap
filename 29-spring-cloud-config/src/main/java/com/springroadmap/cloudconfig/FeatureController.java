package com.springroadmap.cloudconfig;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST que expone las feature flags vigentes.
 *
 * <p>Endpoint: {@code GET /api/features} — retorna JSON con
 * {@code betaEnabled} y {@code maxRetries}, valores tomados directamente
 * del bean {@link FeatureFlags} (que a su vez fue rellenado desde
 * {@code application.yml}).
 *
 * <p>Analogia: es la <i>ventanilla de informacion</i> del edificio.
 * Cualquiera pregunta "¿que puertas estan abiertas hoy?" y el conserje
 * responde con la ficha vigente. Si el administrador cambia el manual
 * (o hace {@code POST /actuator/refresh} en el mundo Cloud Config real),
 * la ventanilla empezara a responder con los nuevos valores.
 *
 * <p>PREGUNTA DE ALUMNO — "¿por que no anoto la clase con {@code @RefreshScope}?"
 *   Porque {@code @RefreshScope} lo aporta {@code spring-cloud-context},
 *   que aun no tiene release para Boot 4.1.0. Cuando exista, bastara
 *   con anotar este controller (o mejor: {@link FeatureFlags}) y la
 *   recarga en caliente funcionara sin cambios adicionales.
 *
 * <hr>
 * <b>ANTES (Java 8) vs AHORA (Java 21)</b>
 * <pre>
 * // ANTES: crear el Map con new HashMap y varios put.
 * Map&lt;String, Object&gt; body = new HashMap&lt;&gt;();
 * body.put("betaEnabled", flags.isBetaEnabled());
 * body.put("maxRetries", flags.getMaxRetries());
 *
 * // AHORA: Map.of(...) crea un mapa inmutable en una linea.
 * Map.of("betaEnabled", flags.isBetaEnabled(),
 *        "maxRetries",  flags.getMaxRetries());
 * </pre>
 */
// @RestController = @Controller + @ResponseBody (serializa la respuesta a JSON).
@RestController
// Prefijo comun para todos los endpoints de este controller.
@RequestMapping("/api/features")
public class FeatureController {

    // 'final' -> el campo se asigna una sola vez (en el constructor)
    // y no puede reasignarse. Facilita razonar sobre concurrencia.
    private final FeatureFlags featureFlags;

    // Constructor injection (patron oficial del roadmap desde 2025-01).
    // Spring detecta el unico constructor y le inyecta el bean FeatureFlags.
    public FeatureController(FeatureFlags featureFlags) {
        this.featureFlags = featureFlags;
    }

    /**
     * GET /api/features
     *
     * @return HTTP 200 con un JSON de las banderas vigentes.
     */
    // @GetMapping mapea peticiones HTTP GET (sin path adicional aqui,
    // porque ya heredamos "/api/features" del @RequestMapping de la clase).
    @GetMapping
    public ResponseEntity<Map<String, Object>> current() {
        // Usamos LinkedHashMap para preservar el orden de insercion en el JSON.
        // (Map.of es mas conciso pero no garantiza orden en versiones antiguas.)
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("betaEnabled", featureFlags.isBetaEnabled());
        body.put("maxRetries", featureFlags.getMaxRetries());
        // ResponseEntity.ok(...) construye un 200 OK con el body dado.
        return ResponseEntity.ok(body);
    }
}
