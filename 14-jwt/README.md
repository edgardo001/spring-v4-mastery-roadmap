# 14 — JWT (JSON Web Tokens)

## Proposito
Aprender autenticacion **stateless** en Spring Boot 4 usando JWT firmados con HMAC-SHA256 (libreria `jjwt` 0.12+). El servidor emite un token al hacer login y valida ese token en cada request protegido, sin usar sesiones ni cookies.

## Problema que resuelve
En el modulo 13 (`13-seguridad-basica`) usamos sesiones HTTP: el servidor guardaba estado del usuario en memoria y el cliente arrastraba una cookie `JSESSIONID`. Ese modelo **no escala** en arquitecturas modernas:
- Con multiples instancias detras de un balanceador, cada nodo tendria su propia sesion (necesitas sticky sessions o un store compartido tipo Redis).
- Para APIs consumidas por SPAs, apps moviles o microservicios, el manejo de cookies es incomodo y sufre problemas de CORS/CSRF.
- Las sesiones acoplan al cliente con un servidor especifico.

## Como lo resuelve
JWT convierte al **token en la fuente de verdad**:
1. El cliente hace login con usuario/contrasena.
2. El servidor emite un JWT firmado que contiene el username y una fecha de expiracion.
3. El cliente guarda el token (localStorage, cookie httpOnly, memoria).
4. En cada request envia `Authorization: Bearer <token>`.
5. Un filtro (`JwtAuthFilter`) valida la firma y coloca al usuario en el `SecurityContext`.
6. El servidor **no guarda nada** entre requests: el token se autovalida.

## Por que aprenderlo
Es el estandar de-facto para autenticacion en APIs REST desde 2015. Lo veras en todas partes: Auth0, Cognito, Keycloak, Firebase Auth, y en el 90% de los microservicios empresariales.

```mermaid
sequenceDiagram
    autonumber
    participant C as Cliente
    participant A as AuthController
    participant J as JwtService
    participant F as JwtAuthFilter
    participant P as PrivateController
    C->>A: POST /api/auth/login {admin, admin123}
    A->>J: generateToken("admin")
    J-->>A: eyJhbGciOi...
    A-->>C: 200 { token }
    C->>F: GET /api/me + Authorization: Bearer eyJ...
    F->>J: validateAndExtractUsername(token)
    J-->>F: "admin"
    F->>P: request con SecurityContext lleno
    P-->>C: 200 "admin"
```

## Glosario Basico
| Termino | Significado |
|---|---|
| **JWT** | JSON Web Token. Cadena `header.payload.signature` codificada en base64url. |
| **HMAC-SHA256 (HS256)** | Algoritmo de firma simetrica. Misma clave firma y verifica. |
| **Claim** | Cada campo del payload: `sub` (subject), `iat` (issued at), `exp` (expiration). |
| **Bearer token** | Convencion RFC 6750: `Authorization: Bearer <token>`. |
| **Stateless** | El servidor no guarda estado entre requests. |
| **`OncePerRequestFilter`** | Filtro Spring que corre exactamente una vez por request. |
| **`SecurityContextHolder`** | Contenedor `ThreadLocal` con el `Authentication` del usuario actual. |
| **`SecurityFilterChain`** | Bean de Spring Security 6+ que define las reglas de seguridad. |

## Conceptos

### 1. Anatomia de un JWT
Un JWT tiene tres partes separadas por punto:
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTcwMDAwMH0.abc123firma
   header (base64url)      payload (base64url)              signature
```
- **Header**: `{"alg":"HS256"}`.
- **Payload**: claims (`sub`, `iat`, `exp`, custom).
- **Signature**: `HMAC-SHA256(base64(header) + "." + base64(payload), secret)`.

Si un atacante modifica el payload, la firma deja de coincidir y el servidor rechaza el token.

### 2. Emision (`JwtService.generateToken`)
```java
Jwts.builder()
    .subject(username)
    .issuedAt(now)
    .expiration(expiration)
    .signWith(key)
    .compact();
```

### 3. Validacion (`JwtService.validateAndExtractUsername`)
```java
Jwts.parser()
    .verifyWith(key)
    .build()
    .parseSignedClaims(token)
    .getPayload()
    .getSubject();
