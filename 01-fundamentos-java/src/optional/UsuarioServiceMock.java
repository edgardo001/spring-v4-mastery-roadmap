package optional;

import records.ClienteDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio SIMULADO (mock) de acceso a datos.
 *
 * En una app real, este servicio pediría los clientes a una base de datos
 * a través de Spring Data JPA. Aquí usamos un HashMap en memoria para
 * enseñar el patrón de uso de Optional, sin depender de una BD.
 *
 * Optional<T> es una "cajita" que puede contener un valor o estar vacía.
 * Sirve para EVITAR devolver `null`, que es la causa #1 del famoso
 * NullPointerException. Al devolver Optional, obligamos al código que
 * consume el método a preguntarse siempre: "¿qué hago si no hay valor?"
 *
 * =====================================================================
 * ANTES (Java 8 clásico con null) vs AHORA (Java 21 con Optional)
 * =====================================================================
 * ANTES — devolver null y esperar que el llamador se acuerde de chequearlo:
 *
 *   public ClienteDto findById(long id) {
 *       return storage.get(id);   // puede ser null
 *   }
 *
 *   // Llamador (fácil de olvidar el null check → NullPointerException en prod):
 *   String name = service.findById(1L).getName();  // 💥 NPE si no existe
 *
 * AHORA — devolver Optional y forzar el chequeo:
 *
 *   public Optional<ClienteDto> findById(long id) {
 *       return Optional.ofNullable(storage.get(id));
 *   }
 *
 *   String name = service.findById(1L)
 *       .map(ClienteDto::name)
 *       .orElse("Desconocido");  // ✅ nunca falla
 */
public class UsuarioServiceMock {

    // "final" en el campo: la referencia al Map no puede cambiar después
    // de asignarse (aunque el contenido del Map sí puede modificarse).
    private final Map<Long, ClienteDto> storage = new HashMap<>();

    /**
     * Constructor: al crear el servicio, precargamos dos clientes de ejemplo.
     * Así los tests tienen datos con los que trabajar.
     */
    public UsuarioServiceMock() {
        storage.put(1L, new ClienteDto("Ada Lovelace", "ada@gmail.com", 36));
        storage.put(2L, new ClienteDto("Alan Turing", "alan@bletchley.uk", 41));
    }

    /**
     * Busca un cliente por su id.
     *
     * Optional.ofNullable(x) devuelve:
     *   - Optional.empty()  si x es null (no había cliente con ese id)
     *   - Optional.of(x)    si x tiene un valor
     * Nunca devolvemos null: siempre un Optional (vacío o con contenido).
     */
    public Optional<ClienteDto> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Devuelve el NOMBRE del cliente si existe, o "Desconocido" si no.
     *
     * ANTES (Java 8 con null check):
     *   ClienteDto c = storage.get(id);
     *   return c != null ? c.getName() : "Desconocido";
     *
     * AHORA (Java 21 con Optional encadenado):
     *   return findById(id)
     *           .map(ClienteDto::name)
     *           .orElse("Desconocido");
     *
     * Encadenamos:
     *   findById(id)             -> Optional<ClienteDto>
     *   .map(ClienteDto::name)   -> Optional<String>  (nombre si el Optional tenía valor)
     *   .orElse("Desconocido")   -> String            (o valor por defecto si estaba vacío)
     *
     * Este es el patrón "safe unwrap": abrimos la cajita solo cuando hay algo,
     * y damos un fallback claro cuando no.
     */
    public String getNameOrDefault(long id) {
        return findById(id)
                .map(ClienteDto::name)
                .orElse("Desconocido");
    }

    /**
     * Devuelve el cliente si existe, o lanza una excepción si no.
     *
     * Es el patrón típico en un servicio Spring cuando el llamador
     * espera que el recurso exista sí o sí. En un @RestController esta
     * excepción la traduciría un @ControllerAdvice a un HTTP 404.
     *
     * ANTES (Java 8):
     *   ClienteDto c = storage.get(id);
     *   if (c == null) throw new IllegalStateException("...");
     *   return c;
     *
     * AHORA (Java 21):
     *   return findById(id).orElseThrow(() -> new IllegalStateException("..."));
     *
     * "() -> new IllegalStateException(...)" es una lambda que crea la
     * excepción SOLO si hace falta lanzarla (evita construirla siempre).
     */
    public ClienteDto getOrThrow(long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalStateException("Cliente " + id + " no existe"));
    }
}
