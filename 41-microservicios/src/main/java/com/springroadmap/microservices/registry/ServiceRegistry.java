package com.springroadmap.microservices.registry;

// Imports basicos: colecciones, concurrencia, y estereotipo Spring.
import java.util.ArrayList;                       // Lista dinamica clasica (mutable).
import java.util.Collections;                     // Utilidades como unmodifiableMap.
import java.util.List;                            // Interfaz para listas.
import java.util.Map;                             // Interfaz clave-valor.
import java.util.concurrent.ConcurrentHashMap;    // HashMap seguro entre hilos.
import java.util.concurrent.atomic.AtomicInteger; // Contador atomico (multi-hilo).
import org.springframework.stereotype.Component;  // Anotacion Spring -> bean singleton.

/**
 * ServiceRegistry: registro in-memory de microservicios.
 *
 * ANALOGIA:
 *   Es el "directorio telefonico amarillo" del centro comercial. Cada tienda
 *   (microservicio) se registra con su nombre y su ubicacion (URL). Cuando
 *   alguien pregunta "donde queda pagos?", el directorio devuelve TODAS las
 *   sucursales conocidas y el que llama elige (Round-Robin).
 *
 * NOTA PEDAGOGICA:
 *   En produccion NO usarias esto: usarias Eureka, Consul o el DNS interno de
 *   Kubernetes. Aca lo hacemos a mano para que se entienda que "descubrir un
 *   servicio" es al final del dia un Map<nombre, List<URL>>.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *
 *   ANTES (Java 8):
 *     List<String> urls = services.get(name);
 *     if (urls == null) { urls = new ArrayList<String>(); services.put(name, urls); }
 *     urls.add(url);
 *
 *   AHORA (Java 21):
 *     services.computeIfAbsent(name, k -> new ArrayList<>()).add(url);
 *     // 'k -> new ArrayList<>()' es una LAMBDA: crea la lista solo si la clave no existe.
 *     // '<>' se llama diamond operator: la JVM infiere el tipo generico.
 */
@Component // Spring crea UNA sola instancia (singleton) y la inyecta donde se pida.
public class ServiceRegistry {

    /**
     * Mapa nombre-servicio -> lista de URLs registradas.
     *   'final' -> la referencia no se puede reasignar (el Map de adentro si muta).
     *   ConcurrentHashMap -> permite lecturas/escrituras concurrentes sin sincronizar.
     */
    private final Map<String, List<String>> services = new ConcurrentHashMap<>();

    /**
     * Contadores por servicio para el Round-Robin. Cada servicio tiene su propio
     * indice atomico para que dos hilos que preguntan simultaneamente reciban
     * URLs diferentes sin condicion de carrera.
     */
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    /**
     * Registra una URL bajo un nombre de servicio.
     * Si el servicio no existe, lo crea. Si ya existe, agrega otra URL a la lista
     * (multiples instancias del mismo microservicio, tipico en produccion).
     */
    public void register(String name, String url) {
        // PREGUNTA DE ALUMNO — "y si registro la misma URL dos veces?"
        // Respuesta: aca la agregamos igual; en Eureka real se deduplica por instanceId.
        services.computeIfAbsent(name, k -> new ArrayList<>()).add(url);
        counters.computeIfAbsent(name, k -> new AtomicInteger(0));
    }

    /**
     * Devuelve TODOS los servicios registrados como un mapa inmutable (para que
     * el controller no pueda mutarlo por accidente al exponerlo por JSON).
     */
    public Map<String, List<String>> listAll() {
        return Collections.unmodifiableMap(services);
    }

    /**
     * Devuelve las URLs de un servicio (o lista vacia si no existe).
     */
    public List<String> getUrls(String name) {
        // 'Map.getOrDefault' evita el clasico null-check de Java 8.
        return services.getOrDefault(name, List.of()); // List.of() = lista inmutable vacia.
    }

    /**
     * Elige la siguiente URL para un servicio usando Round-Robin.
     * Devuelve null si el servicio no esta registrado o no tiene URLs.
     *
     * ANTES (Java 8):
     *   int idx = counter++ % urls.size();  // NO es thread-safe.
     *
     * AHORA (Java 21):
     *   int idx = counter.getAndIncrement() % urls.size();  // atomico.
     */
    public String nextUrl(String name) {
        List<String> urls = services.get(name); // Puede ser null si no existe.
        if (urls == null || urls.isEmpty()) {
            return null; // El caller (Gateway) traducira esto a 404.
        }
        AtomicInteger counter = counters.get(name);
        int idx = Math.floorMod(counter.getAndIncrement(), urls.size());
        // 'Math.floorMod' garantiza indice positivo incluso si el contador desborda.
        return urls.get(idx);
    }
}