```
Si la firma es invalida o el token expiro, jjwt lanza `JwtException` y el filtro deja el `SecurityContext` vacio.

### 4. Filtro (`JwtAuthFilter extends OncePerRequestFilter`)
Lee `Authorization`, extrae el token, valida y setea:
```java
new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList())
```
El tercer parametro (authorities) permitiria manejar roles con `@PreAuthorize` mas adelante.

### 5. Configuracion stateless (`SecurityConfig`)
- `csrf().disable()` porque los tokens no viajan por cookies del navegador automaticamente.
- `SessionCreationPolicy.STATELESS` evita crear `HttpSession`.
- `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`.

### Casos de uso empresariales
- APIs consumidas por SPAs (React/Angular/Vue).
- Autenticacion entre microservicios (propagar identidad).
- Apps moviles (iOS/Android almacenan el token).
- Machine-to-machine con client credentials (OAuth2 -> JWT).

## Antes vs Ahora

### Sesion con cookies (modulo 13) vs JWT stateless (modulo 14)
| Aspecto | Antes (sesion + cookie) | Ahora (JWT stateless) |
|---|---|---|
| Estado en servidor | `HttpSession` en memoria | Nada; el token es la fuente de verdad |
| Escalado horizontal | Requiere sticky sessions o Redis | Cualquier nodo puede validar |
| Cliente | Cookie `JSESSIONID` automatica | Header `Authorization: Bearer ...` manual |
| CSRF | Requiere tokens CSRF | No aplica (no hay envio automatico) |
| CORS | Complicado con credenciales | Trivial (solo header) |
| Logout | Invalidar sesion server-side | Cliente descarta token; blacklist opcional |

### Java 8 vs Java 21 en este modulo
| Tema | Antes (Java 8) | Ahora (Java 21) |
|---|---|---|
| DTO | Clase POJO con getters/setters, equals, hashCode | `record LoginRequest(String username, String password) {}` |
| jjwt API | `Jwts.builder().setSubject(u).signWith(SignatureAlgorithm.HS256, secret)` | `Jwts.builder().subject(u).signWith(key)` (infiere HS256) |
| Spring Security config | `extends WebSecurityConfigurerAdapter` + `configure(HttpSecurity)` | `@Bean SecurityFilterChain` con lambdas |
| Coleccion vacia | `Collections.emptyList()` | `List.of()` (equivalente e inmutable) |

## FAQ del Alumno

- **¿Puedo modificar el payload de un JWT?** Puedes decodificar la parte del payload (es solo base64), pero si la modificas la firma no coincide y el servidor rechaza el token.
- **¿Donde guardo el token en el cliente?** Trade-off clasico: `localStorage` es vulnerable a XSS; cookie `httpOnly + Secure + SameSite=Strict` es mas robusta pero requiere pensar en CSRF.
- **¿Que pasa si robo el token de otro usuario?** Puedes hacerte pasar por el hasta que expire. Por eso los tokens deben durar poco (10-15 min en produccion) y usarse siempre sobre HTTPS.
- **¿Como hago logout si el server no guarda nada?** Opciones: (a) el cliente borra el token, (b) usar tokens muy cortos + refresh tokens, (c) mantener una blacklist server-side (rompe el stateless).
- **¿Por que HS256 y no RS256?** HS256 usa una sola clave compartida (mas simple). RS256 usa clave publica/privada (mejor para escenarios donde varios servicios verifican pero solo uno emite). El modulo 34-oauth2 profundiza en esto.
- **¿Por que 401 y no 403 cuando no hay token?** 401 = "no te identificaste"; 403 = "te identificaste pero no tienes permiso". Sin token es 401.
- **¿El secret HARDCODED es seguro?** NO. Es solo para demo. En produccion se inyecta desde variable de entorno, Vault, AWS Secrets Manager, etc.
- **¿Que es `Bearer`?** Palabra clave del RFC 6750 que indica "el portador del token es el titular". Analogo a un boleto al portador.

## Ejercicios
1. Agregar el claim `roles` al token e imprimir los roles en `/api/me`.
2. Reducir la expiracion a 60 segundos y verificar que despues de esperar, `/api/me` responde 401.
3. Agregar endpoint `POST /api/auth/refresh` que reciba un token proximo a expirar y devuelva uno nuevo.
4. Reemplazar las credenciales hardcoded por un `UserDetailsService` en memoria con `BCryptPasswordEncoder`.
5. Cambiar el algoritmo a RS256 con par de claves generado en el arranque.

## Como ejecutar
```bash
# Windows PowerShell
./build.ps1
java -jar target/jwt-1.0.0.jar

# Git Bash / Linux / Mac
./build.sh
java -jar target/jwt-1.0.0.jar

# Alternativa desarrollo
../apache-maven-3.9.16/bin/mvn spring-boot:run
```

Prueba manual:
```bash
# 1) Login
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
# -> { "token": "eyJhbGciOi..." }

# 2) Endpoint protegido
curl http://localhost:8080/api/me \
     -H "Authorization: Bearer eyJhbGciOi..."
# -> admin

# 3) Sin token
curl -i http://localhost:8080/api/me
# -> HTTP/1.1 401
```

## Archivos del Proyecto
| Archivo | Proposito |
|---|---|
| `pom.xml` | Dependencias (web, security, jjwt 0.12.6, test) y `finalName=jwt-1.0.0`. |
| `src/main/java/.../JwtApplication.java` | Clase principal Spring Boot. |
| `src/main/java/.../service/JwtService.java` | Emision y validacion de JWT (HS256). |
| `src/main/java/.../security/JwtAuthFilter.java` | Filtro que lee `Authorization` y llena `SecurityContext`. |
| `src/main/java/.../security/SecurityConfig.java` | `SecurityFilterChain` stateless con las reglas de acceso. |
| `src/main/java/.../controller/AuthController.java` | `POST /api/auth/login` -> emite token. |
| `src/main/java/.../controller/PrivateController.java` | `GET /api/me` -> devuelve el principal. |
| `src/main/java/.../dto/LoginRequest.java` | `record` con username/password. |
| `src/main/java/.../dto/TokenResponse.java` | `record` con el token emitido. |
| `src/main/resources/application.yml` | Puerto y logging. |
| `src/test/.../JwtApplicationTests.java` | `contextLoads`. |
| `src/test/.../service/JwtServiceTest.java` | Tests unitarios puros del servicio JWT. |
| `src/test/.../AuthIntegrationTest.java` | Tests de integracion con `TestRestTemplate`. |
| `build.sh` / `build.ps1` | Scripts que usan el toolchain portable. |
