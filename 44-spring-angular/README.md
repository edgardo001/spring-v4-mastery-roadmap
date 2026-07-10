## 44 — Integración Spring Boot + Angular

### Propósito
Dominar la integración fluida entre un Backend empresarial de Spring Boot y un Frontend fuertemente tipado en Angular. Aprender a configurar el ecosistema de Angular (RxJS, Interceptors, Proxy) para consumir tu API REST de forma segura y eficiente.

### Problema que resuelve
Angular, a diferencia de React (que es una librería), es un Framework completo orientado a aplicaciones corporativas masivas. 
- Los desarrolladores luchan con el CORS al desarrollar localmente.
- ¿Cómo manejamos la autenticación y pasamos el Token JWT a las 500 llamadas HTTP de Angular sin repetirlo en cada una?
- ¿Cómo mapeamos las respuestas del API REST de Spring a los Observables de RxJS en Angular?

### Cómo lo resuelve
1. **Proxy en Desarrollo:** Angular CLI provee el archivo `proxy.conf.json` para redirigir las llamadas y evitar el CORS.
2. **HttpInterceptor:** El mecanismo nativo de Angular para atrapar todas las peticiones globales e inyectar el Header de Autorización de forma centralizada.
3. **Mapeo de DTOs con TypeScript:** Angular es fuertemente tipado. Crearemos interfaces (o clases) en TypeScript que hagan "match" exacto con los DTOs (Records) de Spring Boot, logrando seguridad de compilación de extremo a extremo.

### Por qué aprenderlo
Java Spring Boot y Angular es, históricamente, el dúo dinámico de las empresas (Bancos, Seguros, Entidades Gubernamentales). Comparten filosofías similares: Tipado fuerte (Java/TypeScript), Inyección de Dependencias, Decoradores/Anotaciones y Arquitectura orientada a Servicios. Conocer ambos te hace el candidato ideal para el software Enterprise.

```mermaid
graph TD
    subgraph Frontend Corporativo (Angular)
        A_COMP["Componente UI<br/>(Presentación)"]
        A_SERV["Servicio (@Injectable)<br/>(Lógica RxJS)"]
        A_INT["HttpInterceptor<br/>(Seguridad)"]
        
        A_COMP -->|Llama método| A_SERV
        A_SERV -->|Genera HTTP Request| A_INT
        A_INT -->|Inyecta Bearer JWT| HTTP_OUT(("HTTP Client"))
    end
    
    subgraph Backend Corporativo (Spring Boot)
        S_FILTER["Filtro de Seguridad<br/>(Valida JWT)"]
        S_CTRL["@RestController<br/>(Expone API)"]
        
        HTTP_OUT -->|Petición Segura| S_FILTER
        S_FILTER -->|Aprobado| S_CTRL
    end

    style A_INT fill:#ffc9c9,stroke:#e03131
    style S_FILTER fill:#339af0,color:#fff
```

---

### Glosario Básico

#### `RxJS` (Reactive Extensions for JavaScript)
La librería principal que usa Angular para manejar asincronismo. En lugar de Promesas (Promises), usa Observables, que son canales de datos continuos.

#### `HttpInterceptor`
Un middleware de Angular que intercepta tanto las peticiones (Requests) que salen, como las respuestas (Responses) que llegan. Es el análogo al `OncePerRequestFilter` de Spring Boot, pero en el frontend.

#### `proxy.conf.json`
Archivo de configuración para el servidor de desarrollo de Angular (`ng serve`). Actúa como puente entre `localhost:4200` y `localhost:8080`, anulando el problema de CORS.

---

### Conceptos

#### 1. Configuración del Proxy (Para el Entorno Local)
- **Qué es** — Igual que en React, engañamos al navegador.
- **Código** — Creamos el archivo `proxy.conf.json` en la raíz de Angular:
  ```json
  {
    "/api": {
      "target": "http://localhost:8080",
      "secure": false,
      "changeOrigin": true
    }
  }
  ```
  Modifica el `angular.json` o corre la app con: `ng serve --proxy-config proxy.conf.json`.
  Ahora tu código de Angular solo necesita hacer `http.get('/api/users')`.

