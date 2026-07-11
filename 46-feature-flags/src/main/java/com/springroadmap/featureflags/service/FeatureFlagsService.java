package com.springroadmap.featureflags.service;

import org.springframework.stereotype.Service;

import com.springroadmap.featureflags.config.FeatureFlags;

/**
 * FeatureFlagsService — API central para consultar y modificar flags en runtime.
 *
 * ANALOGÍA:
 * El "conserje" del edificio. Nadie prende los breakers directamente: le pides
 * al conserje "¿está prendida la luz del pasillo B?" y él te contesta.
 *
 * ¿POR QUÉ NO USAR FeatureFlags DIRECTAMENTE EN LOS CONTROLLERS?
 * - Aísla la lógica: hoy leemos de application.yml, mañana podríamos leer de
 *   una tabla Postgres o de Unleash sin tocar los controllers.
 * - Facilita el toggle en runtime (setEnabled) para demos y tests.
 *
 * NOTA SOBRE @RefreshScope:
 *   Spring Cloud Context tiene @RefreshScope para rebindear @ConfigurationProperties
 *   al hacer POST a /actuator/refresh. En este módulo no dependemos de Spring
 *   Cloud (por compatibilidad con Boot 4.1.0), pero exponemos setEnabled() que
 *   modifica el bean en memoria, cumpliendo el mismo objetivo pedagógico.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   // Antes: if / else if / else con strings
 *   if ("betaCheckout".equals(flag)) { ... }
 *   else if ("newSearch".equals(flag)) { ... }
 *   // Ahora: switch expression con "->" (Java 14+, estable en Java 21)
 *   return switch (flag) {
 *       case "betaCheckout" -> flags.isBetaCheckout();
 *       ...
 *   };
 */
@Service
public class FeatureFlagsService {

    /** Referencia al bean de flags (constructor injection, no @Autowired en campo). */
    private final FeatureFlags flags;

    /**
     * Constructor injection: Spring pasa automáticamente el bean FeatureFlags.
     * 'final' = el campo se asigna una sola vez, en el constructor.
     */
    public FeatureFlagsService(final FeatureFlags flags) {
        this.flags = flags;
    }

    /**
     * Consulta si un flag está activo.
     *
     * @param flag nombre del flag ("betaCheckout", "newSearch" o alguna clave
     *             del mapa custom).
     * @return true si está activo, false en cualquier otro caso.
     */
    public boolean isEnabled(final String flag) {
        // 'switch expression' con "->" (Java 14+). Cada rama retorna un valor.
        return switch (flag) {
            case "betaCheckout" -> flags.isBetaCheckout();
            case "newSearch"    -> flags.isNewSearch();
            // Boolean.TRUE.equals(...) evita NullPointerException cuando el mapa
            // no contiene la clave (getOrDefault también sirve).
            default             -> Boolean.TRUE.equals(flags.getCustom().get(flag));
        };
    }

    /**
     * Cambia un flag en runtime (útil para demos y para tests).
     * En producción real, esto vendría desde un panel Unleash/Togglz/LaunchDarkly
     * y se propagaría vía eventos a todas las instancias.
     */
    public void setEnabled(final String flag, final boolean enabled) {
        switch (flag) {
            case "betaCheckout" -> flags.setBetaCheckout(enabled);
            case "newSearch"    -> flags.setNewSearch(enabled);
            default             -> flags.getCustom().put(flag, enabled);
        }
    }

    /** Acceso directo al bean (útil para /actuator info o dashboards). */
    public FeatureFlags snapshot() {
        return flags;
    }
}
