## 47 — Spring Cloud Gateway

### Propósito
Aprender a implementar un API Gateway (Puerta de Enlace) como el punto de entrada único para toda tu arquitectura de microservicios, manejando el enrutamiento dinámico, filtros de seguridad centralizados y control de tráfico (Rate Limiting).

### Problema que resuelve
Tienes 10 microservicios (Ventas, Inventario, Usuarios) corriendo en puertos aleatorios y escalando dinámicamente.
- Tu frontend de React no puede memorizar las URLs de los 10 servicios.
- No quieres implementar la validación del Token JWT 10 veces en 10 proyectos distintos.
- Si un cliente abusivo hace 5000 peticiones por segundo al servicio de Ventas, lo tumba. ¿Cómo lo bloqueas antes de que llegue a Ventas?

### Cómo lo resuelve
**Spring Cloud Gateway** se sitúa frente a todos tus microservicios.
1. El Frontend siempre llama al Gateway (`http://api.tuempresa.com/ventas`).
2. El Gateway intercepta la llamada, lee la URL, busca la dirección real de "Ventas" en Eureka (Módulo 41) y redirige (enruta) el tráfico hacia allá.
3. El Gateway valida el JWT una sola vez. Si es inválido, rechaza la petición. Si es válido, la deja pasar.
4. Aplica reglas (Rate Limiting) con Redis: "Solo 10 peticiones por segundo por IP".

### Por qué aprenderlo
El API Gateway es el componente arquitectónico más importante en una red de microservicios. Es el "Guardia de Seguridad" y el "Recepcionista" de tu empresa. Spring Cloud Gateway es extremadamente rápido porque está construido sobre Spring WebFlux (Non-blocking), capaz de manejar decenas de miles de conexiones simultáneas.

```mermaid
graph TD
    A["Frontend / Móvil"] -->|Todas las peticiones| B["Spring Cloud Gateway"]
    
    subgraph Microservicios Privados (Ocultos a Internet)
        C["Servicio de Usuarios"]
        D["Servicio de Ventas"]
        E["Servicio de Inventario"]
    end
    
    B -->|Enrutamiento: /api/users/**| C
    B -->|Enrutamiento: /api/sales/**| D
    B -->|Enrutamiento: /api/inventory/**| E
    
    B -.->|Limita Tráfico (Rate Limiting)| F[(Redis Cache)]
    B -.->|Verifica Direcciones| G["Eureka Registry"]

    style B fill:#f03e3e,color:#fff
```

---

### Glosario Básico

#### `Route` (Ruta)
La unidad de construcción básica. Consiste en un ID, un URI de destino, y una colección de Predicados y Filtros.

#### `Predicate` (Predicado)
Una condición `if`. El Gateway evalúa la petición HTTP. Ej: "Si el Path empieza por `/api/ventas`" o "Si la cabecera X-Device es Mobile".

#### `Filter` (Filtro)
Código que intercepta y modifica el Request ANTES de enviarlo al microservicio, o modifica el Response ANTES de devolvérselo al cliente. Ej: "Añadir un Header", "Quitar un Query Param", "Validar JWT".

#### `WebFlux / Netty`
Spring Cloud Gateway NO usa Tomcat. Usa Netty y el stack Reactivo de Spring (WebFlux). Esto significa que **nunca** bloquea un hilo esperando respuestas, lo que lo hace perfecto para ser un proxy de alto rendimiento.

---

### Conceptos

#### 1. Configuración Básica de Rutas (application.yml)
- **Qué es** — Puedes configurar todo el enrutamiento sin escribir una sola línea de código Java.
- **Código**:
  ```xml
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-gateway</artifactId>
  </dependency>
  ```
  ```yaml
  spring:
    cloud:
      gateway:
        routes:
          - id: ventas-service
            uri: http://localhost:8081 # Destino
            predicates:
              - Path=/ventas/**        # Condición: Entrar por /ventas
            filters:
              - RewritePath=/ventas/(?<segment>.*), /$\{segment} # Le quita el '/ventas' antes de enviarlo
  ```

#### 2. Integración con Eureka (Load Balancing Automático)
- **Qué es** — En lugar de hardcodear `localhost:8081`, le decimos al Gateway que busque el servicio en Eureka (Módulo 41).
- **Código**:
  ```yaml
  spring:
    cloud:
      gateway:
        routes:
          - id: ventas-service
            uri: lb://servicio-ventas # lb:// significa "Load Balancer" buscando en Eureka
            predicates:
              - Path=/api/ventas/**
  ```

