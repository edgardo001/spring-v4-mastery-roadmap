package com.springroadmap.dtos.mapper;

import com.springroadmap.dtos.domain.Employee;
import com.springroadmap.dtos.dto.EmployeeRequest;
import com.springroadmap.dtos.dto.EmployeeResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitario del EmployeeMapper.
 *
 * Usa Mappers.getMapper(...) para obtener la implementacion generada por
 * MapStruct sin depender del contexto de Spring. Esto es POSIBLE porque
 * MapStruct genera la clase EmployeeMapperImpl en target/generated-sources.
 *
 * Ventajas del test unitario:
 *   - Ejecuta en milisegundos (no arranca Spring).
 *   - Sirve como documentacion viva del contrato del mapper.
 */
class EmployeeMapperTest {

    // Instancia unica del mapper generado por MapStruct.
    private final EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    void toResponse_concatenaFullName_yOcultaInternalNotes() {
        // Given: una Entity con internalNotes cargados (dato SENSIBLE).
        Employee entity = new Employee(
                42L,
                "Ada",
                "Lovelace",
                new BigDecimal("1000.00"),
                LocalDate.of(2024, 1, 15),
                "TOP SECRET: negocia aumento en Q2"  // <- jamas debe salir al DTO.
        );

        // When: se pide el DTO de respuesta.
        EmployeeResponse response = mapper.toResponse(entity);

        // Then: el fullName se concatena correctamente...
        assertThat(response.id()).isEqualTo(42L);
        assertThat(response.fullName()).isEqualTo("Ada Lovelace");
        assertThat(response.salary()).isEqualByComparingTo("1000.00");
        assertThat(response.hireDate()).isEqualTo(LocalDate.of(2024, 1, 15));

        // ...y NO existe manera de acceder a internalNotes desde el DTO:
        //    EmployeeResponse es un record con solo 4 campos y ninguno
        //    corresponde a internalNotes. Prueba de que la exposicion es imposible.
        //    (Este assert es semantico: si alguien agregara el campo, el compilador
        //     obligaria a mapear y este test fallaria.)
        assertThat(response.getClass().getRecordComponents())
                .extracting("name")
                .containsExactly("id", "fullName", "salary", "hireDate");
    }

    @Test
    void toEntity_mapeaCamposDirectos_yDejaIdEInternalNotesEnNull() {
        EmployeeRequest request = new EmployeeRequest(
                "Grace",
                "Hopper",
                new BigDecimal("2500.00"),
                LocalDate.of(2023, 6, 1)
        );

        Employee entity = mapper.toEntity(request);

        assertThat(entity.getFirstName()).isEqualTo("Grace");
        assertThat(entity.getLastName()).isEqualTo("Hopper");
        assertThat(entity.getSalary()).isEqualByComparingTo("2500.00");
        assertThat(entity.getHireDate()).isEqualTo(LocalDate.of(2023, 6, 1));

        // 'id' e 'internalNotes' quedan null: el cliente no puede setearlos por API.
        assertThat(entity.getId()).isNull();
        assertThat(entity.getInternalNotes()).isNull();
    }
}
