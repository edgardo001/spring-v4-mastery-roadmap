package com.springroadmap.jwt.security;

import com.springroadmap.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro que se ejecuta UNA VEZ por cada request HTTP y realiza:
 *   1. Lee el header Authorization.
 *   2. Si empieza por "Bearer ", extrae el token.
 *   3. Valida el token con JwtService.
 *   4. Si es valido, coloca un Authentication en el SecurityContext para que
 *      Spring Security considere al usuario autenticado en esta request.
 *
 * ANALOGIA: Es el portero del edificio que revisa la credencial al entrar.
 *   - Si la credencial esta bien, te deja pasar y anota tu nombre en la lista del dia.
 *   - Si no, no hace nada: el siguiente control (el firewall) te rechaza.
 *
 * OncePerRequestFilter (Spring) garantiza que aunque el request sea forwardeado
 * internamente, este filtro solo corre una vez por request.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // Prefijo estandar del header segun RFC 6750.
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    // Constructor injection: Spring pasa el JwtService al crear este bean.
    public JwtAuthFilter(final JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * doFilterInternal es la logica del filtro. Se invoca por cada request.
     * Debemos SIEMPRE llamar a chain.doFilter al final para no romper la cadena.
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain)
            throws ServletException, IOException {

        // 1) Leer el header Authorization (puede venir null).
        final String header = request.getHeader("Authorization");

        // 2) Solo procesar si viene con el prefijo Bearer.
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            // substring(7) salta el "Bearer " (7 caracteres) y deja solo el token.
            final String token = header.substring(BEARER_PREFIX.length());

            try {
                // 3) Validar y extraer username. Si el token es invalido lanza excepcion.
                final String username = jwtService.validateAndExtractUsername(token);

                // 4) Construir el Authentication de Spring Security.
                //    - principal = username (String simple para este demo).
                //    - credentials = null (ya validamos con la firma; no hay password).
                //    - authorities = lista vacia (este demo no maneja roles).
                final UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList());

                // Depositamos el Authentication en el contexto de seguridad de este hilo.
                // A partir de aqui, @AuthenticationPrincipal y controllers ven al usuario.
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (final Exception ex) {
                // Token invalido: NO llenamos el contexto. La request seguira su curso
                // y sera rechazada por el SecurityFilterChain con 401 si el endpoint
                // requiere autenticacion. Limpiamos por si acaso.
                SecurityContextHolder.clearContext();
            }
        }

        // Continuar la cadena de filtros SIEMPRE. Sin esto, la request se cuelga.
        chain.doFilter(request, response);
    }
}
