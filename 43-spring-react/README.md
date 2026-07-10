## 43 — Integración Spring Boot + React

### Propósito
Aprender las estrategias arquitectónicas para integrar un Backend robusto en Spring Boot (API REST) con un Frontend moderno en React (Single Page Application - SPA), resolviendo los desafíos de CORS, autenticación y despliegue conjunto vs separado.

### Problema que resuelve
El desarrollador novato suele crear un monolito con Thymeleaf (Módulo 02). Cuando intenta migrar a React o Angular, se encuentra con múltiples muros de la arquitectura web moderna:
- Las llamadas desde el navegador (React corriendo en el puerto 3000) hacia Spring (puerto 8080) fallan por **CORS**.
- ¿Dónde guardo el Token JWT de forma segura sin que me roben la sesión por XSS (Cross-Site Scripting)?
- ¿Cómo compilo y despliego el proyecto final a Producción? ¿Debe Spring servir los archivos de React, o uso un CDN/Nginx?

### Cómo lo resuelve
1. **En Desarrollo:** Se configura un Proxy en React (o Vite) para que engañe al navegador y redirija las peticiones a Spring, evitando los problemas de CORS locales.
2. **Autenticación Segura:** Spring Security se configura de manera "Stateless" y el cliente de React (Axios / Fetch) implementa interceptores para inyectar el Token JWT en el header `Authorization` en cada petición.
3. **Despliegue:** Existen 2 estrategias principales (Separado y Empaquetado), cada una resolviendo un caso de uso corporativo distinto.

### Por qué aprenderlo
Java Backend + React Frontend es una de las combinaciones de stack tecnológico más demandadas en el mercado laboral mundial (Fullstack Developer). Dominar la integración impecable entre ambos mundos es crucial para construir sistemas modernos y escalables.

```mermaid
graph TD
    subgraph Modo Desarrollo (Local)
        R_DEV["Navegador<br/>(React en Puerto 3000)"]
        P_DEV(("Proxy Local<br/>(Vite / Webpack)"))
        S_DEV["Spring Boot<br/>(Puerto 8080)"]
        
        R_DEV -->|Llama a /api/users| P_DEV
        P_DEV -->|Redirige sin CORS| S_DEV
    end
    
    subgraph Modo Producción (Empaquetado - Monorepo)
        N["Navegador<br/>(El cliente)"]
        S_PROD["Spring Boot<br/>(Puerto 80 o 443)"]
        F_PROD[("Archivos Estáticos<br/>(index.html, .js, .css)")]
        
        N -->|Pide la web (GET /)| S_PROD
        S_PROD -->|Sirve desde /public| F_PROD
        N -->|Pide datos (GET /api)| S_PROD
    end

    style P_DEV fill:#ffc9c9,stroke:#e03131
    style S_PROD fill:#339af0,color:#fff
```

---

### Glosario Básico

#### `SPA (Single Page Application)`
Una aplicación web (React, Angular, Vue) que carga una sola página HTML inicialmente. Toda la navegación posterior se hace reescribiendo la página con JavaScript en el navegador, sin recargar (Falso enrutamiento).

#### `CORS` (Cross-Origin Resource Sharing)
Restricción de seguridad del navegador. Visto en el Módulo 33, impide que código JavaScript descargado desde `http://localhost:3000` (React) pueda leer respuestas de `http://localhost:8080` (Spring), a menos que Spring lo permita explícitamente.

#### `Proxy Reverso (Desarrollo)`
Configuración en el servidor de desarrollo de React (ej. Vite) que intercepta cualquier petición que empiece con `/api` y la reenvía a Spring Boot, haciéndole creer al navegador que ambas cosas corren en el mismo puerto.

#### `Axios Interceptor`
Un middleware en React que permite atrapar todas las peticiones HTTP justo antes de salir de la aplicación, inyectándoles automáticamente el Token JWT al header.

---

### Conceptos

#### 1. Estrategia 1: Despliegue Separado (Microservicios / API First)
- **Qué es** — Spring Boot se compila y sube a un servidor (AWS ECS, Heroku). React se compila de forma independiente (`npm run build`) y se sube a un CDN estático (Vercel, Netlify, AWS S3).
- **Ventajas** — Equipos independientes (Front vs Back), escalabilidad separada, el CDN sirve los archivos de React mucho más rápido (Global Cache).
- **Código en Spring (CORS Requerido)**:
  Como corren en dominios reales distintos (`api.miempresa.com` y `www.miempresa.com`), debes configurar CORS en Spring (Módulo 33).
  ```java
  @Configuration
  public class CorsConfig implements WebMvcConfigurer {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/api/**")
                  .allowedOrigins("https://www.miempresa.com")
                  .allowedMethods("GET", "POST", "PUT", "DELETE");
      }
  }
  ```

