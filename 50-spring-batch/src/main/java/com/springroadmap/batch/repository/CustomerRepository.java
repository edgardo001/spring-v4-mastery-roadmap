package com.springroadmap.batch.repository;

import com.springroadmap.batch.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA de Customer.
 *
 * Analogía: es un "asistente automático" que ya sabe hacer todos los CRUD
 * (save, findById, findAll, count, delete) sin que escribamos una línea de SQL.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - Antes: DAO manual con EntityManager, unas 80 líneas.
 *   - Ahora: una interface que extiende JpaRepository y listo.
 *
 * PREGUNTA DE ALUMNO — "¿cómo puede una interface hacer cosas si no tiene código?"
 *   Spring Data JPA la implementa en tiempo de arranque con un Proxy dinámico.
 *   El proxy genera el SQL a partir del NOMBRE de cada método.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // No hace falta declarar métodos: findAll(), count(), save() vienen gratis.
}
