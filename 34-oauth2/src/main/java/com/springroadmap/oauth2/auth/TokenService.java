package com.springroadmap.oauth2.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.springroadmap.oauth2.config.SecurityConfig;

/**
 * Servicio que EMITE tokens JWT firmados con HMAC HS256.
 *
 * Analogía:
 *   Es la "oficina de pasaportes" del edificio. Recibe tu identidad, imprime un
 *   pasaporte (JWT) con tu nombre, fecha de expiración y permisos (scope), y lo
 *   sella con la clave HMAC. El guardia (SecurityConfig#jwtDecoder) verifica el
 *   mismo sello cuando lo presentes.
 *
 * ANTES (sesión + cookie):
 *   El servidor guardaba el usuario en HttpSession y devolvía JSESSIONID en cookie.
 *   Cada request re-visitaba la sesión en memoria/Redis.
 * AHORA (JWT stateless):
 *   El servidor no guarda nada. El cliente lleva el token; el servidor solo verifica
 *   la firma. Escalable horizontal sin sticky sessions.
 *
 * PREGUNTA DE ALUMNO — "¿Y si roban el token?"
 *   Válido hasta que expire. Por eso se usa TTL corto (aquí 1 hora) + refresh tokens
 *   + HTTPS obligatorio. Nunca guardar JWT en localStorage sin CSP.
 */
@Service
public class TokenService {

    /**
     * Genera un JWT firmado (HS256) con:
     *   - subject (`sub`) = username recibido
     *   - issuer (`iss`)  = "spring-roadmap-oauth2-demo"
     *   - claim `scope`   = "read" (permiso de ejemplo)
     *   - iat / exp       = ahora / +1 hora
     *
     * `String` es inmutable en Java: cada operación devuelve una nueva instancia.
     */
    public String generateToken(String username) {
        try {
            // Construye el conjunto de claims (payload del JWT).
            Instant now = Instant.now(); // reloj UTC actual, precisión de nanosegundos.
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issuer("spring-roadmap-oauth2-demo")
                    .claim("scope", "read")
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(1, ChronoUnit.HOURS)))
                    .build();

            // Encabezado JOSE: algoritmo HS256.
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            // Objeto que combina header + claims y todavía no está firmado.
            SignedJWT signedJWT = new SignedJWT(header, claims);

            // Firma con la misma clave HMAC que el JwtDecoder valida.
            byte[] keyBytes = SecurityConfig.HMAC_SECRET
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "HmacSHA256");
            signedJWT.sign(new MACSigner(key.getEncoded()));

            // Serializa en formato compacto `header.payload.signature` (Base64URL).
            return signedJWT.serialize();
        } catch (JOSEException e) {
            // Envolvemos la excepción checked en una runtime para no ensuciar el controller.
            throw new IllegalStateException("No se pudo firmar el JWT", e);
        }
    }
}
