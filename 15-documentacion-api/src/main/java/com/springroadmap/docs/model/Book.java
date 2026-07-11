package com.springroadmap.docs.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Modelo de dominio Book expresado como record de Java 21.
 *
 * Las anotaciones @Schema NO son obligatorias: springdoc infiere el schema
 * a partir de los tipos. Se agregan para enriquecer la documentación con
 * descripciones y ejemplos visibles en Swagger UI.
 */
@Schema(description = "Libro del catálogo")
public record Book(
        @Schema(description = "Identificador único", example = "1")
        Long id,

        @Schema(description = "Título del libro", example = "Clean Code")
        String title,

        @Schema(description = "Autor del libro", example = "Robert C. Martin")
        String author
) {
}