#### 2. Estrategia 2: Despliegue Empaquetado (El "Nuevo Monolito")
- **Qué es** — Tú compilas React, tomas los archivos estáticos generados y los metes dentro de la carpeta `src/main/resources/public` o `static` de Spring Boot. Spring actuará como el servidor de la API *y* el servidor web que devuelve el `index.html`. 
- **Ventajas** — Un solo `.jar` ejecutable, 1 solo servidor para desplegar, **CORS desaparece por completo** (ambos corren bajo el mismo dominio). Ideal para paneles de administración internos o apps de bajo tráfico.
- **Automatización (Maven + Node)**:
  Usas el `frontend-maven-plugin`. Cuando ejecutas `mvn clean package`, Maven instalará Node.js, hará `npm install`, `npm run build` en la carpeta de React, y copiará el resultado a la carpeta estática de Spring, entregándote un `.jar` perfecto.

#### 3. El Problema del Enrutamiento (React Router vs Spring MVC)
- **El Problema** — Si empacas React dentro de Spring, React Router maneja las URLs (ej: `http://localhost:8080/dashboard`). Si el usuario recarga la página (F5) en esa URL, el navegador le pedirá a Spring el archivo `/dashboard`. Spring buscará un Controlador REST para `/dashboard`, fallará y devolverá **404 Not Found** (White Label Error).
- **La Solución (Forwarding)** — Hay que decirle a Spring: *"Si te piden algo que NO sea una API (`/api/**`) y no es un archivo estático conocido, NO devuelvas 404, devuelve el `index.html` de React y deja que React Router resuelva la ruta en el cliente"*.
- **Código (El Controlador Salvador)**:
  ```java
  @Controller
  public class ReactForwardingController {
  
      // Atrapa cualquier ruta que no tenga punto (como .js o .css) y no empiece por /api
      @RequestMapping(value = "/{path:[^\\.]*}")
      public String forwardToFrontend() {
          // 'forward:/' carga el index.html base sin cambiar la URL del navegador
          return "forward:/"; 
      }
  }
  ```

#### 4. Integración Segura (JWT) en el Frontend (React)
- **Qué es** — Tras hacer login, Spring devuelve el token JWT. En React debes interceptar las futuras llamadas para adjuntarlo.
- **Código en React (Axios)**:
  ```javascript
  import axios from 'axios';
  
  const api = axios.create({
      baseURL: '/api' // En prod relativo al dominio. En dev el proxy lo atrapa.
  });
  
  api.interceptors.request.use((config) => {
      const token = localStorage.getItem('jwt_token');
      if (token) {
          config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
  });
  
  export default api;
  ```

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| 404 en el Front al refrescar página (F5) | Despliegue Empaquetado. Spring Boot busca un EndPoint y no existe. | Implementar el `ReactForwardingController` (Punto 3). |
| Vulnerabilidad XSS por `localStorage` | Si tu aplicación es vulnerable a Cross-Site Scripting, los hackers robarán el JWT leyendo `localStorage`. | Si es un sistema bancario o de altísima seguridad, usa **HttpOnly Cookies** en lugar de enviar el token en el JSON. Spring crea la Cookie con flag `HttpOnly`, el navegador la guarda y la envía automáticamente en cada Request sin que el código de React (JavaScript) pueda leerla jamás. |
| CORS en Localhost (Desarrollo) | Ejecutas React en 3000, Spring en 8080, e intentas comunicarlos directamente. | Usa el Proxy de Vite o Create-React-App. En `vite.config.js`: `server: { proxy: { '/api': 'http://localhost:8080' } }`. Así tu fetch en React es simplemente `fetch('/api/users')`. |

---

### Ejercicios
1. Crea un API REST en Spring Boot con un endpoint `@GetMapping("/api/hello")` que devuelva un saludo.
2. Inicia un proyecto en React usando Vite (`npm create vite@latest frontend -- --template react`).
3. Configura el archivo `vite.config.js` para que el proxy redirija las llamadas `/api` a `http://localhost:8080`.
4. En el `App.jsx` de React, haz un `fetch('/api/hello')`, y muestra el resultado en pantalla. Inicia ambos servidores y comprueba que funciona (Sin configurar CORS en Spring!).
5. (Avanzado): Usa `frontend-maven-plugin` para copiar el build de React (`dist/`) a `src/main/resources/static/` durante el empaquetado y logra un ejecutable único.

### Cómo ejecutar
```bash
# Terminal 1 - Iniciar Backend
cd 43-spring-react/backend
mvn spring-boot:run

# Terminal 2 - Iniciar Frontend (Desarrollo)
cd 43-spring-react/frontend
npm install
npm run dev
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `backend/config/ForwardingConfig.java` | Resolución de URLs de React Router hacia el `index.html`. |
| `backend/pom.xml` | Configuración opcional del `frontend-maven-plugin`. |
| `frontend/vite.config.js` | Configuración del Proxy de desarrollo para evadir CORS. |
| `frontend/src/api/axios.js` | Interceptor de peticiones inyectando Autenticación. |
