package com.springroadmap.featureflags.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FeatureFlags — bean de configuración type-safe con los interruptores de la app.
 *
 * ANALOGÍA:
 * Un tablero eléctrico de la casa. Cada breaker (booleano) prende o apaga una
 * zona: cocina, patio, garage. Puedes tener zonas "predefinidas" (betaCheckout,
 * newSearch) y zonas "extensibles" (custom) para banderas nuevas que agregues
 * sin recompilar.
 *
 * ¿POR QUÉ @ConfigurationProperties y NO @Value?
 * - Con @Value("${features.beta-checkout}") tendrías strings sueltos por todo
 *   el código, sin type-safety ni autocompletado.
 * - Con @ConfigurationProperties, Spring toma el prefijo "features" y mapea
 *   las claves YAML a los campos de esta clase.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   // Antes: campos + getters + setters + constructor no-args (POJO clásico)
 *   private boolean betaCheckout;
 *   public boolean isBetaCheckout() { return betaCheckout; }
 *   public void setBetaCheckout(boolean v) { this.betaCheckout = v; }
 *   // Ahora: mismos POJOs (Spring @ConfigurationProperties todavía necesita
 *   // setters para binding "rebindable"; los records son inmutables y no
 *   // sirven cuando queremos hot-reload).
 */
@ConfigurationProperties(prefix = "features")
public class FeatureFlags {

    /** Flag para activar el nuevo flujo de checkout beta. Default: false. */
    private boolean betaCheckout = false;

    /** Flag para activar la nueva búsqueda. Default: false. */
    private boolean newSearch = false;

    /**
     * Mapa extensible de flags custom.
     * Ejemplo YAML:
     *   features:
     *     custom:
     *       promo-navidad: true
     *       banner-verano: false
     *
     * ANTES (Java 8): new HashMap&lt;String, Boolean&gt;()
     * AHORA (Java 21): new HashMap&lt;&gt;() gracias al diamond operator (que ya
     * existía desde Java 7, pero conviene recordarlo).
     */
    private Map<String, Boolean> custom = new HashMap<>();

    // --- getters / setters requeridos por @ConfigurationProperties ---

    public boolean isBetaCheckout() {
        return betaCheckout;
    }

    public void setBetaCheckout(boolean betaCheckout) {
        this.betaCheckout = betaCheckout;
    }

    public boolean isNewSearch() {
        return newSearch;
    }

    public void setNewSearch(boolean newSearch) {
        this.newSearch = newSearch;
    }

    public Map<String, Boolean> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, Boolean> custom) {
        this.custom = custom;
    }
}
