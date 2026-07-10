## 14 — JWT (JSON Web Tokens) con Spring Security

### Propósito
Aprender a implementar autenticación stateless con JWT (JSON Web Tokens) en Spring Security, incluyendo la generación de access tokens y refresh tokens, filtros de autenticación y protección de endpoints REST sin sesiones de servidor.

### Problema que resuelve
La autenticación basada en sesiones (módulo 13) tiene limitaciones críticas para APIs modernas:
- **No es escalable horizontalmente**: Si tienes 3 instancias detrás de un load balancer, la sesión solo existe en una instancia.
- **No funciona para móviles y SPAs**: Las aplicaciones React/Angular y las apps móviles no manejan cookies de sesión.
- **Estado en el servidor**: El servidor debe almacenar en memoria la sesión de cada usuario activo, consumiendo recursos.

### Cómo lo resuelve
JWT es un token auto-contenido (contiene los datos del usuario codificados) que el cliente envía en cada petición dentro del header `Authorization`. El servidor no necesita almacenar sesiones; solo verifica la firma del token.

### Por qué aprenderlo
JWT es el estándar de facto para autenticación en APIs REST, microservicios, aplicaciones móviles y SPAs. Empresas como Netflix, Spotify y bancos digitales usan JWT. En entrevistas técnicas, implementar un flujo JWT completo es una prueba frecuente.

```mermaid
sequenceDiagram
    participant Client as Cliente (React/Angular/Mobile)
    participant Auth as POST /api/auth/login
    participant JWT as JwtService
    participant Filter as JwtAuthFilter
    participant API as GET /api/users (protegido)

    Client->>Auth: {"username":"admin","password":"123"}
    Auth->>JWT: generateAccessToken(user)
    JWT-->>Auth: accessToken (expira en 15min)
    Auth->>JWT: generateRefreshToken(user)
    JWT-->>Auth: refreshToken (expira en 7 días)
    Auth-->>Client: {"accessToken":"eyJ...","refreshToken":"eyJ..."}

    Note over Client: Guarda los tokens (localStorage/HttpOnly cookie)

    Client->>Filter: GET /api/users + Header: Authorization: Bearer eyJ...
    Filter->>JWT: validateToken(token)
    JWT-->>Filter: ✅ Token válido, usuario: admin
    Filter->>Filter: SecurityContextHolder.setAuthentication(admin)
    Filter->>API: Petición autorizada
    API-->>Client: 200 OK + lista de usuarios

    Note over Client: Cuando el accessToken expira...
    Client->>Auth: POST /api/auth/refresh + refreshToken
    Auth->>JWT: validateRefreshToken + generateNewAccessToken
    Auth-->>Client: {"accessToken":"eyJ_nuevo..."}
```

---

### Glosario Básico

#### `JWT (JSON Web Token)`
Un string codificado en Base64 con tres partes separadas por puntos: `header.payload.signature`. El payload contiene los datos del usuario (claims) y la signature asegura que no ha sido modificado.
```
eyJhbGciOiJIUzI1NiJ9.       <- Header (algoritmo)
eyJzdWIiOiJhZG1pbiJ9.       <- Payload (claims: username, roles, expiration)
SflKxwRJSMeKKF2QT4fwpM...   <- Signature (firma con secret key)
```

#### `Access Token`
Token de corta duración (15 min) que se envía en cada petición. Si expira, el usuario necesita un nuevo token.

#### `Refresh Token`
Token de larga duración (7 días) que se usa exclusivamente para obtener un nuevo Access Token sin requerir login completo.

#### `JwtAuthenticationFilter`
Un filtro de Spring Security que intercepta cada petición, extrae el JWT del header `Authorization: Bearer xxx`, lo valida y establece la autenticación.

#### `SecurityContextHolder`
El "almacén" donde Spring Security guarda la identidad del usuario autenticado durante el procesamiento de una petición.

---

### Conceptos

