## 33 — Seguridad Avanzada (RBAC, Method Security, CORS y CSRF)

### Propósito
Llevar la seguridad de tu aplicación Spring Boot más allá de un simple inicio de sesión. Aprender a proteger métodos específicos (Method Security), implementar control de acceso basado en roles (RBAC) de forma granular, y configurar correctamente las protecciones contra ataques comunes de navegadores como CORS y CSRF.

### Problema que resuelve
- **Autorización Insuficiente:** Tienes un endpoint `/api/users/delete`. Si solo verificas que el usuario esté "autenticado", cualquier usuario normal podría borrar a otro usuario. Necesitas validar que tenga el rol de "ADMINISTRADOR".
- **Ataques de Navegador (CORS/CSRF):** Un atacante crea una página web falsa (ej: `www.tu-banco-falso.com`) que hace peticiones AJAX secretas hacia tu API (`www.tu-banco.com`) aprovechando que el usuario dejó su sesión abierta.
- **Filtros Globales Inmantenibles:** Poner toda la seguridad en el bloque `HttpSecurity` (ej: `.requestMatchers("/admin/**").hasRole("ADMIN")`) se vuelve inmanejable cuando tu API crece a cientos de endpoints.

### Cómo lo resuelve
- **Method Security (`@PreAuthorize`)**: Te permite poner la regla de negocio de seguridad directamente encima del método que quieres proteger. Es la forma más legible y mantenible de hacer autorización.
- **Configuración de CORS**: Le dice al navegador explícitamente "Solo acepto peticiones que vengan del dominio `tu-frontend.com`".
- **Protección CSRF**: (Cross-Site Request Forgery). Exige un token dinámico en cada petición POST, haciendo imposible que un sitio externo falsifique una petición válida.

### Por qué aprenderlo
La seguridad perimetral (Autenticación) es solo el paso 1. La seguridad profunda (Autorización en capas) es lo que evita los peores hackeos corporativos (Escalación de privilegios). Dominar CORS te ahorrará horas de dolores de cabeza integrando frontends modernos (React, Angular).

```mermaid
graph TD
    A["Hacker en 'sitio-malo.com'"] -->|Script AJAX GET /api/data| B["Tu Servidor Spring"]
    B --> C{"Filtro CORS"}
    C -->|"Origen No Permitido"| D["Bloquea la respuesta"]
    
    E["Usuario Legítimo"] -->|GET /api/admin/delete| F["Controlador REST"]
    F --> G["AOP: @PreAuthorize('hasRole(\"ADMIN\")')"]
    G -->|"No es Admin"| H["HTTP 403 Forbidden"]
    G -->|"Es Admin"| I["Ejecuta Lógica"]

    style D fill:#ff6b6b,color:#fff
    style H fill:#ff6b6b,color:#fff
    style I fill:#51cf66,color:#fff
```

---

### Glosario Básico

#### `RBAC` (Role-Based Access Control)
Control de acceso basado en roles. Asignas roles (`USER`, `ADMIN`) a las personas, y concedes permisos a los roles, no a las personas directamente.

#### `@EnableMethodSecurity`
Anotación en la clase de configuración de Spring Security que enciende el soporte para anotar métodos individuales con reglas de seguridad. (Reemplaza a la vieja `@EnableGlobalMethodSecurity`).

#### `@PreAuthorize`
Anotación de Method Security. Evalúa una expresión *antes* de que el método se ejecute. Si es falsa, lanza un `AccessDeniedException` (HTTP 403).

#### `CORS` (Cross-Origin Resource Sharing)
Un mecanismo de seguridad **del navegador web**. Por defecto, si un script en `dominio-a.com` intenta hacer un fetch a `dominio-b.com`, el navegador lo bloquea a menos que el servidor de `dominio-b.com` responda con un header especial (`Access-Control-Allow-Origin: dominio-a.com`).

#### `CSRF` (Cross-Site Request Forgery)
Ataque donde el usuario es engañado para ejecutar una acción no deseada en un sitio web en el que está autenticado. Si usas Sesiones (Cookies), debes protegerte. Si usas Tokens JWT en Headers (Arquitectura Stateless), el CSRF es estadísticamente imposible y se puede deshabilitar.

---

### Conceptos

#### 1. Method Security y Autorización Granular
- **Qué es** — En vez de definir todas tus reglas en el `SecurityFilterChain`, las defines método por método usando SpEL (Spring Expression Language).
- **Código** — Configuración y uso:
  ```java
  @Configuration
  @EnableWebSecurity
  @EnableMethodSecurity // 1. Activamos la seguridad por método
  public class SecurityConfig { ... }
  ```
  ```java
  @RestController
  @RequestMapping("/api/documents")
  public class DocumentController {
  
      // Solo usuarios con ROL ADMIN pueden ejecutar esto
      @PreAuthorize("hasRole('ADMIN')")
      @DeleteMapping("/{id}")
      public void deleteDocument(@PathVariable Long id) { ... }
  
      // SpEL Avanzado: El usuario solo puede ver el documento si el usuario actual
      // es EXACTAMENTE el dueño del documento (id coincidente)
      @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
      @GetMapping("/user/{userId}")
      public List<Document> getUserDocs(@PathVariable Long userId) { ... }
  }
  ```

