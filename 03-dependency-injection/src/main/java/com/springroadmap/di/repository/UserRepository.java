package com.springroadmap.di.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio SIMULADO de usuarios (Map en memoria).
 *
 * PREGUNTA DE ALUMNO — "¿@Repository es distinto de @Service?"
 *   Ambos son variantes de @Component. La diferencia real está en dos
 *   cosas cuando usas Spring Data / JPA (módulo 07):
 *     1. @Repository TRADUCE excepciones específicas del proveedor de BD
 *        (Hibernate, JDBC) a la jerarquía genérica DataAccessException.
 *     2. Comunica intención de "capa de acceso a datos".
 *   Como este módulo NO usa BD real, aquí @Repository funciona
 *   básicamente como un @Component semánticamente correcto.
 *
 * Analogía: es la "bodega" del restaurante. El chef (@Service) no va
 * personalmente al supermercado: le pide al bodeguero (@Repository) los
 * ingredientes y este los saca del inventario.
 *
 * ANTES (Java 1.8): la clase se llamaría UserDao y se instanciaba a mano.
 * AHORA: la marca @Repository + inyección la hacen automáticamente parte
 * del contenedor.
 */
@Repository
public class UserRepository {

    /** "Base de datos" mock: email -> nombre. */
    private final Map<String, String> storage = new HashMap<>();

    /** Precargamos un usuario de ejemplo en el constructor. */
    public UserRepository() {
        storage.put("ada@example.com", "Ada Lovelace");
    }

    /**
     * Busca el nombre asociado a un email.
     *
     * Devolvemos Optional<String> en vez de null para forzar al Controller
     * a manejar explícitamente el caso "no encontrado" (patrón de módulo 01).
     *
     * @param email correo a buscar
     * @return Optional con el nombre, o vacío si no existe
     */
    public Optional<String> findByEmail(String email) {
        return Optional.ofNullable(storage.get(email));
    }
}
