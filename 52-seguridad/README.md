# 52 — Seguridad (OWASP Top 10 aplicado)

Módulo de hardening OWASP sobre Spring Boot 4.1.0 / Java 21.

- `groupId`: `com.springroadmap`
- `artifactId`: `seguridad-owasp`
- Paquete: `com.springroadmap.owasp`
- Artefacto: `target/seguridad-owasp-1.0.0.jar`

## Build

```bash
./build.sh
```

```powershell
./build.ps1
```

## Contenido

- `SecurityConfig` con `@EnableWebSecurity` y `SecurityFilterChain`:
  - Headers: `Content-Security-Policy: default-src 'self'`, `Referrer-Policy: no-referrer`, `X-Frame-Options: DENY`.
  - CSRF activo para forms; ignorado en `/api/**` (stateless).
- `SearchController` `/api/users/search?email=X` con query **parametrizada** (`findByEmail`).
- `HtmlSanitizerService` basado en `owasp-java-html-sanitizer` (`PolicyFactory`).
- `RateLimitFilter` (`OncePerRequestFilter`) in-memory por IP (100 req/min).

## Antes vs Ahora

### 1. Prevención SQL Injection

**Antes (MALO):** validación manual con regex + concatenación en query nativa.

```java
if (!email.matches("[a-zA-Z0-9@._-]+")) throw new IllegalArgumentException();
@Query(value = "SELECT * FROM users WHERE email = '" + email + "'", nativeQuery = true)
List<User> unsafeSearch(String email);
```

**Ahora (BUENO):** query derivada / parametrizada. El valor viaja como bind param.

```java
List<User> findByEmail(String email); // PreparedStatement bajo el capó
```

### 2. Prevención XSS

**Antes (MALO):** filtrar `<script>` con `replaceAll` casero.

```java
html.replaceAll("(?i)<script.*?>.*?</script>", ""); // evadible con SVG, onerror, etc.
```

**Ahora (BUENO):** `PolicyFactory` de OWASP con whitelist de tags/atributos.

```java
policy.sanitize("<script>alert(1)</script>Hola"); // => "Hola"
```

### 3. Headers de seguridad

**Antes:** nada — defaults permisivos, iframes libres, sin CSP.

**Ahora:** `.headers(h -> h.contentSecurityPolicy(...).referrerPolicy(...).frameOptions(f -> f.deny()))`.

### 4. CSRF

**Antes:** deshabilitado por completo (`.csrf(csrf -> csrf.disable())`).

**Ahora:** activo por defecto para forms; ignorado sólo en `/api/**` (stateless con token).

### 5. Rate limiting

**Antes:** sin protección → brute force libre.

**Ahora:** `RateLimitFilter` corta a 100 req/min por IP (en prod: bucket4j + Redis).

## FAQ Alumno

**P: ¿Por qué desactivo CSRF sólo en `/api/**`?**
R: CSRF protege sesiones basadas en cookies. Las APIs REST stateless usan token en `Authorization`, que el navegador no envía cross-site → CSRF no aplica. Forms server-side usan cookie de sesión → deben mantenerlo.

**P: ¿Regex para validar HTML no basta?**
R: No. HTML no es lenguaje regular; hay decenas de vectores XSS (SVG, `javascript:`, atributos `on*`, entidades). Usa un sanitizer probado — OWASP Java HTML Sanitizer.

**P: ¿`findByEmail` es realmente inmune a SQL injection?**
R: Sí. Spring Data JPA construye un `PreparedStatement` donde el `?` se enlaza con el valor. Aunque `email` valga `' OR '1'='1`, se envía como literal, no como SQL.

**P: ¿Por qué `default-src 'self'` en CSP?**
R: Base estricta: sólo carga recursos del mismo origen. Bloquea `<script src=evil.com>` inyectado. Ajusta añadiendo hosts confiables.

**P: ¿`frameOptions.deny()` no rompe integraciones?**
R: Sí, si necesitas embeder tu app en iframe (widgets). Usa `sameOrigin()` en ese caso. Para máxima seguridad: `deny()`.

**P: ¿El rate limiter in-memory sirve en producción?**
R: Sólo para 1 instancia y como demo. En prod: bucket4j con Redis, o WAF/API gateway.

**P: ¿Por qué no Lombok?**
R: Convención del roadmap: código explícito y portable.
