package com.springroadmap.cloudconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Feature flags externalizados desde {@code application.yml} (prefijo
 * {@code app.features}). Este bean es la <b>representacion tipada</b> de
 * la configuracion remota que en un Spring Cloud Config real vendria
 * desde un repositorio Git.
 *
 * <p>Analogia: es la ficha tecnica que Recursos Humanos entrega al
 * empleado en su primer dia — le dice que puertas puede abrir
 * ({@code betaEnabled}) y cuantas veces puede intentar loguearse
 * ({@code maxRetries}) antes de bloquearse.
 *
 * <hr>
 * <b>ANTES (Java 8) vs AHORA (Java 21)</b>
 * <pre>
 * // ANTES: leer cada valor por separado con &#64;Value ("acopla y ensucia").
 * &#64;Value("${app.features.beta-enabled}") boolean betaEnabled;
 * &#64;Value("${app.features.max-retries}") int maxRetries;
 *
 * // AHORA: un solo objeto tipado, validable y auto-documentado.
 * &#64;ConfigurationProperties(prefix = "app.features")
 * public class FeatureFlags { ... }
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por que no uso un record?"
 *   Los records son inmutables. {@code @ConfigurationProperties} necesita
 *   poder <i>rebind</i> los valores tras un {@code /actuator/refresh}, y
 *   eso requiere setters. Por eso conservamos la clase con getters/setters
 *   clasicos.
 */
// @ConfigurationProperties(prefix="app.features") indica a Spring que
// mapee automaticamente las claves 'app.features.*' de application.yml
// a los campos de esta clase (convirtiendo 'beta-enabled' -> 'betaEnabled').
@ConfigurationProperties(prefix = "app.features")
public class FeatureFlags {

    // Bandera booleana: activa/desactiva la funcionalidad beta.
    // 'private' -> encapsulacion, solo accesible desde esta clase.
    private boolean betaEnabled;

    // Cuantas veces reintentar una operacion antes de rendirse.
    private int maxRetries;

    // Getter (metodo que Spring usa para LEER el valor tras el binding).
    public boolean isBetaEnabled() {
        return betaEnabled;
    }

    // Setter (metodo que Spring usa para ESCRIBIR el valor durante el binding).
    public void setBetaEnabled(boolean betaEnabled) {
        this.betaEnabled = betaEnabled;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}
