package com.springroadmap.dtos.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad JPA que representa la tabla 'employees' en la base de datos.
 *
 * IMPORTANTE (regla del roadmap):
 *   Esta clase NUNCA debe salir del backend hacia el cliente REST.
 *   Contiene un campo sensible 'internalNotes' que el frontend jamas
 *   deberia ver. Para eso existe EmployeeResponse (el DTO).
 *
 * Analogia del mundo real:
 *   La Entity es el "expediente medico completo" del empleado guardado
 *   en el archivador (BD). El DTO es la "credencial visible" que le entregas
 *   a un tercero: contiene solo el nombre y datos publicos.
 *
 * ANTES (Java 8):
 *   - Clase con getters/setters manuales o generados por Lombok.
 *   - Fecha con java.util.Date (con problemas de zona horaria).
 *   - Dinero con double/float (con errores de redondeo).
 *
 * AHORA (Java 21):
 *   - Sigue siendo una clase mutable porque JPA necesita setters.
 *   - java.time.LocalDate para fechas.
 *   - java.math.BigDecimal para dinero (precision exacta).
 *
 * PREGUNTA DE ALUMNO - "por que no usar un record en vez de una clase?"
 *   JPA/Hibernate necesita un constructor sin argumentos y setters para
 *   poder instanciar la entidad al leer un ResultSet. Los records son
 *   inmutables y no permiten setters. Por eso Entity = clase mutable,
 *   y DTO = record.
 */
@Entity
@Table(name = "employees")
public class Employee {

    // @Id marca la clave primaria. @GeneratedValue delega el auto-incremento a la BD.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    // BigDecimal para dinero. NUNCA usar double para valores monetarios
    // (double 0.1 + 0.2 = 0.30000000000000004).
    private BigDecimal salary;

    private LocalDate hireDate;

    /**
     * Campo SENSIBLE. Contiene notas internas del RRHH sobre el empleado
     * (evaluaciones, comentarios de jefes). No debe exponerse jamas al API publico.
     * El EmployeeMapper lo IGNORA explicitamente al construir EmployeeResponse.
     */
    private String internalNotes;

    // Constructor sin argumentos - REQUERIDO por JPA (Hibernate lo usa por reflexion).
    public Employee() {
    }

    // Constructor conveniente para tests unitarios (no lo usa JPA).
    public Employee(Long id, String firstName, String lastName,
                    BigDecimal salary, LocalDate hireDate, String internalNotes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.hireDate = hireDate;
        this.internalNotes = internalNotes;
    }

    // ---- Getters y Setters ----
    // Necesarios porque MapStruct y JPA los invocan.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
}
