package com.springroadmap.oauth2.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad OAuth2 Resource Server con JWT firmado en HS256.
 *
 * Analogía:
 *   Este archivo es el "reglamento del edificio". Le dice al guardia (Spring
 *   Security) qué puertas son libres (/api/public, /api/auth/token) y en cuáles
 *   exige pasaporte JWT. Además define con qué "sello" (clave secreta) valida
 *   la autenticidad de los pasaportes.
 *
 * ANTES (Spring Security 5, sesión + cookie):
 *   http.formLogin().and().httpBasic();  // guardaba estado del usuario en la sesión HTTP
 *
 * AHORA (Spring Security 6/7 + Boot 4.1, stateless con JWT):
 *   http.oauth2ResourceServer(oauth2 -> oauth2.jwt(...));
 *   No hay sesión: cada request lleva su propio token firmado (JWT).
 *
 * PREGUNTA DE ALUMNO — "¿Por qué la clave está hardcodeada?"
 *   Solo para demo. En producción viene de vault/env var y se rota. Al final del
 *   README hay un apartado sobre Keycloak/Auth0/Cognito.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Clave HMAC compartida. En HS256 el mismo secreto firma y verifica.
     * Debe tener >= 32 bytes (256 bits) para HS256, si no Nimbus lanza excepción.
     * `static final` = constante de clase, inmutable, una sola copia en memoria.
     */
    public static final String HMAC_SECRET = "demo-secret-key-please-change-in-prod-1234567890";

    /**
     * Define la cadena de filtros de seguridad para TODAS las peticiones HTTP.
     *
     * `@Bean` = Spring guarda este objeto en su contenedor para inyectarlo donde haga falta.
     * El parámetro `HttpSecurity http` lo inyecta Spring automáticamente.
     *
     * Lambda `authz -> authz. ...`:
     *   ANTES (Java 8 con clase anónima):
     *     http.authorizeHttpRequests(new Customizer<...>() {
     *         public void customize(AuthorizeHttpRequestsConfigurer<...>.Registry authz) {
     *             authz.requestMatchers("/api/public/**").permitAll()
     *                  .anyRequest().authenticated();
     *         }
     *     });
     *   AHORA (Java 21, lambda):
     *     http.authorizeHttpRequests(authz -> authz
     *         .requestMatchers("/api/public/**").permitAll()
     *         .anyRequest().authenticated());
     *
     * `throws Exception` = Spring exige declararla porque HttpSecurity la lanza.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF innecesario en API stateless con JWT (no hay sesión ni cookie de auth).
            .csrf(csrf -> csrf.disable())
            // STATELESS = no crear ni usar HttpSession. Cada petición se autentica sola con el JWT.
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()      // rutas abiertas
                .requestMatchers("/api/auth/**").permitAll()        // el endpoint que emite tokens
                .anyRequest().authenticated())                      // todo lo demás requiere JWT válido
            // Activa el resource server con validación JWT. Usará el bean JwtDecoder de abajo.
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // config por defecto: extrae y valida
        return http.build();
    }

    /**
     * Decodificador de JWT: recibe el token del header `Authorization: Bearer ...`,
     * verifica firma y expiración, y devuelve un objeto Jwt con los claims.
     *
     * Aquí usamos HMAC HS256 con la misma clave que firma en TokenService.
     * `SecretKeySpec` envuelve los bytes de la clave con el algoritmo HmacSHA256.
     *
     * PREGUNTA DE ALUMNO — "¿Por qué NimbusJwtDecoder y no otro?"
     *   Nimbus JOSE JWT es la lib estándar que trae `spring-security-oauth2-jose`.
     *   Spring Security la expone con builders (`.withSecretKey(...)`).
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = HMAC_SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
