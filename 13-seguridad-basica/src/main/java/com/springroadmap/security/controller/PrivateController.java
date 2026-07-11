package com.springroadmap.security.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de endpoints protegidos.
 *
 * <p><b>Propósito:</b> demostrar cómo un endpoint puede leer el usuario
 * autenticado a través de {@link Principal}. Sólo se puede acceder tras
 * pasar por Basic Auth con credenciales válidas.</p>
 *
 * <p><b>Analogía:</b> es la sala VIP; el guardia (SecurityFilterChain)
 * ya validó tu credencial y este método simplemente saluda por tu nombre.</p>
 *
 * <p><b>Palabras clave:</b>
 * <ul>
 *   <li>{@code Principal} — interfaz de Java estándar que representa
 *       la identidad del usuario autenticado. Spring la inyecta.</li>
 * </ul>
 * </p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES:
 * //   HttpServletRequest req = ...;
 * //   String user = req.getUserPrincipal().getName();
 * // AHORA:
 * //   Spring inyecta Principal directamente como argumento.
 * </pre>
 */
@RestController
@RequestMapping("/api/private")
public class PrivateController {

    /**
     * Retorna un saludo personalizado al usuario autenticado.
     *
     * @param principal identidad del usuario autenticado (inyectada por Spring)
     * @return {@code "private for " + principal.getName()}
     */
    @GetMapping("/hello")
    public String hello(Principal principal) {
        // principal.getName() → username del usuario autenticado (ej. "admin").
        return "private for " + principal.getName();
    }
}
