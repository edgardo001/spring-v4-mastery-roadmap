## 30 — Tolerancia a Fallos (Resilience4j)

### Propósito
Aprender a proteger tu aplicación contra fallos en servicios de terceros o caídas de otros microservicios utilizando patrones de resiliencia como Circuit Breaker (Cortacircuitos), Retry (Reintentos) y Rate Limiter (Limitador de Tasa) con la librería estándar de la industria: **Resilience4j**.

### Problema que resuelve
En arquitecturas distribuidas y de microservicios, el fallo es la norma, no la excepción. Si tu `Servicio de Pagos` consulta a la API de `Stripe` y Stripe está caído (responde en 30 segundos o arroja 500):
- **Efecto Dominó:** Tu aplicación se quedará esperando 30 segundos por cada cliente. Los hilos de Tomcat se agotarán, tu servidor consumirá toda la memoria, y tu propia aplicación se caerá (Cascading Failure), aunque el problema sea de Stripe.
- **Mala Experiencia:** El usuario ve un error críptico o la pantalla se queda cargando infinitamente.

### Cómo lo resuelve
Resilience4j envuelve tus llamadas a APIs externas en "protectores":
- **Circuit Breaker:** Si detecta que Stripe falla el 50% de las veces, "Abre" el circuito. Las siguientes llamadas ya no intentarán ir a Stripe (para no esperar 30s), sino que fallarán instantáneamente (Fail-fast), salvando los hilos de tu servidor.
- **Fallback (Respaldo):** Si falla el servicio, puedes configurar una respuesta por defecto (ej: "Servicio no disponible, intente más tarde").
- **Retry:** Si falla por un error temporal de red, automáticamente reintenta 3 veces antes de rendirse.

### Por qué aprenderlo
La resiliencia separa un sistema de juguete de un sistema de calidad empresarial. Spring Cloud recomendaba Hystrix en el pasado (ya obsoleto). Hoy, Resilience4j es la herramienta oficial y es conocimiento obligatorio en el diseño de microservicios (Microservices Design Patterns).

```mermaid
graph TD
    A["Cliente Llama al Endpoint"] --> B["Resilience4j (Circuit Breaker)"]
    
    B --> C{"Estado del Circuito"}
    C -->|"CERRADO (Normal)"| D["Llama a la API Externa"]
    C -->|"ABIERTO (Fallo detectado)"| E["Fallback (Retorna Default)"]
    
    D -->|"Falla muchas veces"| F["El Circuito se ABRE"]
    F --> E
    
    D -->|"Éxito"| G["Retorna Resultado Real"]
```

---

### Glosario Básico

#### `Circuit Breaker`
Máquina de estados con 3 estados: 
- `CLOSED` (Cerrado): Todo funciona normal. Deja pasar las peticiones.
- `OPEN` (Abierto): Detectó muchos fallos. Corta la conexión y rechaza todas las peticiones instantáneamente.
- `HALF_OPEN` (Medio-abierto): Tras un tiempo, deja pasar unas pocas peticiones para ver si el servicio externo ya se recuperó. Si tienen éxito, se cierra; si fallan, se vuelve a abrir.

#### `Retry`
Mecanismo que captura una excepción y vuelve a ejecutar el código automáticamente `N` veces, opcionalmente esperando `X` segundos entre intentos (Backoff).

#### `Rate Limiter`
Limita la cantidad de peticiones que se pueden hacer a un método en un periodo de tiempo. Ej: "Máximo 5 peticiones por segundo".

#### `Fallback`
Un método alternativo que se ejecuta cuando el Circuit Breaker está abierto o cuando se acaban los reintentos. Garantiza que el usuario siempre reciba una respuesta controlada, no un Stack Trace.

---

### Conceptos

#### 1. Configuración Básica y Circuit Breaker
- **Qué es** — Envolver la llamada externa (`RestClient` o `RestTemplate`) con la anotación `@CircuitBreaker`.
- **Código** — Ejemplo de consumo con protección:
  ```xml
  <!-- En pom.xml -->
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
  <!-- Obligatorio para que funcionen las anotaciones AOP de Resilience4j -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-aop</artifactId>
  </dependency>
  ```
  
  ```java
  @Service
  @Slf4j
  public class InventoryService {
  
      private final RestClient restClient;
  
      public InventoryService(RestClient restClient) {
          this.restClient = restClient;
      }
  
      // Aplica el patrón usando la configuración "inventoryService"
      // Si falla, llama al método "defaultInventory"
      @CircuitBreaker(name = "inventoryService", fallbackMethod = "defaultInventory")
      public InventoryResponse checkStock(String productId) {
          log.info("Consultando stock a servicio externo para: {}", productId);
          // Llamada real que podría fallar
          return restClient.get()
                  .uri("http://localhost:9090/api/inventory/{id}", productId)
                  .retrieve()
                  .body(InventoryResponse.class);
      }
      
      // Mismo nombre, mismos parámetros + la excepción que causó la falla
      public InventoryResponse defaultInventory(String productId, Exception e) {
          log.warn("El servicio externo falló. Retornando stock por defecto. Error: {}", e.getMessage());
          // Respuesta de salvavidas (Fallback)
          return new InventoryResponse(productId, 0, "No Disponible por el momento");
      }
  }
  ```

