package com.springroadmap.batch.domain;

/**
 * DTO plano que mapea UNA fila del CSV customers.csv (columnas: name, email).
 *
 * Analogía: el "sobre en bruto" apenas llegado, antes de que el validador lo
 * revise y le pegue etiqueta (Customer).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - Antes: clase con getters/setters manuales.
 *   - Ahora: podríamos usar 'record', pero el FlatFileItemReader de Spring Batch
 *     con BeanWrapperFieldSetMapper NECESITA setters (JavaBean). Records no
 *     encajan directo. Por eso mantenemos POJO clásico.
 *
 * PREGUNTA DE ALUMNO — "¿por qué separar DTO y Entity?"
 *   Porque la fuente (CSV) y el destino (tabla) son cosas distintas. El DTO
 *   representa la fila cruda; la Entity representa la tabla. En el medio, el
 *   Processor decide qué hacer (validar, transformar, descartar).
 */
public class CustomerDto {

    private String name;
    private String email;

    /** Constructor sin argumentos requerido por el FieldSetMapper. */
    public CustomerDto() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
