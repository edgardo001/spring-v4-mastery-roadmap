package com.springroadmap.dtos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de SALIDA: lo que el backend devuelve al cliente REST.
 *
 * Diferencias clave con la Entity Employee:
 *   - Trae 'id' (util para el frontend).
 *   - En lugar de firstName + lastName expone 'fullName' concatenado.
 *   - NO trae 'internalNotes' (dato sensible).
 *
 * ANTES (Java 8):
 *   Se armaba a mano en el Controller:
 *     EmployeeResponse r = new EmployeeResponse();
 *     r.setId(e.getId());
 *     r.setFullName(e.getFirstName() + " " + e.getLastName());
 *     r.setSalary(e.getSalary());
 *     r.setHireDate(e.getHireDate());
 *
 * AHORA (Java 21 + MapStruct):
 *   Se declara la interfaz EmployeeMapper con @Mapping y MapStruct
 *   genera todo el codigo de conversion automaticamente.
 */
public record EmployeeResponse(
        Long id,
        String fullName,
        BigDecimal salary,
        LocalDate hireDate
) { }
