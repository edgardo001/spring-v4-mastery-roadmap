package com.springroadmap.config.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Servicio "legacy" que ilustra el PATRÓN VIEJO de leer propiedades:
 * usar @Value("${...}") por cada valor suelto.
 *
 * ¿Por qué lo mantenemos? Para que el alumno reconozca este patrón cuando
 * herede código antiguo, y compare directamente con AppProperties
 * (el patrón moderno type-safe usado por AdvancedConfigService).
 *
 * =====================================================================
 * ANTES (Java 8 + Spring — @Value en cada campo/param)
 * =====================================================================
 * Es lo que ves aquí: rápido para uno o dos valores, INMANEJABLE cuando
 * hay 15. Cada refactor de nombre en el YAML obliga a buscar en TODO el
 * código dónde se usa esa propiedad.
 *
 * =====================================================================
 * AHORA (Java 21 + @ConfigurationProperties)
 * =====================================================================
 * Un solo record (AppProperties) agrupa todas las claves con un prefijo.
 * El IDE avisa cuando cambias un nombre y hay type-safety.
 *
 * PREGUNTA DE ALUMNO — "¿qué es la sintaxis '${app.name:VALOR-POR-DEFECTO}'?"
 *   Los dos puntos separan el nombre de la propiedad de su valor por defecto.
 *   Sin `:VALOR`, si la propiedad no existe, la app NO ARRANCA.
 */
@Service
public class LegacyValueService {

    // "final" = referencia inmutable, se asigna una sola vez en el constructor.
    private final String appName;

    /**
     * Constructor injection con @Value en el PARÁMETRO (no en el campo).
     * Esto es lo más cercano al patrón "moderno" dentro del enfoque viejo.
     *
     * @param appName valor inyectado desde `app.name` del YAML activo.
     */
    public LegacyValueService(@Value("${app.name:desconocido}") String appName) {
        this.appName = appName;
    }

    /**
     * Devuelve un saludo formado con el nombre de la app inyectado.
     *
     * @return frase con formato "Legacy dice: hola desde <nombre>".
     */
    public String greet() {
        return "Legacy dice: hola desde " + appName;
    }
}
