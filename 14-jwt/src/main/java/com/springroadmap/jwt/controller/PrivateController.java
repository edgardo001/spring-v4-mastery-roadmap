package com.springroadmap.jwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint privado que solo responde si el request trae un JWT valido.
 * El SecurityFilterChain rechaza con 401 si el SecurityContext no tiene Authentication.
 *
 * ANALOGIA: Es la puerta VIP. Nadie te pregunta nada aqui adentro; ya te revisaron
 * la pulsera afuera (JwtAuthFilter). Solo consultas quien es el que entro.
 */
@RestController
@RequestMapping("/api")
public class PrivateController {

    /**
     * GET /api/me
     * @param authentication objeto inyectado por Spring Security con el usuario actual.
     * @return el nombre del principal (username incrustado en el JWT).
     *
     * PREGUNTA DE ALUMNO — "¿de donde sale 'authentication' sin @Autowired?"
     *   Spring MVC detecta parametros de tipo Authentication y los inyecta desde
     *   el SecurityContext del hilo actual. Lo llenamos en JwtAuthFilter.
     */
    @GetMapping("/me")
    public String me(final Authentication authentication) {
        // getName() retorna el principal como String, que en nuestro filtro seteamos como username.
        return authentication.getName();
    }
}