#### 2. Entendiendo y Configurando CORS
- **Qué es** — Configurar tu backend para que acepte peticiones desde el servidor donde está alojado tu frontend (ej. Vercel o Netlify).
- **Código** — Configuración global (La mejor práctica):
  ```java
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
          // 1. Inyecta la configuración CORS al filtro de seguridad
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          // ... resto de config
          .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
          
      return http.build();
  }
  
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      
      // SOLO permite que este frontend te haga peticiones
      configuration.setAllowedOrigins(List.of("https://mi-frontend-real.com", "http://localhost:4200"));
      // Permite los métodos que necesites
      configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      // Permite que el frontend te envíe el token de Autorización
      configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
      
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration); // Aplica a todas tus rutas
      return source;
  }
  ```

#### 3. El Dilema del CSRF (Cookies vs JWT)
- **El Concepto Vital:** El CSRF se basa en que los navegadores envían automáticamente todas las Cookies al servidor de destino. Si autenticas a tus usuarios con `JSESSIONID` (Cookies), un script malicioso puede hacer un POST a tu banco y el navegador adjuntará la cookie.
- **La Solución (Monolitos):** Mantener `.csrf(csrf -> csrf.withDefaults())` activado. Spring generará un Token CSRF aleatorio que el Frontend debe leer y enviar como un Header oculto en cada POST. Si no coincide, Spring rechaza el POST con un HTTP 403.
- **La Solución (APIs REST con JWT):** Si usas React/Angular y guardas tu JWT en `localStorage`, tú mismo debes enviar el header `Authorization: Bearer <token>`. El navegador NUNCA envía ese header automáticamente. Por tanto, las APIs REST Stateless son **inmunes** al CSRF.
  ```java
  http
      // Si tu app es 100% REST con JWT, es completamente seguro deshabilitarlo
      .csrf(csrf -> csrf.disable()) 
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
  ```

#### 4. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| 403 CORS Error persistente | Falla el "Preflight Request" (`OPTIONS`). | El navegador primero envía un `OPTIONS` vacío para preguntar permisos. Si tu `SecurityFilterChain` requiere autenticación para TODOS los requests, bloquea el `OPTIONS` (porque no lleva JWT). Asegúrate de configurar `.cors()` antes de la autorización para que Spring responda los Preflights automáticamente. |
| `hasRole('ADMIN')` falla (Retorna 403 siempre) | Tus Authorities en el Token/BD dicen "ADMIN" | Spring Security espera por defecto que los roles empiecen con el prefijo "ROLE_". Debes tener en BD "ROLE_ADMIN", o usar `hasAuthority('ADMIN')` en vez de `hasRole`. |
| Postman funciona, el Navegador no | Postman no respeta las políticas CORS (es una app de escritorio) | Las restricciones CORS son un acuerdo de honor implementado por los navegadores (Chrome, Safari). Siempre debes probar la integración CORS desde un navegador real ejecutando tu app de React/Angular/JS. |

---

### Ejercicios
1. Crea un controlador con tres endpoints: `/publico`, `/user/datos` y `/admin/borrar`.
2. Activa `@EnableMethodSecurity`.
3. Anota `/admin/borrar` con `@PreAuthorize("hasRole('ADMIN')")`.
4. Inyecta usuarios en memoria (`InMemoryUserDetailsManager`), uno con rol `USER` y otro con rol `ADMIN`.
5. Inicia sesión como `USER` e intenta acceder a `/admin/borrar`. Verifica que recibes un HTTP 403 Forbidden.
6. (Para CORS) Levanta tu aplicación en el puerto 8080. Crea un archivo `index.html` estático en el puerto 3000 (usando Python `http.server` o NodeJS) que haga un `fetch('http://localhost:8080/publico')`. Verás el error CORS en la consola del navegador.
7. Aplica la configuración de `CorsConfigurationSource` permitiendo a `http://localhost:3000`. Refresca el HTML y observa cómo ahora el Fetch tiene éxito.

### Cómo ejecutar
```bash
cd 33-security-avanzado
mvn spring-boot:run

# Probar acceso de un usuario a recurso de Admin (Rechazado)
curl -u user:password http://localhost:8080/api/admin/borrar -v
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `config/SecurityConfig.java` | Configuración de CORS y desactivación de CSRF (Stateless). |
| `controller/AdminController.java` | Controladores protegidos con `@PreAuthorize`. |
| `service/CustomUserDetailsService.java` | Carga de usuarios y asignación de Authorities (`ROLE_ADMIN`). |