#### 1. JwtService — Generación y Validación de Tokens
- **Qué es** — El servicio central que genera, valida y extrae información de los JWT. Usa la librería `jjwt` (Java JWT).
- **Por qué importa** — Centralizar la lógica JWT en un solo servicio garantiza que todos los tokens se generen con la misma clave secreta y las mismas reglas.
- **Código** — Servicio JWT completo:
  ```java
  @Service
  @Slf4j
  public class JwtService {
  
      @Value("${jwt.secret}")
      private String secretKey;
  
      @Value("${jwt.access-token.expiration}")
      private long accessTokenExpiration;  // 15 minutos en milisegundos
  
      @Value("${jwt.refresh-token.expiration}")
      private long refreshTokenExpiration;  // 7 días en milisegundos
  
      /**
       * Genera un Access Token con los datos del usuario.
       * Los "claims" son los datos que viajan dentro del token.
       */
      public String generateAccessToken(UserDetails userDetails) {
          Map<String, Object> claims = new HashMap<>();
          claims.put("roles", userDetails.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .toList());
          
          return buildToken(claims, userDetails.getUsername(), accessTokenExpiration);
      }
  
      /**
       * Genera un Refresh Token (sin claims adicionales, solo el username).
       */
      public String generateRefreshToken(UserDetails userDetails) {
          return buildToken(Map.of(), userDetails.getUsername(), refreshTokenExpiration);
      }
  
      private String buildToken(Map<String, Object> claims, String subject, long expiration) {
          return Jwts.builder()
              .claims(claims)
              .subject(subject)
              .issuedAt(Date.from(Instant.now()))
              .expiration(Date.from(Instant.now().plusMillis(expiration)))
              .signWith(getSigningKey())
              .compact();
      }
  
      /**
       * Extrae el username del token (el "subject" del JWT).
       */
      public String extractUsername(String token) {
          return extractClaims(token).getSubject();
      }
  
      /**
       * Valida que el token no esté expirado y pertenezca al usuario correcto.
       */
      public boolean isTokenValid(String token, UserDetails userDetails) {
          String username = extractUsername(token);
          return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
      }
  
      private boolean isTokenExpired(String token) {
          return extractClaims(token).getExpiration().before(new Date());
      }
  
      private Claims extractClaims(String token) {
          return Jwts.parser()
              .verifyWith(getSigningKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();
      }
  
      private SecretKey getSigningKey() {
          byte[] keyBytes = Decoders.BASE64.decode(secretKey);
          return Keys.hmacShaKeyFor(keyBytes);
      }
  }
  ```
  ```yaml
  # application.yml
  jwt:
    secret: "dGhpcyBpcyBhIHZlcnkgc2VjdXJlIGtleSBmb3IgSldUIHRva2VuIGdlbmVyYXRpb24="
    access-token:
      expiration: 900000    # 15 minutos
    refresh-token:
      expiration: 604800000  # 7 días
  ```
- **Analogía** — El JwtService es como la imprenta de un gobierno que fabrica pasaportes. Tiene el sello oficial (secret key) para crearlos, y también tiene el escáner (validación) para verificar que un pasaporte es auténtico y no ha expirado.

#### 2. JwtAuthenticationFilter — Interceptar Cada Petición
- **Qué es** — Un filtro que se ejecuta antes de cada petición HTTP. Extrae el token del header, lo valida y establece la autenticación en el `SecurityContextHolder`.
- **Código** — Filtro JWT completo:
  ```java
  @Component
  @Slf4j
  public class JwtAuthenticationFilter extends OncePerRequestFilter {
  
      private final JwtService jwtService;
      private final UserDetailsService userDetailsService;
  
      public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
          this.jwtService = jwtService;
          this.userDetailsService = userDetailsService;
      }
  
      @Override
      protected void doFilterInternal(
              HttpServletRequest request,
              HttpServletResponse response,
              FilterChain filterChain) throws ServletException, IOException {
  
          // 1. Extraer el header "Authorization"
          String authHeader = request.getHeader("Authorization");
  
          // Si no hay header o no empieza con "Bearer ", pasar al siguiente filtro
          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
              filterChain.doFilter(request, response);
              return;
          }
  
          // 2. Extraer el token (quitar "Bearer ")
          String jwt = authHeader.substring(7);
  
          try {
              // 3. Extraer el username del token
              String username = jwtService.extractUsername(jwt);
  
              // 4. Si el usuario no está ya autenticado en este request
              if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                  
                  UserDetails userDetails = userDetailsService.loadUserByUsername(username);
  
                  // 5. Validar el token
                  if (jwtService.isTokenValid(jwt, userDetails)) {
                      // 6. Crear la autenticación y ponerla en el contexto
                      var authToken = new UsernamePasswordAuthenticationToken(
                          userDetails, null, userDetails.getAuthorities()
                      );
                      authToken.setDetails(new WebAuthenticationDetailsSource()
                          .buildDetails(request));
  
                      SecurityContextHolder.getContext().setAuthentication(authToken);
                  }
              }
          } catch (ExpiredJwtException e) {
              log.warn("Token expirado para request: {}", request.getRequestURI());
          } catch (JwtException e) {
              log.warn("Token inválido: {}", e.getMessage());
          }
  
          filterChain.doFilter(request, response);
      }
  }
  ```

