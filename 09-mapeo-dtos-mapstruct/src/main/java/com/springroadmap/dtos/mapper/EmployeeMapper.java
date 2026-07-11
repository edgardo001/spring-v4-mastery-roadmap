package com.springroadmap.dtos.mapper;

import com.springroadmap.dtos.domain.Employee;
import com.springroadmap.dtos.dto.EmployeeRequest;
import com.springroadmap.dtos.dto.EmployeeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para convertir entre Employee (Entity) y sus DTOs.
 *
 * Analogia del mundo real:
 *   Es un "traductor simultaneo" que ya memorizo el diccionario en tiempo
 *   de compilacion. Cuando le pides una traduccion en runtime, la responde
 *   al instante porque el codigo ya esta compilado (no usa reflexion).
 *
 * componentModel = "spring":
 *   MapStruct genera EmployeeMapperImpl y lo anota con @Component,
 *   de modo que puedas inyectarlo por constructor en cualquier @Service
 *   o @RestController.
 *
 * ANTES (Java 8 - mapeo manual):
 *   public EmployeeResponse toResponse(Employee e) {
 *       return new EmployeeResponse(
 *           e.getId(),
 *           e.getFirstName() + " " + e.getLastName(),
 *           e.getSalary(),
 *           e.getHireDate()
 *       );
 *   }
 *   Cada campo nuevo obliga a tocar el mapper Y sus tests.
 *
 * AHORA (Java 21 + MapStruct):
 *   Declaras una interfaz. El processor lee las anotaciones al compilar
 *   y genera EmployeeMapperImpl.java en target/generated-sources.
 *   Cero codigo boilerplate, cero reflexion en runtime.
 *
 * PREGUNTA DE ALUMNO - "donde esta la implementacion? veo solo una interfaz."
 *   MapStruct la genera en compile-time. Despues de `mvn compile` mira:
 *     target/generated-sources/annotations/com/springroadmap/dtos/mapper/EmployeeMapperImpl.java
 *   Ahi encontraras codigo Java normal (con getters/setters) que Spring
 *   registra como un bean.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    /**
     * Convierte una peticion (EmployeeRequest) en una Entity nueva.
     *
     * MapStruct copia por nombre: firstName -> firstName, lastName -> lastName,
     * salary -> salary, hireDate -> hireDate.
     *
     * Los campos que NO existen en el request (id, internalNotes) se ignoran
     * explicitamente con @Mapping(ignore=true). Al persistir, la BD asigna
     * el id y las notas quedan vacias hasta que un backoffice interno las
     * complete. Sin estos @Mapping, MapStruct 1.6+ falla en compile con
     * "Unmapped target properties".
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "internalNotes", ignore = true)
    Employee toEntity(EmployeeRequest request);

    /**
     * Convierte una Entity en el DTO de respuesta.
     *
     * @Mapping(target = "fullName", expression = ...)
     *   Le decimos a MapStruct que 'fullName' se construye ejecutando la
     *   expresion Java: e.getFirstName() + " " + e.getLastName().
     *
     * NO existe @Mapping para 'internalNotes' porque el DTO EmployeeResponse
     * simplemente no tiene ese campo -> el dato sensible NUNCA sale.
     *
     * Nota: el parametro se llama 'e' porque asi lo referencia la expression.
     */
    @Mapping(
        target = "fullName",
        expression = "java(e.getFirstName() + \" \" + e.getLastName())"
    )
    EmployeeResponse toResponse(Employee e);
}
