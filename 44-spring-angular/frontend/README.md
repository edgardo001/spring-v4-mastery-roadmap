# Frontend Angular v22 — Modulo 44

Este modulo NO trae el `package.json` ni `node_modules` generados. Se documenta
como crear el frontend con Angular CLI y consumir la API del backend Spring Boot.

## Generar el proyecto con Angular CLI

```bash
# Prerequisito: Node 22 y Angular CLI global
npm install -g @angular/cli@22

# Desde 44-spring-angular/frontend/
ng new frontend --routing --style=css --skip-git --standalone
cd frontend
```

Angular v22 usa componentes standalone por defecto (sin NgModules).

## Modelo TypeScript (match exacto con el Record de Java)

`src/app/models/task.dto.ts`:

```typescript
// Match 1:1 con com.springroadmap.springangular.domain.Task
export interface TaskDto {
  id: number;
  title: string;
  done: boolean;
}
```

## Servicio con HttpClient (RxJS Observables)

`src/app/services/task.service.ts`:

```typescript
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskDto } from '../models/task.dto';

@Injectable({ providedIn: 'root' })
export class TaskService {
  // Inyeccion de dependencias moderna (inject()) — analogo a @Autowired en Spring.
  private http = inject(HttpClient);

  // GET publico -> lista de tareas.
  getTasks(): Observable<TaskDto[]> {
    return this.http.get<TaskDto[]>('/api/tasks');
  }

  // POST autenticado -> el HttpInterceptor inyecta el header Authorization.
  createTask(task: Omit<TaskDto, 'id'>): Observable<TaskDto> {
    return this.http.post<TaskDto>('/api/tasks', task);
  }
}
```

## Componente que renderiza con AsyncPipe

`src/app/task-list.component.ts`:

```typescript
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { TaskService } from './services/task.service';
import { TaskDto } from './models/task.dto';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>Modulo 44 — Spring + Angular</h1>
    <ul>
      <!-- AsyncPipe se subscribe y se desuscribe automaticamente (no memory leaks) -->
      <li *ngFor="let t of tasks$ | async">
        {{ t.title }} — {{ t.done ? 'HECHO' : 'PENDIENTE' }}
      </li>
    </ul>
  `,
})
export class TaskListComponent {
  private taskService = inject(TaskService);
  tasks$: Observable<TaskDto[]> = this.taskService.getTasks();
}
```

## HttpInterceptor funcional (Angular 15+) — Basic Auth demo

`src/app/interceptors/auth.interceptor.ts`:

```typescript
import { HttpInterceptorFn } from '@angular/common/http';

// Basic Auth: usuario:contraseña en base64 (demo/demo123 del backend).
// En produccion se usa JWT desde localStorage/sessionStorage.
const AUTH = 'Basic ' + btoa('demo:demo123');

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith('/api')) {
    const cloned = req.clone({
      setHeaders: { Authorization: AUTH },
    });
    return next(cloned);
  }
  return next(req);
};
```

## Registrar HttpClient e Interceptor en `app.config.ts`

```typescript
import { ApplicationConfig } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
};
```

## Proxy de desarrollo (evita CORS)

`proxy.conf.json` en la raiz del proyecto Angular:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

`angular.json` — bajo `projects.frontend.architect.serve.options`:

```json
"proxyConfig": "proxy.conf.json"
```

O bien correr manualmente:

```bash
ng serve --proxy-config proxy.conf.json
```

Con el proxy, Angular en `localhost:4200` envia peticiones a `/api/tasks` y
Angular CLI las redirige a `localhost:8080`. El navegador nunca ve una peticion
cross-origin y CORS no aplica.

## Build de produccion

```bash
npm run build     # genera dist/frontend/browser/
```

En produccion `docker-compose.yml` sirve el `dist/frontend/browser/` con nginx
y hace `proxy_pass /api/` al servicio `backend` (ver `nginx.conf`).

## Antes vs Ahora

| Antes (AngularJS 1.x + JSP + WAR) | Ahora (Angular v22 + REST + JWT/Basic) |
|-----------------------------------|-----------------------------------------|
| Mismo WAR contenia backend + `<script src="angular.js">` + JSP | JAR backend independiente + estaticos Angular servidos por nginx |
| `$scope`, `$http`, `$rootScope`, digest cycle manual | `signals`, `HttpClient`, RxJS Observables, standalone components |
| Sin tipado — bugs de `undefined is not a function` en runtime | TypeScript + interfaces sincronizadas con records Java |
| Sesion server-side con `JSESSIONID` cookie | Basic Auth / JWT stateless en header `Authorization` |
| Un solo puerto -> no habia CORS | Angular:4200 vs Spring:8080 -> proxy.conf.json o CORS |
| Interceptors con `$httpProvider.interceptors.push(...)` | `HttpInterceptorFn` funcional en `provideHttpClient(withInterceptors(...))` |
| Deploy: build WAR + copiar a Tomcat | Deploy: `docker compose up` con backend + nginx separados |
