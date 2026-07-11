package com.springroadmap.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Servicio de emision y verificacion de JSON Web Tokens.
 *
 * PROPOSITO:
 *   - generateToken(username): crea un token firmado con HMAC-SHA256 que "sella"
 *     el nombre del usuario y una fecha de expiracion.
 *   - validateAndExtractUsername(token): verifica la firma y devuelve el username
 *     que venia dentro. Si el token es invalido o expiro, jjwt lanza una excepcion.
 *
 * ANALOGIA: Un JWT es como una pulsera de un festival:
 *   - En la entrada (login) te la ponen y la marcan con un sello especial (firma).
 *   - En cada puesto (endpoint) miran solo la pulsera: si el sello coincide, pasas.
 *   - Nadie necesita llamar a la boleteria porque la pulsera se autovalida.
 *
 * ADVERTENCIA DE SEGURIDAD:
 *   El 'secret' esta HARDCODED aqui SOLO para fines didacticos. En produccion
 *   NUNCA se hace esto: se inyecta desde una variable de entorno, un secret manager
 *   (Vault, AWS Secrets Manager) o un archivo cifrado. Ademas, en produccion el
 *   token debe tener expiracion corta (10-15 min) y se acompana de un refresh token.
 */
@Service
public class JwtService {

    // Clave secreta de 256 bits en base64. HS256 requiere >=256 bits.
    // Esta cadena base64 decodifica a 32 bytes exactos.
    private static final String SECRET_BASE64 =
            "ZGVtby1zZWNyZXQta2V5LTMyLWJ5dGVzLWZvci1obWFjLXNoYTI1Ni1va2F5IQ==";

    // Duracion del token: 1 hora en milisegundos (60 * 60 * 1000).
    private static final long EXPIRATION_MS = 3_600_000L;

    // 'final' = una vez asignada, la referencia no puede cambiar.
    private final SecretKey key;

    /**
     * Constructor: convierte el string base64 en una SecretKey utilizable por jjwt.
     * Keys.hmacShaKeyFor exige un array de bytes de al menos 32 posiciones para HS256.
     */
    public JwtService() {
        // Base64.getDecoder() decodifica la cadena a bytes crudos (32 bytes).
        final byte[] keyBytes = Base64.getDecoder().decode(SECRET_BASE64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Emite un JWT firmado. El 'subject' del token guarda el username.
     *
     * ANTES (Java 8, jjwt 0.9):
     *   Jwts.builder()
     *       .setSubject(username)
     *       .setIssuedAt(new Date())
     *       .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
     *       .signWith(SignatureAlgorithm.HS256, secretBytes)
     *       .compact();
     *
     * AHORA (Java 21, jjwt 0.12+):
     *   Los setters legacy fueron eliminados; se usan 'subject()', 'issuedAt()',
     *   'expiration()', y 'signWith(SecretKey)' infiere el algoritmo desde la clave.
     */
    public String generateToken(final String username) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .subject(username)     // el 'sub' del payload guarda el nombre del usuario
                .issuedAt(now)         // 'iat' = issued at
                .expiration(expiration) // 'exp' = expiration
                .signWith(key)         // firma HMAC-SHA256 con nuestra clave secreta
                .compact();            // serializa a la cadena base64url final "xxx.yyy.zzz"
    }

    /**
     * Verifica el token y devuelve el username incrustado en el 'subject'.
     * Si el token esta manipulado, mal firmado o expirado, jjwt lanza una excepcion
     * (subtipo de JwtException). Dejamos que la excepcion PROPAGUE para que el filtro
     * la capture y responda 401.
     */
    public String validateAndExtractUsername(final String token) {
        // parser() -> builder de parseador. verifyWith(key) exige que la firma coincida.
        // parseSignedClaims(token).getPayload() devuelve los Claims (payload verificado).
        final Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // getSubject() retorna el valor del claim 'sub' (nuestro username).
        return claims.getSubject();
    }
}
