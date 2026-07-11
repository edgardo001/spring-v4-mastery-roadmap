package com.springroadmap.vslice.shared;

// BigDecimal: numero decimal exacto (evita errores de redondeo de double).
import java.math.BigDecimal;
// ArrayList/List: coleccion ordenada. Se usa para devolver copias defensivas.
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
// ConcurrentHashMap: mapa thread-safe (varios hilos HTTP simultaneos no lo rompen).
import java.util.concurrent.ConcurrentHashMap;
// AtomicLong: contador incrementable de forma segura entre hilos.
import java.util.concurrent.atomic.AtomicLong;

// @Component: marca la clase como bean gestionado por Spring (se crea 1 instancia
// singleton y se inyecta en cualquier constructor que la pida).
import org.springframework.stereotype.Component;

/**
 * Almacen en memoria compartido por todas las features.
 *
 * <p><b>Analogia:</b> es la "bodega central" del supermercado. Cada estacion
 * (feature) usa la misma bodega, pero cada una accede solo a lo que necesita.
 * En un caso real esto seria un Repository JPA; aqui es un mapa concurrente
 * para mantener el foco en la arquitectura, no en la persistencia.</p>
 *
 * <p><b>Vertical Slice — regla clave:</b> el store vive en {@code shared/}
 * porque de verdad lo comparten las 3 features. Lo demas NO se comparte
 * (cada feature tiene su propio Command/Response/Handler/Endpoint).</p>
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b>
 * <pre>
 *   // ANTES: clase interna "StoredOrder" con getters/setters, ~40 lineas.
 *   public static class StoredOrder {
 *       private final Long id; ...
 *       public Long getId() { return id; } ...
 *   }
 *   // AHORA: record de 1 linea equivalente e inmutable.
 *   public record StoredOrder(Long id, String customer, BigDecimal amount, String status) {}
 * </pre></p>
 */
@Component
public class OrderStore {

    /**
     * Record interno que representa una orden guardada.
     * Un record es una clase inmutable con constructor, getters, equals,
     * hashCode y toString generados por el compilador.
     */
    public record StoredOrder(Long id, String customer, BigDecimal amount, String status) {}

    // Mapa concurrente id -> StoredOrder. Final => la referencia no cambia.
    private final ConcurrentHashMap<Long, StoredOrder> data = new ConcurrentHashMap<>();
    // Generador de ids auto-incremental thread-safe.
    private final AtomicLong sequence = new AtomicLong(0);

    /**
     * Guarda una nueva orden y devuelve el id asignado.
     * @param customer nombre del cliente
     * @param amount monto
     * @param status estado inicial (ej. "CREATED")
     * @return la orden almacenada con id ya asignado
     */
    public StoredOrder save(String customer, BigDecimal amount, String status) {
        // incrementAndGet: suma 1 y devuelve el nuevo valor, atomicamente.
        long id = sequence.incrementAndGet();
        StoredOrder order = new StoredOrder(id, customer, amount, status);
        data.put(id, order);
        return order;
    }

    /**
     * Busca por id. Devuelve Optional para forzar al llamador a considerar
     * el caso "no existe" (evita NullPointerException).
     *
     * <p>ANTES (Java 8): {@code return data.get(id);} y el que llama debia
     * hacer {@code if (result != null)}. AHORA (Java 21): {@code Optional.ofNullable(...)}
     * hace explicito que el valor puede faltar.</p>
     */
    public Optional<StoredOrder> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    /**
     * Devuelve todas las ordenes filtradas por status (si status es null,
     * devuelve todas). Retorna una copia defensiva para que nadie mute el mapa.
     */
    public List<StoredOrder> findAll(String statusFilter) {
        List<StoredOrder> result = new ArrayList<>();
        // values(): coleccion de valores del mapa; se itera con for-each.
        for (StoredOrder o : data.values()) {
            if (statusFilter == null || statusFilter.isBlank() || statusFilter.equals(o.status())) {
                result.add(o);
            }
        }
        return result;
    }
}
