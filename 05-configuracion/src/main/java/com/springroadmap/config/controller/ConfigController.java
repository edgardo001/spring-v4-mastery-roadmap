package com.springroadmap.config.controller;

import com.springroadmap.config.props.AppProperties;
import com.springroadmap.config.service.LegacyValueService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone la configuración resuelta.
 *
 * Endpoint:  GET /api/config
 * Respuesta: JSON serializado desde el `record` {@link ConfigResponse}.
 *
 * Este controller demuestra AMBOS enfoques a la vez:
 *   - Inyecta el `AppProperties` (moderno, type-safe).
 *   - Inyecta el `LegacyValueService` (viejo, @Value).
 *
 * Ambos leen del MISMO archivo YAML. Cambia el perfil activo y verás
 * los valores cambiar por ambos caminos.
 *
 * =====================================================================
 * ANTES (Java 8) vs AHORA (Java 21) — la respuesta como DTO
 * =====================================================================
 * ANTES: clase pública con campos privados + getters + constructor +
 *        Jackson serializaba invocando los getters.
 * AHORA: `record` inmutable con campos declarados en el header. Jackson
 *        los serializa usando los accessors del record.
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final AppProperties appProperties;
    private final LegacyValueService legacyValueService;

    /**
     * Constructor injection. Sin @Autowired (redundante desde Spring 4.3
     * cuando hay un único constructor).
     */
    public ConfigController(AppProperties appProperties,
                            LegacyValueService legacyValueService) {
        this.appProperties = appProperties;
        this.legacyValueService = legacyValueService;
    }

    /**
     * Devuelve el estado actual de la configuración: el nombre de la app,
     * la versión, los feature flags y el saludo del servicio legacy.
     *
     * @return DTO con la configuración resuelta según el perfil activo.
     */
    @GetMapping
    public ConfigResponse getConfig() {
        return new ConfigResponse(
                appProperties.name(),
                appProperties.version(),
                appProperties.features().emailEnabled(),
                appProperties.features().maxUsers(),
                legacyValueService.greet()
        );
    }

    /**
     * DTO de respuesta expresado como `record` (Java 16+).
     *
     * Un record genera automáticamente:
     *   - Constructor canónico con todos los campos.
     *   - Accessors sin prefijo `get` (`name()`, `version()`, ...).
     *   - equals(), hashCode(), toString() coherentes.
     *
     * PREGUNTA DE ALUMNO — "¿por qué el record VA DENTRO del controller?"
     *   Es una convención para records que solo se usan aquí. Si otro
     *   controller/servicio lo necesitara, iría a su propio archivo en
     *   un paquete `dto/`.
     */
    public record ConfigResponse(
            String name,
            String version,
            boolean emailEnabled,
            int maxUsers,
            String legacyGreeting
    ) {
    }
}
