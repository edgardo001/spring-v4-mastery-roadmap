package com.springroadmap.dtos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de ENTRADA: lo que el cliente REST envia en un POST /api/employees.
 *
 * Observa que NO existe el campo 'id' (lo genera la BD) ni 'internalNotes'
 * (el cliente no puede escribir en un campo sensible del backoffice).
 *
 * ANTES (Java 8 - POJO clasico):
 *   public class EmployeeRequest {
 *       private String firstName;
 *       private String lastName;
 *       private BigDecimal salary;
 *       private LocalDate hireDate;
 *
 *       public EmployeeRequest() { }
 *       public String getFirstName() { return firstName; }
 *       public void setFirstName(String firstName) { this.firstName = firstName; }
 *       // ... otros 6 metodos ...
 *       public boolean equals(Object o) { ... }
 *       public int hashCode() { ... }
 *       public String toString() { ... }
 *   }
 *   Facil llegar a 60+ lineas.
 *
 * AHORA (Java 21 - record):
 *   Una sola linea. Java genera el constructor, los accessors (firstName(), no getFirstName()),
 *   equals, hashCode y toString automaticamente. Ademas es INMUTABLE.
 *
 * PREGUNTA DE ALUMNO - "que es un 'record'?"
 *   Es una clase especial introducida en Java 14 (estable en Java 16) pensada
 *   para representar datos. Sus campos son 'final' de forma automatica, no se
 *   pueden modificar despues de crear el objeto. Perfecto para DTOs.
 */
public record EmployeeRequest(
        String firstName,
        String lastName,
        BigDecimal salary,
        LocalDate hireDate
) { }
