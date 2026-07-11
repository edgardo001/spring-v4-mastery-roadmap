package com.springroadmap.jwt.controller;

import com.springroadmap.jwt.dto.LoginRequest;
import com.springroadmap.jwt.dto.TokenResponse;
import com.springroadmap.jwt.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint publico para el login.
 * Recibe credenciales y devuelve un JWT si son correctas.
 *
 * SEGURIDAD DEL DEMO:
 *   Aqui las credenciales estan HARDCODED (admin / admin123). En produccion,
 *   consultarias un UserDetailsService que busca en base de datos y compara
 *   contrasenas con BCryptPasswordEncoder.
 *
 * ANALOGIA: Es la ventanilla de emision de credenciales.
 *   - Muestras tu carnet (username+password).
 *   - Si eres quien dices ser, te entregan una pulsera (JWT) firmada por la casa.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Credenciales unicas aceptadas en este demo. NO usar este patron en produccion.
    private static final String DEMO_USER = "admin";
    private static final String DEMO_PASS = "admin123";

    private final JwtService jwtService;

    public AuthController(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * POST /api/auth/login
     *
     * @param request record con username y password
     * @return 200 + { "token": "..." } si las credenciales coinciden; 401 si no.
     *
     * PREGUNTA DE ALUMNO — "¿que hace @RequestBody?"
     *   Le dice a Spring: "toma el JSON del cuerpo del request y conviertelo en un
     *   objeto Java de este tipo". Jackson hace la deserializacion automatica.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginRequest request) {
        // Comparacion simple. En produccion: passwordEncoder.matches(raw, hash).
        if (DEMO_USER.equals(request.username()) && DEMO_PASS.equals(request.password())) {
            final String token = jwtService.generateToken(request.username());
            return ResponseEntity.ok(new TokenResponse(token));
        }
        // 401 Unauthorized sin cuerpo si las credenciales son invalidas.
        return ResponseEntity.status(401).build();
    }
}
