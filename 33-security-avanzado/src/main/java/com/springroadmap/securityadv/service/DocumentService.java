package com.springroadmap.securityadv.service;

import com.springroadmap.securityadv.domain.Document;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Servicio que expone documentos protegidos con Method Security.
 *
 * <p><b>Analogia:</b> cada metodo es una oficina con un guardia propio.
 * El guardia lee la anotacion {@code @PreAuthorize} o {@code @PostAuthorize}
 * antes o despues de ejecutar el metodo para decidir si tienes permiso.</p>
 *
 * <p><b>Palabras clave:</b>
 * <ul>
 *   <li>{@code @Service} — bean de logica de negocio.</li>
 *   <li>{@code @PreAuthorize("expr")} — evalua Spring EL ANTES de ejecutar
 *       el metodo. Si es falso, lanza {@code AccessDeniedException}.</li>
 *   <li>{@code @PostAuthorize("expr")} — evalua Spring EL DESPUES de
 *       ejecutar el metodo. Puede consultar {@code returnObject}
 *       (el valor devuelto) para tomar decisiones sobre el propio objeto.
 *       Sirve para <i>object-level security</i>: "solo puedes ver este
 *       documento si eres su dueño".</li>
 *   <li>{@code hasRole('ADMIN')} — verdadero si el usuario autenticado
 *       tiene la autoridad {@code ROLE_ADMIN}. Spring agrega el prefijo
 *       {@code ROLE_} automaticamente en {@code hasRole}.</li>
 *   <li>{@code authentication.name} — nombre del usuario autenticado
 *       (username) tal como esta en el {@code SecurityContext}.</li>
 * </ul>
 * </p>
 *
 * <h3>ANTES (Java 8, Security 5) vs AHORA (Java 21, Security 7)</h3>
 * <pre>
 * // ANTES: verificacion manual dentro del metodo
 * //   public Document findById(Long id) {
 * //       Document doc = docs.get(id);
 * //       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 * //       if (!doc.getOwner().equals(auth.getName())) {
 * //           throw new AccessDeniedException("nope");
 * //       }
 * //       return doc;
 * //   }
 *
 * // AHORA: una anotacion declarativa
 * //   @PostAuthorize("returnObject.owner == authentication.name")
 * //   public Document findById(Long id) { return docs.get(id); }
 * </pre>
 */
@Service
public class DocumentService {

    /**
     * Almacen in-memory.
     *
     * <p>Nota Java 21: {@code Map.of(...)} devuelve un mapa INMUTABLE.
     * Aqui usamos un {@link HashMap} porque queremos poder borrar en
     * {@link #deleteById(Long)}.</p>
     */
    private final Map<Long, Document> docs = new HashMap<>();

    /**
     * Constructor — carga los 2 documentos de demo.
     *
     * <p>Uno pertenece a "admin" y otro a "user" para poder demostrar
     * la seguridad a nivel de objeto (object-level security).</p>
     */
    public DocumentService() {
        // "new Document(...)" invoca el constructor canonico del record.
        docs.put(1L, new Document(1L, "Manual del administrador", "admin"));
        docs.put(2L, new Document(2L, "Notas personales de user",  "user"));
    }

    /**
     * Lista todos los documentos.
     *
     * <p>Autorizacion: cualquier autenticado con {@code USER} o {@code ADMIN}.</p>
     *
     * <p>PREGUNTA DE ALUMNO — "¿por que no filtro por owner en findAll?"</p>
     * <p>Este metodo es de demo listado. En un caso real filtrarias en
     * el propio query. Aqui la meta pedagogica es mostrar el uso de
     * roles combinados con {@code or}.</p>
     */
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Document> findAll() {
        // List.copyOf devuelve una lista INMUTABLE (Java 10+).
        return List.copyOf(docs.values());
    }

    /**
     * Busca un documento por id.
     *
     * <p>Autorizacion: {@code @PostAuthorize} exige que el owner del
     * documento devuelto sea el usuario autenticado. Si no coincide,
     * Spring lanza {@code AccessDeniedException} y el
     * {@code AccessDeniedHandler} responde 403.</p>
     *
     * <p>Esto es OBJECT-LEVEL SECURITY: la regla depende de datos del
     * objeto devuelto, no solo del rol.</p>
     */
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document findById(Long id) {
        Document doc = docs.get(id);
        if (doc == null) {
            // Optional.orElseThrow es la version moderna; aqui usamos un
            // check explicito para mantenerlo simple para el alumno.
            throw new NoSuchElementException("Documento no encontrado: " + id);
        }
        return doc;
    }

    /**
     * Borra un documento por id.
     *
     * <p>Autorizacion: solo {@code ADMIN}. Cualquier otro rol recibe 403.</p>
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        docs.remove(id);
    }
}
