package com.springroadmap.restclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO inmutable (Java record) que mapea la respuesta JSON de la API externa
 * https://jsonplaceholder.typicode.com/todos/{id}.
 *
 * Antes vs Ahora:
 *  - Antes: clases DTO con getters/setters, equals/hashCode manuales (o con Lombok).
 *  - Ahora: `record` genera todo eso automaticamente y expresa la intencion de
 *    "objeto de datos inmutable".
 *
 * @JsonIgnoreProperties evita fallar si la API devuelve mas campos (por ejemplo
 * userId) que no queremos consumir aqui.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Todo(long id, String title, boolean completed) {
}
