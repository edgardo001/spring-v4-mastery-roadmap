package com.springroadmap.hexagonal.adapter.out.persistence;

import com.springroadmap.hexagonal.domain.model.Customer;
import com.springroadmap.hexagonal.domain.port.out.CustomerRepositoryPort;
// @Component: le dice a Spring que registre esta clase como bean. Podría ser también
// @Repository (más semántico para persistencia); ambos funcionan.
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
// ConcurrentHashMap: Map thread-safe. En un caso real habría múltiples requests HTTP
// concurrentes escribiendo, así que evitamos HashMap normal (no thread-safe).
import java.util.concurrent.ConcurrentHashMap;
// AtomicLong: contador thread-safe. Simula el 'autoincrement' de una BD real.
import java.util.concurrent.atomic.AtomicLong;

/**
 * ADAPTADOR DE SALIDA (Secondary Adapter / Outbound Adapter).
 *
 * Analogía del mundo real: es el "archivero" del banco. Implementa el "enchufe hembra"
 * que el hexágono definió (CustomerRepositoryPort) y guarda los papeles físicamente.
 * En este ejemplo lo hace en RAM. Mañana podrías reemplazar esta clase por
 * 'JpaCustomerRepository' (con JPA/Hibernate) y el dominio NI SE ENTERARÍA.
 *
 * ¿Por qué en memoria?
 *   - Para que el módulo compile y corra sin depender de una BD real.
 *   - En módulos posteriores (07 JPA, 08 Migraciones) verás el reemplazo real.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   La lógica de un Map + AtomicLong no cambió entre Java 8 y 21. Java 21 nos
 *   da acceso a colecciones inmutables (List.of, Map.of) y a Streams más ergonómicos,
 *   pero para este adaptador el código es idéntico.
 *
 * PREGUNTA DE ALUMNO — "Si mañana uso JPA, ¿tengo que borrar esta clase?"
 *   No. La dejas para tests o para el perfil 'dev'. Creas otra clase
 *   'JpaCustomerRepository' que también implemente CustomerRepositoryPort y anotas
 *   una con @Profile("dev") y otra con @Profile("prod"). Spring elige según el perfil.
 */
@Component
public class InMemoryCustomerRepository implements CustomerRepositoryPort {

    // Mapa id -> Customer. 'final' porque la referencia no cambia (el contenido sí).
    private final Map<Long, Customer> storage = new ConcurrentHashMap<>();
    // Generador de ids. Empieza en 0; el primer incrementAndGet() devolverá 1.
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public Customer save(Customer customer) {
        // Si el customer viene sin id (null), asignamos uno nuevo (autoincrement simulado).
        // Si viene con id (update), lo respetamos.
        Long id = customer.id() != null ? customer.id() : idSequence.incrementAndGet();
        // Como Customer es un record inmutable, no podemos "setId". Creamos uno nuevo
        // con el id asignado.
        Customer persisted = new Customer(id, customer.name(), customer.email());
        storage.put(id, persisted);
        return persisted;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        // Optional.ofNullable: envuelve el valor; si es null, devuelve Optional.empty().
        return Optional.ofNullable(storage.get(id));
    }
}