#### 3. AuthController — Login y Refresh
- **Qué es** — El controller que maneja el flujo de autenticación: login y refresh de tokens.
- **Código** — Controller de autenticación:
  ```java
  @RestController
  @RequestMapping("/api/auth")
  public class AuthController {
  
      private final AuthenticationManager authenticationManager;
      private final JwtService jwtService;
      private final UserDetailsService userDetailsService;
  
      public AuthController(AuthenticationManager authenticationManager,
                            JwtService jwtService,
                            UserDetailsService userDetailsService) {
          this.authenticationManager = authenticationManager;
          this.jwtService = jwtService;
          this.userDetailsService = userDetailsService;
      }
  
      @PostMapping("/login")
      public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
          // Autenticar credenciales contra la BD
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.username(), request.password())
          );
  
          // Cargar detalles del usuario
          UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
  
          // Generar tokens
          String accessToken = jwtService.generateAccessToken(userDetails);
          String refreshToken = jwtService.generateRefreshToken(userDetails);
  
          return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
      }
  
      @PostMapping("/refresh")
      public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
          String username = jwtService.extractUsername(request.refreshToken());
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
  
          if (jwtService.isTokenValid(request.refreshToken(), userDetails)) {
              String newAccessToken = jwtService.generateAccessToken(userDetails);
              return ResponseEntity.ok(new AuthResponse(newAccessToken, request.refreshToken()));
          }
  
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
  }
  ```

#### 4. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `SignatureException` | El secret key cambió o el token fue manipulado | Usar la misma key en todos los servidores. Nunca cambiar la key en producción sin invalidar tokens |
| `ExpiredJwtException` | El access token expiró | El frontend debe usar el refresh token para obtener uno nuevo |
| Secret key débil | Key menor a 256 bits | Usar al menos 32 bytes (256 bits) codificados en Base64 |
| Token en localStorage (XSS vulnerable) | JavaScript malicioso puede robar el token | Preferir HttpOnly cookies para refresh tokens. Access token en memoria |
| No verificar expiración | `isTokenValid` no checa `expiration` | Siempre verificar `!isTokenExpired(token)` |
| Refresh token sin expiración | El refresh token nunca expira | Siempre configurar expiración (7 días típico) |

---

### Ejercicios
1. Agrega la dependencia `jjwt-api`, `jjwt-impl` y `jjwt-jackson` al `pom.xml`.
2. Implementa el `JwtService` con métodos para generar, validar y extraer claims de un token.
3. Crea el `JwtAuthenticationFilter` que intercepte cada petición y valide el token.
4. Implementa los endpoints `POST /api/auth/login` y `POST /api/auth/refresh`.
5. Prueba el flujo completo: registrar → login → usar access token → esperar expiración → refresh.

### Cómo ejecutar
```bash
cd 14-jwt
mvn spring-boot:run

# 1. Login:
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Usar el access token:
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# 3. Refresh:
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"eyJ..."}'
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Dependencias: `jjwt-api`, `jjwt-impl`, `jjwt-jackson`. |
| `config/SecurityConfig.java` | `SecurityFilterChain` con JWT filter y stateless session. |
| `security/JwtService.java` | Generación, validación y extracción de claims JWT. |
| `security/JwtAuthenticationFilter.java` | Filtro que intercepta cada petición HTTP. |
| `controller/AuthController.java` | Endpoints `/login` y `/refresh`. |
| `dto/LoginRequest.java` | Record con username y password. |
| `dto/AuthResponse.java` | Record con accessToken y refreshToken. |
| `dto/RefreshRequest.java` | Record con el refreshToken para renovar. |
