package com.springroadmap.dtos.repository;

import com.springroadmap.dtos.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para Employee.
 *
 * ANTES (Java 8 + JDBC crudo):
 *   Se escribia DAO con Connection, PreparedStatement, ResultSet... 40+ lineas
 *   solo para insertar una fila y otro tanto para leerla.
 *
 * AHORA (Spring Data JPA):
 *   Al extender JpaRepository&lt;Employee, Long&gt;, Spring genera
 *   automaticamente los metodos save, findById, findAll, deleteById, etc.
 *   Cero codigo, todo tipado.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
