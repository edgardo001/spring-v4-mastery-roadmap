package com.springroadmap.restadv.domain;

import java.math.BigDecimal;

/**
 * Product — record inmutable.
 *
 * `version` participa en el cálculo del ETag: si el cliente conserva un
 * ETag y el `version` cambia, el ETag ya no coincide y el servidor
 * responderá 200 con el nuevo cuerpo. Si NO cambia, responde 304.
 *
 * Antes (Java 8): clase POJO con getters, setters, equals, hashCode.
 * Ahora (Java 21): un record cubre todo eso en una línea.
 */
public record Product(Long id, String name, BigDecimal price, String version) {
}