#### 2. Tipado Estricto de DTOs (Backend -> Frontend)
- **Qué es** — El DTO (Record) de Spring debe tener una réplica exacta (Interface) en TypeScript.
- **Código**:
  
  **Backend (Java):**
  ```java
  public record UserDto(Long id, String username, String email) {}
  ```
  
  **Frontend (TypeScript `user.dto.ts`):**
  ```typescript
  export interface UserDto {
      id: number;
      username: string;
      email: string;
  }
  ```

#### 3. El Consumo con HttpClient y Observables
- **Qué es** — Angular no usa `fetch` ni `axios` por defecto; usa su poderoso `HttpClient`.
- **Código**:
  ```typescript
  @Injectable({
    providedIn: 'root'
  })
  export class UserService {
    // Inyección de dependencias estilo Spring
    constructor(private http: HttpClient) { }
  
    getUsers(): Observable<UserDto[]> {
      return this.http.get<UserDto[]>('/api/users');
    }
  }
  ```
  **El Componente:**
  ```typescript
  export class UserListComponent implements OnInit {
    users$: Observable<UserDto[]>; // La convención del signo $ indica Observable
  
    constructor(private userService: UserService) {}
  
    ngOnInit() {
      // Angular recomienda suscribirse directamente en el HTML usando la tubería (pipe) | async
      this.users$ = this.userService.getUsers();
    }
  }
  ```

#### 4. Seguridad: El JWT Interceptor
- **Qué es** — Evita tener que escribir el Header de Authorization en cada `http.get()`.
- **Código (Angular 15+ / Standalone):**
  ```typescript
  import { HttpInterceptorFn } from '@angular/common/http';
  
  export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const token = localStorage.getItem('jwt_token');
    
    // Si la petición va a tu propia API y hay token, se inyecta
    if (token && req.url.startsWith('/api')) {
      const clonedReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next(clonedReq);
    }
    
    return next(req);
  };
  ```
  **Para Registrarlo (En `app.config.ts`):**
  ```typescript
  export const appConfig: ApplicationConfig = {
    providers: [
      provideHttpClient(withInterceptors([authInterceptor]))
    ]
  };
  ```

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Las fechas (LocalDate/LocalDateTime) llegan como un arreglo `[2024, 10, 5]` | El serializador JSON de Java (Jackson) serializa las fechas de Java 8 como arreglos en lugar de strings ISO-8601 por defecto. | Agregar `spring.jackson.serialization.write-dates-as-timestamps=false` al `application.yml` de Spring Boot y añadir la dependencia de `jackson-datatype-jsr310`. |
| Memory Leaks en Componentes | Angular descarga tu API pero tú nunca destruyes la suscripción. | Aunque el `HttpClient` hace auto-unsubscribe al terminar, otras suscripciones no. Acostúmbrate a usar `takeUntilDestroyed()` o preferiblemente el `| async` pipe en el HTML para manejo automático de la memoria. |
| El CORS me bloquea en Producción | En local el Proxy salvó tu vida. Pero en producción el Proxy de Angular (que es solo para desarrollo) no existe. | Configurar el CORS Globalmente en Spring Boot (Ver Módulo 33 y 43) o desplegar tu app Angular sirviendo los estáticos desde el propio servidor Spring o detrás de un Nginx común. |

---

### Ejercicios
1. Crea una API sencilla de Spring Boot que devuelva una lista de Tareas (`TaskDto`).
2. Genera una aplicación Angular: `ng new frontend-app`.
3. Configura el archivo `proxy.conf.json` para esquivar el CORS.
4. Define la interface de TypeScript `TaskDto` igual al Record de Java.
5. Crea un `TaskService` en Angular que haga la petición usando `HttpClient`. Renderiza las tareas en un Componente usando el `AsyncPipe` (`*ngFor="let task of tasks$ | async"`).

### Cómo ejecutar
```bash
# Terminal 1 - Backend Spring
cd 44-spring-angular/backend
mvn spring-boot:run

# Terminal 2 - Frontend Angular
cd 44-spring-angular/frontend
npm install
npm run start # Internamente ejecuta ng serve
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `backend/controller/ApiController.java` | API Rest que proveerá la data. |
| `frontend/proxy.conf.json` | Proxy reverso de Desarrollo. |
| `frontend/src/app/core/interceptors/auth.interceptor.ts` | Interceptor funcional (Angular 15+) para inyección JWT. |
| `frontend/src/app/models/dto.ts` | Interface Typescript para tipado estricto extremo a extremo. |
