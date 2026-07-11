package com.springroadmap.jwt.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests UNITARIOS puros del JwtService (sin cargar contexto de Spring).
 * Rapidos y aislados: instancian el servicio con 'new'.
 */
class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_returnsNonEmptyString() {
        final String token = jwtService.generateToken("alice");
        assertNotNull(token, "el token no debe ser null");
        assertTrue(token.length() > 0, "el token debe ser no vacio");
        // Un JWT tiene tres partes separadas por punto: header.payload.signature
        assertEquals(3, token.split("\\.").length, "el JWT debe tener 3 partes");
    }

    @Test
    void validateAndExtractUsername_returnsSameUsername() {
        final String token = jwtService.generateToken("bob");
        final String username = jwtService.validateAndExtractUsername(token);
        assertEquals("bob", username);
    }

    @Test
    void validateAndExtractUsername_throwsForInvalidToken() {
        // Token completamente invalido: debe lanzar excepcion (jjwt propaga JwtException).
        assertThrows(Exception.class,
                () -> jwtService.validateAndExtractUsername("no-es-un-token-valido"));
    }
}
