package com.springroadmap.oauth2.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint que emite tokens JWT para la demo.
 *
 * Analogía: el mostrador de la oficina de pasaportes.
 * En producción, aquí validarías credenciales contra una BD o un IdP.
 *
 * PREGUNTA DE ALUMNO — "¿Aquí no debería pedir contraseña?"
 *   Sí. Se omite a propósito para no confundir al lector con el flujo del token,
 *   que es el foco del módulo. Al final del README ves cómo Keycloak lo hace real.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Constructor injection: la dependencia es `final` (no cambia tras crearse).
    private final TokenService tokenService;

    // Spring inyecta automáticamente el bean TokenService.
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * POST /api/auth/token?username=alice
     * Retorna { "access_token": "...", "token_type": "Bearer" }.
     *
     * ANTES (Java 8):
     *   Map<String,String> body = new HashMap<>();
     *   body.put("access_token", token);
     *   body.put("token_type", "Bearer");
     *   return body;
     * AHORA (Java 21 con `Map.of` inmutable — introducido en Java 9):
     *   return Map.of("access_token", token, "token_type", "Bearer");
     */
    @PostMapping("/token")
    public Map<String, String> issueToken(@RequestParam String username) {
        String token = tokenService.generateToken(username);
        Map<String, String> body = new HashMap<>();
        body.put("access_token", token);
        body.put("token_type", "Bearer");
        return body;
    }
}
