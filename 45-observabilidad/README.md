## 45 — Observabilidad: Métricas y Logs (Prometheus, Grafana, ELK/Loki)

### Propósito
Completar la "Trinidad de la Observabilidad" (Trazas, Métricas y Logs). Aprender a exportar métricas de la JVM y del negocio hacia Prometheus, crear dashboards visuales e impresionantes con Grafana, y centralizar los archivos de log de múltiples microservicios para búsqueda masiva.

### Problema que resuelve
En el Módulo 35 aprendimos a rastrear una petición (Trazas), pero:
- **Métricas:** Si tu servidor de repente se queda sin Memoria RAM o la base de datos se queda sin conexiones en el Connection Pool (HikariCP), las Trazas no te ayudarán. Necesitas ver una gráfica del consumo a lo largo del tiempo para predecir si vas a colapsar en la próxima hora (Capacidad).
- **Logs:** Si tienes 10 microservicios corriendo en 50 contenedores Docker, no puedes hacer SSH a 50 máquinas para buscar un `NullPointerException`. 

### Cómo lo resuelve
1. **Spring Boot Actuator + Micrometer Registry:** Expone un endpoint (`/actuator/prometheus`) con métricas en tiempo real. **Prometheus** (una Base de Datos de series de tiempo) extrae (scrapea) esos datos cada 10 segundos.
2. **Grafana:** Se conecta a Prometheus y dibuja gráficos en tiempo real. Configuras una alarma: "Si la CPU supera el 90%, mándame un mensaje de Slack".
3. **Log Aggregation (Loki o ELK):** Se instala un agente (promtail/filebeat) que lee todos tus archivos de log y los envía a una base de datos central. Luego los buscas como si usaras Google: `{app="pagos"} |~ "Exception"`.

### Por qué aprenderlo
Desarrollar la aplicación es solo el 20% del trabajo. Mantenerla operando el 80%. Las empresas grandes tienen departamentos enteros de SRE (Site Reliability Engineers) que dependen de que los desarrolladores instrumenten correctamente sus aplicaciones con estas herramientas.

```mermaid
graph TD
    subgraph Tu App (Spring Boot)
        A[Actuator Endpoint<br/>/prometheus]
        B[Logs de Consola / Archivo<br/>.log]
    end
    
    subgraph Ecosistema Observabilidad
        P[(Prometheus<br/>Time-Series DB)]
        L[(Grafana Loki / ELK<br/>Logs DB)]
        G[Grafana<br/>Dashboards y Alertas]
    end
    
    P -->|Hace PULL cada 15s| A
    B -.->|Promtail hace PUSH| L
    
    G -->|Consulta PromQL| P
    G -->|Consulta LogQL| L
    
    style G fill:#f06595,color:#fff
    style P fill:#fcc419,color:#000
```

---

### Glosario Básico

#### `Métricas` (Metrics)
Datos numéricos puros a lo largo del tiempo. Ej: "CPU: 45%", "Peticiones HTTP: 100/seg", "Usuarios Registrados: 50".

#### `Prometheus`
Base de datos de series de tiempo (TSDB) open-source estandarizada. Trabaja por PULL: tu aplicación no le envía datos a Prometheus, es Prometheus el que "raspa" (scrapea) tu app cada N segundos.

#### `Grafana`
El frontend visual definitivo. No guarda datos. Solo se conecta a Prometheus (y otras BDs) para dibujar gráficos hermosos (Barras, Gauges, Líneas).

#### `ELK Stack` / `PLG Stack`
- **ELK:** Elasticsearch, Logstash, Kibana (Stack clásico y muy pesado de gestión de Logs).
- **PLG:** Prometheus, Loki, Grafana (Stack moderno, ultra ligero, muy utilizado en Kubernetes).

---

### Conceptos

#### 1. Configurando el Endpoint de Prometheus en Spring Boot
- **Qué es** — Actuator (Módulo 32) por defecto es seguro y oculta todo. Debes activarlo e instalar la librería de adaptación a Prometheus.
- **Código**:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
  <dependency>
      <groupId>io.micrometer</groupId>
      <artifactId>micrometer-registry-prometheus</artifactId>
  </dependency>
  ```
  ```yaml
  management:
    endpoints:
      web:
        exposure:
          include: health,info,prometheus # Abre el endpoint de Prometheus
    metrics:
      tags:
        application: "mi-ecommerce" # IMPORTANTE: Permite filtrar en Grafana
  ```
  Al acceder a `http://localhost:8080/actuator/prometheus`, verás un texto extraño (El formato que entiende Prometheus):
  `jvm_memory_used_bytes{area="heap",id="G1 Eden Space",} 1.2582912E7`

