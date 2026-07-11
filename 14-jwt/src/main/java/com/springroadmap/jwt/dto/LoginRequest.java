package com.springroadmap.jwt.dto;

/**
 * DTO de entrada para POST /api/auth/login.
 *
 * 'record' (Java 14+, estable en 21) es una clase inmutable con constructor,
 * getters (username(), password()), equals, hashCode y toString generados.
 *
 * ANTES (Java 8) — POJO manual:
 *   public class LoginRequest {
 *       private final String username;
 *       private final String password;
 *       public LoginRequest(String u, String p) { this.username=u; this.password=p; }
 *       public String getUsername() { return username; }
 *       public String getPassword() { return password; }
 *       // ... equals, hashCode, toString a mano ...
 *   }
 *
 * AHORA (Java 21) — una linea:
 *   public record LoginRequest(String username, String password) {}
 */
public record LoginRequest(String username, String password) {
}