#### 3. Filtros Globales (Global Filters)
- **Qué es** — Código Java que se ejecuta para **TODAS** las rutas. Ideal para escribir logs de auditoría global o inyectar un Correlation ID.
- **Código**:
  ```java
  @Component
  @Slf4j
  public class LoggingGlobalFilter implements GlobalFilter, Ordered {
  
      @Override
      public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
          log.info("Petición entrante a la ruta: {}", exchange.getRequest().getURI());
          
          // Agrega un Header global a todas las peticiones que van hacia los microservicios
          ServerHttpRequest request = exchange.getRequest().mutate()
                  .header("X-Correlation-Id", UUID.randomUUID().toString())
                  .build();
                  
          // Continúa la cadena (Pasa al microservicio) y luego intercepta la respuesta
          return chain.filter(exchange.mutate().request(request).build())
              .then(Mono.fromRunnable(() -> {
                  log.info("Respuesta enviada con status: {}", exchange.getResponse().getStatusCode());
              }));
      }
  
      @Override
      public int getOrder() {
          return -1; // Prioridad alta (Se ejecuta primero)
      }
  }
  ```

#### 4. Rate Limiting con Redis (Filtro por Ruta)
- **Qué es** — Evitar ataques DDoS limitando a un usuario a 10 peticiones por segundo. El Gateway usa Redis (usando un script de LUA ultrarrápido) para llevar la cuenta en tiempo real.
- **Código**:
  ```yaml
  spring:
    cloud:
      gateway:
        routes:
          - id: inventario-service
            uri: lb://servicio-inventario
            predicates:
              - Path=/api/inventario/**
            filters:
              - name: RequestRateLimiter
                args:
                  redis-rate-limiter.replenishRate: 10  # Tickets por segundo (10 req/s)
                  redis-rate-limiter.burstCapacity: 20  # Ráfaga máxima permitida
                  key-resolver: "#{@ipKeyResolver}"     # El Bean que decide CÓMO identificar al usuario
  ```
  ```java
  @Configuration
  public class RateLimiterConfig {
      // Limitar por IP del cliente
      @Bean
      public KeyResolver ipKeyResolver() {
          return exchange -> Mono.just(
              exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
          );
      }
  }
  ```

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Excepción al mezclar Spring Web MVC con el Gateway | Agregaste `spring-boot-starter-web` (Tomcat) al proyecto del Gateway. | **NUNCA** pongas la dependencia `web` en un Gateway. El Gateway funciona exclusivamente sobre Spring WebFlux (Netty). Si pones Tomcat, la aplicación chocará y no arrancará. |
| Timeouts en peticiones largas | Un microservicio tarda 45 segundos en generar un reporte PDF, pero el Gateway le corta la conexión al cliente a los 30 segundos. | Ajustar el `timeout` de la conexión en la configuración del Gateway. Puedes configurarlo globalmente o por ruta (`metadata.response-timeout: 60000`). |
| CORS en Gateway vs Microservicio | Configuraste CORS en el microservicio de Ventas, y también en el Gateway. El navegador lanza un error "Access-Control-Allow-Origin multiple values". | **Regla de oro:** El CORS se configura **ÚNICAMENTE** en el API Gateway. Los microservicios detrás del Gateway deben estar limpios de configuraciones CORS. |

---

### Ejercicios
1. Crea un proyecto Spring Boot (Solo incluye `Gateway` y `Eureka Client`). **No incluyas Web**.
2. Configura el `application.yml` para enrutar el tráfico `/api/mock/**` hacia `https://jsonplaceholder.typicode.com`. 
3. Implementa el filtro global `LoggingGlobalFilter` que imprima en consola la URI entrante.
4. Levanta el proyecto. Usa Postman para llamar a `http://localhost:8080/api/mock/users`. 
5. Verifica en la consola que el filtro interceptó la petición, y observa cómo la respuesta es mágicamente devuelta desde JsonPlaceholder como si fuera de tu servidor.

### Cómo ejecutar
```bash
cd 47-spring-cloud-gateway
mvn spring-boot:run

# Probar la ruta mockeada
curl http://localhost:8080/api/mock/posts/1
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `application.yml` | Configuración de Predicates, Filters y Rutas hacia Eureka. |
| `config/LoggingGlobalFilter.java` | Filtro global para auditar peticiones y alterar el ServerWebExchange. |
| `config/RateLimiterConfig.java` | Bean `KeyResolver` para limitar peticiones usando la IP del cliente. |
