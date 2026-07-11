package com.springroadmap.oauth2.web;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints protegidos: requieren JWT válido en `Authorization: Bearer ...`.
 *
 * Analogía: cualquier oficina detrás del guardia. Necesitas mostrar el pasaporte.
 *
 * PREGUNTA DE ALUMNO — "¿Cómo obtengo el usuario dentro del método?"
 *   Con `@AuthenticationPrincipal Jwt jwt`. El objeto Jwt es el token ya
 *   decodificado (claims accesibles con `.getSubject()`, `.getClaim("scope")`, etc.).
 *
 * ANTES (Spring Security 5 con sesión):
 *   SecurityContextHolder.getContext().getAuthentication().getName();  // hilo local
 * AHORA (JWT stateless):
 *   Se inyecta el Jwt en el método; sin ThreadLocal implícito en tu código.
 */
@RestController
@RequestMapping("/api")
public class PrivateController {

    /**
     * GET /api/me -> devuelve el subject (username) y el scope del JWT.
     * Si el token falta o es inválido, Spring Security responde 401 antes de entrar aquí.
     */
    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "username", jwt.getSubject(),
                "scope", jwt.getClaimAsString("scope"),
                "issuer", jwt.getClaimAsString("iss"));
    }
}
