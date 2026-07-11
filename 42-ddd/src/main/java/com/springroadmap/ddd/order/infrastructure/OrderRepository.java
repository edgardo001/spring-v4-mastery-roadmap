package com.springroadmap.ddd.order.infrastructure;

import com.springroadmap.ddd.order.domain.Order;
import com.springroadmap.ddd.order.domain.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * OrderRepository - repositorio del aggregate Order.
 *
 * ANALOGIA: como el archivero de una oficina. Tu le pides "traeme el pedido X" y el te lo trae
 * de su gaveta (la base de datos). No sabes ni te importa como esta guardado internamente.
 *
 * En DDD un Repository trabaja con AGGREGATES completos (no con entidades sueltas).
 * Aqui, el aggregate root es Order y su ID es OrderId (Value Object).
 *
 * Al extender {@code JpaRepository<Order, OrderId>}, Spring Data JPA nos regala metodos
 * como save, findById, findAll, deleteById... sin escribir codigo.
 *
 * ANTES (Java 8 / Spring clasico):
 *   Se escribia una clase con EntityManager, @PersistenceContext y queries JPQL a mano.
 *
 * AHORA (Spring Data JPA):
 *   Solo se declara la interfaz; Spring genera la implementacion en runtime.
 */
public interface OrderRepository extends JpaRepository<Order, OrderId> {
}