#### 2. Configurando las Reglas del Circuito (YAML)
- **Qué es** — Decidir cuándo se abre el circuito. ¿Después de 3 errores? ¿Si el 50% de 10 peticiones fallan?
- **Código** — En el `application.yml`:
  ```yaml
  resilience4j:
    circuitbreaker:
      instances:
        inventoryService:
          # Tipo de ventana: Basado en cantidad de peticiones (COUNT_BASED) o tiempo (TIME_BASED)
          sliding-window-type: COUNT_BASED
          sliding-window-size: 10 # Evalúa las últimas 10 peticiones
          failure-rate-threshold: 50 # Si el 50% fallan (5 peticiones), el circuito se ABRE
          wait-duration-in-open-state: 10s # Se queda ABIERTO por 10 segundos antes de intentar HALF_OPEN
          permitted-number-of-calls-in-half-open-state: 3 # En HALF_OPEN, deja pasar 3 peticiones de prueba
          minimum-number-of-calls: 5 # Espera al menos 5 peticiones antes de empezar a calcular porcentajes
  ```
- **Analogía** — El interruptor de luz de tu casa (Breaker). Si hay un corto circuito en un enchufe, se "baja" (Abre) para evitar que la casa se incendie. No puedes usar la luz por un rato, pero el resto de tu casa sigue funcionando perfectamente.

#### 3. Implementando Retry (Reintentos Inteligentes)
- **Qué es** — Si la red parpadea por 1 segundo, la petición falla. En vez de mostrar error al usuario, intentas de nuevo.
- **Código**:
  ```java
  @Service
  @Slf4j
  public class PaymentService {
      
      // Se puede combinar con CircuitBreaker!
      @Retry(name = "paymentRetry", fallbackMethod = "paymentFailed")
      public String processPayment(Double amount) {
          log.info("Intentando cobrar ${}...", amount);
          // Simular fallo
          if(Math.random() > 0.1) { 
              throw new RuntimeException("Error temporal de red al cobrar"); 
          }
          return "Cobro exitoso";
      }
      
      public String paymentFailed(Double amount, Exception e) {
          return "Fallo definitivo tras reintentos. Intente usar otra tarjeta.";
      }
  }
  ```
  ```yaml
  resilience4j:
    retry:
      instances:
        paymentRetry:
          max-attempts: 3 # Intenta 3 veces en total (1 original + 2 reintentos)
          wait-duration: 2s # Espera 2 segundos entre cada intento
  ```

#### 4. Monitoreando la Salud con Actuator
- **Qué es** — Cuando expones los endpoints de Actuator (`/actuator/health`), Resilience4j inyecta automáticamente el estado de tus Circuit Breakers.
- **Por qué importa** — Los sistemas de monitoreo como Prometheus pueden leer esto y enviarte una alerta de Slack: *"Alerta: El CircuitBreaker 'inventoryService' está OPEN!"*.
- **Código** — `application.yml`:
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health, prometheus, circuitbreakers
    health:
      circuitbreakers:
        enabled: true # Muestra el detalle CLOSED/OPEN en el JSON de /health
  ```

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| El CircuitBreaker no hace nada | Falta la dependencia de AOP (`spring-boot-starter-aop`). | Las anotaciones de Resilience4j funcionan con AOP. Si falta la dependencia, Spring ignora la anotación silenciosamente. |
| FallbackMethod not found | La firma del método fallback no coincide exactamente con el original. | El método de fallback debe tener exactamente los mismos argumentos que el original, y añadir un parámetro final `Exception e` (o el tipo específico de excepción). |
| Llamadas internas no protegidas | Llamar a un método con `@CircuitBreaker` desde OTRO método en la *misma clase*. | Por limitaciones de los Proxies AOP (Self-Invocation), debes inyectar la dependencia o mover el método a otra clase. |
| Excepciones que no deberían abrir el circuito | Una API externa devuelve 404. Eso es un error de negocio, no un fallo del servidor. | Configurar `ignore-exceptions:` en el YAML para excluir `ResourceNotFoundException` o similares del conteo de fallas. |

---

### Ejercicios
1. Crea un proyecto con `spring-cloud-starter-circuitbreaker-resilience4j`, `aop`, y `actuator`.
2. Crea un `@Service` con un método que siempre lance una excepción simulando un servicio caído. Anótalo con `@CircuitBreaker(name="test")`.
3. Configura en el `application.yml` que el circuito se abra tras 2 errores seguidos (`sliding-window-size: 2`, `minimum-number-of-calls: 2`).
4. Haz un Endpoint REST que llame a este servicio. Ejecuta `curl` 3 veces. 
5. Observa los logs: La petición 1 y 2 ejecutan el código y fallan. La petición 3 ya NO ejecuta el código, arroja un error inmediato o salta al `fallbackMethod`.
6. Ve a `http://localhost:8080/actuator/health` y verifica que el estado del CircuitBreaker es `OPEN`.

### Cómo ejecutar
```bash
cd 30-resilience4j
mvn spring-boot:run

# Llama repetidamente para observar cómo se ABRE el circuito
curl http://localhost:8080/api/checkout/1
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Dependencias de Resilience4j, AOP y Actuator. |
| `application.yml` | Reglas del CircuitBreaker (umbrales, ventanas de tiempo) y Retry. |
| `service/InventoryService.java` | Lógica de integración externa con protección de Circuit Breaker y Fallback. |
| `service/PaymentService.java` | Lógica protegida con `@Retry`. |
