package com.springroadmap.jwt.dto;

/**
 * DTO de salida para POST /api/auth/login.
 * Contiene solo el JWT emitido. En un sistema real llevaria tambien
 * refreshToken, tipo ("Bearer"), expiresIn, etc.
 *
 * ANTES (Java 8): clase POJO con getter.
 * AHORA (Java 21): record con un solo componente.
 */
public record TokenResponse(String token) {
}