#### 2. Creando Métricas de Negocio Personalizadas
- **Qué es** — Aparte de RAM y CPU, quieres saber cuántos "Pedidos Exitosos" ha procesado tu app.
- **Código** — Inyección de `MeterRegistry`:
  ```java
  @Service
  public class PedidoService {
  
      private final Counter pedidosExitosos;
      private final Timer tiempoProcesamiento;
  
      public PedidoService(MeterRegistry registry) {
          // Contador: Solo sube
          this.pedidosExitosos = Counter.builder("negocio.pedidos.creados")
                  .description("Total de pedidos creados con éxito")
                  .register(registry);
                  
          // Timer: Mide latencias (Cuánto tardan los procesos en ms)
          this.tiempoProcesamiento = Timer.builder("negocio.pedidos.tiempo")
                  .register(registry);
      }
  
      public void crearPedido(Pedido pedido) {
          tiempoProcesamiento.record(() -> {
              // Lógica de guardar en BD...
              pedidosExitosos.increment(); // Aumenta el contador de Grafana
          });
      }
  }
  ```

#### 3. Agregación de Logs (Logging Estructurado / JSON)
- **El Problema:** Si escribes tus logs así: `2024-05-10 INFO El usuario 5 compró algo`, Loki o ElasticSearch tendrán problemas para parsear e indizar esa cadena de texto.
- **La Solución:** Escribir los logs en formato JSON.
- **Código** — (Uso de `Logback-Logstash`):
  ```xml
  <dependency>
      <groupId>net.logstash.logback</groupId>
      <artifactId>logstash-logback-encoder</artifactId>
      <version>7.3</version>
  </dependency>
  ```
  Crea un archivo `logback-spring.xml` en `src/main/resources`:
  ```xml
  <configuration>
      <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
          <encoder class="net.logstash.logback.encoder.LogstashEncoder" />
      </appender>
      <root level="INFO">
          <appender-ref ref="CONSOLE" />
      </root>
  </configuration>
  ```
  Ahora los logs salen en JSON. Herramientas como Promtail pueden enviarlo a Loki y tú podrás hacer consultas precisas: `buscar donde userId = 5`.

#### 4. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Prometheus Metrics con valores "Rotos" (Ej. URLs largas llenan la RAM) | Si tu endpoint tiene un ID dinámico `/api/users/123`, Micrometer puede generar una métrica separada para el user 1, user 2... user 1,000,000. Esto revienta la memoria (Cardinality Explosion). | Usar variables de path `@PathVariable` en Spring MVC. Spring automáticamente colapsará las métricas bajo la etiqueta `URI: /api/users/{id}`. Cuidado con usar filtros crudos (`Filter`) que no tengan este contexto. |
| Exceso de Logs de nivel DEBUG/TRACE | Enviar demasiados datos a ELK/Loki cuesta dinero y disco duro. | Mantén el nivel global en `INFO`. Usa la anotación o propiedad de Actuator para cambiar dinámicamente el nivel de log a `DEBUG` temporalmente sin reiniciar la app si estás buscando un bug. |
| El `scrape` de Prometheus tumba la app | Prometheus raspa cada 1 segundo (muy agresivo). | Por defecto, cada 15 o 30 segundos es el estándar. Ajusta el `scrape_interval` en la configuración de Prometheus (`prometheus.yml`). |

---

### Ejercicios
1. Crea un proyecto web básico y añade `micrometer-registry-prometheus`.
2. Habilita el endpoint expuesto. Usa un navegador para entrar a `/actuator/prometheus`.
3. Levanta la infraestructura visual. Se provee un `docker-compose.yml` que contiene un servidor Prometheus y un Grafana.
4. (Paso a paso Docker): El archivo `prometheus.yml` enlazado instruye a Prometheus a raspar `host.docker.internal:8080`.
5. Entra a Grafana (`localhost:3000`, admin/admin), agrega Prometheus como "Data Source". Luego importa un dashboard pre-fabricado de la comunidad buscando el ID "4701" (Spring Boot Observability). Contempla todas las métricas de tu JVM de inmediato.

### Cómo ejecutar
```bash
# 1. Levantar DBs de Observabilidad y Dashboards
cd 45-observabilidad/infra
docker-compose up -d

# 2. Correr aplicación Spring
cd ../app
mvn spring-boot:run
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `infra/docker-compose.yml` | Contenedores de Prometheus y Grafana. |
| `infra/prometheus.yml` | Reglas de raspado (Scrape config). Instruye a Prom a leer la app Spring. |
| `app/service/NegocioService.java` | Inyección de `MeterRegistry` y creación de Métricas Custom (Counters y Timers). |
| `app/resources/logback-spring.xml` | Configuración de logs en formato estructurado (JSON). |
