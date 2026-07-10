package com.springroadmap.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades tipadas de la aplicación, mapeadas a claves del YAML con
 * el prefijo `app.*`.
 *
 * Ejemplo de mapeo (application.yml):
 *
 *   app:
 *     name: "..."
 *     version: "..."
 *     features:
 *       email-enabled: true
 *       max-users: 100
 *
 * Se traduce a:
 *   new AppProperties("...", "...", new Features(true, 100))
 *
 * =====================================================================
 * ANTES (Java 8 — POJO con getters/setters + @Value dispersos)
 * =====================================================================
 * public class AppProperties {
 *     private String name;
 *     private String version;
 *     // ... otro POJO Features anidado ...
 *     public String getName() { return name; }
 *     public void setName(String n) { this.name = n; }
 *     // etc. Constructor vacío obligatorio para el binding.
 * }
 * // En cada clase que quisiera usar una propiedad:
 * //   @Value("${app.name}") private String name;
 * //   @Value("${app.features.email-enabled}") private boolean emailEnabled;
 * // Problemas: no type-safe, sin autocompletado, difícil de refactorizar.
 *
 * =====================================================================
 * AHORA (Java 21 — `record` inmutable + @ConfigurationProperties)
 * =====================================================================
 * Un `record` es una clase COMPACTA: declara nombre + tipos de campos, y
 * Java genera automáticamente constructor, getters (con el nombre del
 * campo, sin el prefijo `get`), equals, hashCode y toString.
 *
 *   record AppProperties(String name, String version, Features features) {}
 *
 * Type-safe: si el YAML dice max-users: "abc", el arranque FALLA con un
 * mensaje claro en vez de un ClassCastException en runtime.
 *
 * PREGUNTA DE ALUMNO — "¿por qué kebab-case en el YAML (email-enabled)?"
 *   Es la convención "relaxed binding" de Spring Boot. Los guiones del
 *   YAML se traducen al camelCase del Java (`emailEnabled`). Ambas formas
 *   funcionan, pero kebab-case es la RECOMENDADA para YAML.
 *
 * PREGUNTA DE ALUMNO — "¿por qué el record anidado `Features`?"
 *   Refleja la jerarquía del YAML (`app.features.*`). Spring Boot detecta
 *   que el tipo del campo `features` es otro record y lo construye
 *   recursivamente. Es "type-safety hasta los sub-objetos".
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String name,
        String version,
        Features features
) {

    /**
     * Sub-record que agrupa los "feature flags" bajo `app.features.*`.
     *
     * `boolean` (primitivo) por defecto es false si la clave falta.
     * `int` (primitivo) por defecto es 0 si la clave falta.
     */
    public record Features(
            boolean emailEnabled,
            int maxUsers
    ) {
    }
}
