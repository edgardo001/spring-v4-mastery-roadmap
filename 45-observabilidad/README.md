# Modulo 45 - Observabilidad (Logs JSON + Micrometer Tracing + Prometheus)

Este modulo cubre las **3 patas de la observabilidad** en Spring Boot 4.1.0:

1. **Logs estructurados** en JSON via Logback + LogstashEncoder.
2. **Trazas distribuidas** via Micrometer Tracing + puente Brave (traceId/spanId automaticos en cada log).
3. **Metricas** en formato Prometheus via Micrometer Registry Prometheus.

## Convenciones

- Spring Boot 4.1.0, Java 21, Maven portable (`../apache-maven-3.9.16`).
- `groupId=com.springroadmap`, `artifactId=observabilidad`.
- Package `com.springroadmap.observability`.
- Artefacto: `target/observabilidad-1.0.0.jar`.
- Tests HTTP con `RestClient` + `@LocalServerPort` (NO `TestRestTemplate`, eliminado en Boot 4).
- Sin Lombok.

## Build

```bash
./build.sh      # Linux/Mac/WSL
./build.ps1     # Windows PowerShell
```

Produce `target/observabilidad-1.0.0.jar`. Ejecutar con:

```bash
java -jar target/observabilidad-1.0.0.jar
```

## Endpoints

| Endpoint                | Descripcion                                        |
|-------------------------|----------------------------------------------------|
| `GET /api/orders/{id}`  | Endpoint de negocio instrumentado con logs+tracing |
| `GET /actuator/health`  | Health check                                       |
| `GET /actuator/metrics` | Lista de metricas Micrometer                       |
| `GET /actuator/prometheus` | Metricas en formato text/plain para Prometheus  |
| `GET /actuator/info`    | Info de la app                                     |

## Antes vs Ahora

### Logs

**ANTES (Boot 2.x, logging texto plano):**
```
2024-01-01 12:00:00.123 INFO 1234 --- [nio-8080-exec-1] c.e.d.OrderController : Fetching order id=1
```
- Dificil de indexar en Elasticsearch/Loki.
- Sin correlacion entre servicios.
- El operador tiene que hacer regex sobre el texto.

**AHORA (Boot 4.1, LogstashEncoder + Micrometer Tracing):**
```json
{"@timestamp":"2026-07-10T12:00:00.123Z","level":"INFO","logger_name":"com.springroadmap.observability.controller.TracedController","message":"Fetching order id=1","traceId":"6f2a3c1b8e...","spanId":"3d4f...","app":"observabilidad"}
```
- Ingesta directa en Loki/Elastic/Datadog.
- `traceId` correlaciona logs con trazas de Tempo/Zipkin.
- Filtrar por `level`, `app`, `traceId` sin regex.

### Tracing

**ANTES:** `long t0 = System.currentTimeMillis(); ... log.info("tomo {}ms", t1-t0);` en cada metodo.

**AHORA:** Micrometer Tracing crea un span por request HTTP automaticamente, propaga el `traceparent` header en llamadas salientes (RestClient/WebClient), y agrega `traceId`/`spanId` al MDC de SLF4J. Cero codigo manual.

### Metricas

**ANTES:** contadores manuales en variables `AtomicLong`, expuestos ad-hoc.

**AHORA:** `MeterRegistry` inyectable, endpoint `/actuator/prometheus` listo para scrapear, binders JVM/HTTP/Tomcat por defecto.

## Piezas clave

- `src/main/resources/logback-spring.xml`: define appender `JSON_CONSOLE` con `LogstashEncoder` e incluye `traceId`/`spanId` desde el MDC.
- `application.yml`: expone `health,metrics,prometheus,info` y setea `management.tracing.sampling.probability: 1.0`.
- `TracedController`: `GET /api/orders/{id}` llama a `SlowService.slowCall()` (Thread.sleep 100ms). Cada log emitido lleva traceId/spanId.
- `SlowService`: simula latencia; sirve para ver spans hijos en el futuro (si se le agrega `@Observed` o llamadas HTTP salientes).

## FAQ Alumno

**P: No veo `traceId` en mis logs, aparecen vacios.**
R: Verifica que el starter `micrometer-tracing-bridge-brave` este en el pom. Sin puente, Micrometer no publica al MDC. Ademas la request debe pasar por el filtro HTTP de Boot: `contextLoads` no genera trazas porque no hay request.

**P: Por que `sampling.probability=1.0`?**
R: Para DEMO. En produccion usa `0.1` (10%) o menos para no saturar Tempo/Jaeger. En desarrollo 1.0 garantiza que veas cada request.

**P: Puedo cambiar el nombre del campo `message` en el JSON?**
R: Si, `LogstashEncoder` acepta `<fieldNames><message>msg</message></fieldNames>`. Ver docs de logstash-logback-encoder.

**P: Como envio los logs a Loki en vez de a consola?**
R: Se puede agregar un `com.github.loki4j.logback.Loki4jAppender` como appender adicional apuntando a `http://loki:3100/loki/api/v1/push`. En este modulo emitimos a consola; en produccion tipicamente promtail/vector recoge stdout del contenedor.

**P: Por que el test valida `jvm_memory` en `/actuator/prometheus`?**
R: Es una metrica que Micrometer registra por defecto via `JvmMemoryMetrics` binder. Nos garantiza que el endpoint esta activo y bien formado sin depender de metricas custom.

**P: Como veo el span de `slowCall()` como span hijo?**
R: Anotar el metodo con `@io.micrometer.observation.annotation.Observed` o instrumentar manualmente con `ObservationRegistry`. En este modulo mantuvimos el codigo minimo; el span raiz del request HTTP ya se genera.

**P: Que cambia respecto al modulo 35?**
R: Modulo 35 = actuator + micrometer registry Prometheus (metricas). Modulo 45 = logs JSON + tracing + prometheus (los 3 pilares).

## Stack opcional (docker-compose)

Ver `docker-compose.yml` + `ops/prometheus.yml`:

```bash
docker compose up -d
# Grafana en http://localhost:3000 (anon admin)
# Prometheus en http://localhost:9090
# Loki en http://localhost:3100
# Tempo en http://localhost:3200
java -jar target/observabilidad-1.0.0.jar
```

Configurar en Grafana datasources: Prometheus (`http://prometheus:9090`), Loki (`http://loki:3100`), Tempo (`http://tempo:3200`). Es un esqueleto: para produccion habria que agregar promtail y OTLP exporter en la app.
