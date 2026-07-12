package com.springroadmap.rsocket.domain;

/**
 * DTO inmutable devuelto por los endpoints RSocket.
 *
 * <p>ANTES (Java 8): habria sido una clase POJO con constructor, getters,
 * equals/hashCode y toString manuales (o Lombok).</p>
 *
 * <p>AHORA (Java 21): un {@code record} genera todo eso automaticamente en
 * una sola linea, e implica inmutabilidad total (los campos son {@code final}).</p>
 */
public record Greeting(String message) {
}
