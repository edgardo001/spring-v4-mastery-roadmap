## 57 — AI Integration (Manual con RestClient)

### Proposito
Integrar un LLM (OpenAI-compatible) desde Spring Boot 4 **sin usar Spring AI**, llamando directamente al endpoint `/chat/completions` con `RestClient`. Sirve para entender que hay debajo del framework antes de adoptarlo (modulo 58).

### Problema que resuelve
Los tutoriales suelen mostrar Spring AI como magia. Pero cuando algo falla — timeout, retry, coste, prompt mal formado — hay que saber que ocurre a nivel HTTP. Este modulo hace la llamada "a mano".

### Como lo resuelve
- `LlmProperties` (`@ConfigurationProperties`) tipa la config (`llm.api-key`, `llm.base-url`, `llm.model`).
- `LlmClient` usa `RestClient.builder().baseUrl(...)` (MEMORY.md: en Boot 4 no hay `RestClient.Builder` autoconfigurado).
- `ChatController` expone `POST /api/chat?message=X`, con 503 si falta la API key.

### Por que aprenderlo
En produccion nunca se llama al LLM a pelo: hay retries con backoff, tracking de tokens/coste, guardrails de contenido, y timeouts agresivos. Entender la llamada base es el primer paso.

```mermaid
flowchart LR
    C["Cliente HTTP"] -->|POST /api/chat| CTRL["ChatController"]
    CTRL -->|falta api-key| E503["503 LLM not configured"]
    CTRL -->|api-key OK| CL["LlmClient"]
    CL -->|POST /chat/completions| API["OpenAI-compatible API"]
    API -->|choices[0].message.content| CL
    CL -->|reply| CTRL
    CTRL -->|200 JSON| C
```

### Glosario Basico
- **LLM**: Large Language Model (GPT, Claude, Llama...).
- **`/chat/completions`**: endpoint estandar OpenAI para chat.
- **`choices[0].message.content`**: donde vive la respuesta textual.
- **`RestClient`**: cliente HTTP fluido de Spring 6+/Boot 3.2+.
- **`MockRestServiceServer`**: interceptor de peticiones para tests, parte de `spring-test`.

### Conceptos
- **Configuracion tipada**: `LlmProperties` reemplaza `@Value` disperso.
- **RestClient manual**: `RestClient.builder().baseUrl(...).build()` — en Boot 4 el Builder NO es un bean autoconfigurado.
- **Body como Map**: `Map.of("model", ..., "messages", List.of(Map.of("role","user","content",msg)))`.
- **Extraccion de respuesta**: casting explicito porque el JSON llega como `Map<String, Object>`.
- **Fail-fast**: 503 si `llm.api-key` esta vacia, en vez de estrellarse al llamar al proveedor.

### Antes vs Ahora (Java 8 → Java 21)

| Concepto | Antes (Java 8 + RestTemplate) | Ahora (Java 21 + RestClient) |
|---|---|---|
| Cliente HTTP | `new RestTemplate()` + `HttpEntity` + `HttpHeaders` | `RestClient.builder().baseUrl(...).build()` |
| Body literal | `new HashMap<>() {{ put("k","v"); }}` | `Map.of("k","v")` |
| Chequeo vacio | `s == null \|\| s.trim().isEmpty()` | `s == null \|\| s.isBlank()` |
| Config tipada | `@Value("${llm.api-key}") String key;` | `@ConfigurationProperties(prefix="llm")` |
| Retry | Manual con `for` + `Thread.sleep` | `Retry.decorateSupplier(retry, () -> client.chat(...))` (modulo 30) |
| Cost tracking | Contar tokens a mano al final | Interceptor de `RestClient` que suma `usage.total_tokens` |

### FAQ del Alumno
- **¿Por que no uso Spring AI directamente?** Porque hasta que no entiendas la peticion HTTP cruda (body, headers, extraccion), Spring AI es una caja negra. El modulo 58 aborda Spring AI.
- **¿Como consigo la API key?** [platform.openai.com](https://platform.openai.com) → API keys. Empieza con `sk-...`.
- **¿Y si uso otro proveedor (Anthropic, local, Ollama)?** Muchos exponen la misma API OpenAI-compatible. Cambia `llm.base-url` y `llm.model`. Ollama por ejemplo: `http://localhost:11434/v1`.
- **¿Por que 503 y no 500?** Porque "servicio no configurado" es una indisponibilidad conocida, no un bug. 503 (Service Unavailable) es semanticamente correcto.
- **¿Streaming?** Real streaming (SSE) necesita WebFlux/`WebClient`. Aqui hay `streamAsBlock` como placeholder.
- **¿Por que tantos `@SuppressWarnings("unchecked")`?** Porque el JSON se deserializa a `Map<String, Object>` generico y el cast a `List<Map<...>>` no es verificable en tiempo de compilacion.

### Ejercicios
1. Anade un interceptor al `RestClient` que loguee `usage.total_tokens` de cada respuesta.
2. Extrae el `system prompt` a `LlmProperties.systemMessage` y anadelo como primer mensaje.
3. Implementa reintentos con backoff exponencial (3 intentos) usando el patron del modulo 30.

### Como ejecutar
```bash
# Build
./build.sh          # Git Bash
.\build.ps1         # PowerShell

# Ejecucion (Linux/Mac/Git Bash):
OPENAI_API_KEY=sk-... java -jar target/ai-integration-1.0.0.jar

# Ejecucion (PowerShell):
$env:OPENAI_API_KEY = "sk-..."
java -jar target\ai-integration-1.0.0.jar

# Peticion de prueba:
curl -X POST "http://localhost:8080/api/chat?message=hola"
```

Si NO se define `OPENAI_API_KEY`, el endpoint responde 503 con `{"error":"LLM not configured"}`. Perfecto para desarrollo y CI sin costes.

### Archivos del Proyecto

| Archivo | Proposito |
|---|---|
| `pom.xml` | Coordenadas Maven (`ai-integration-1.0.0`), starter web + test. |
| `src/main/java/.../AiIntegrationApplication.java` | Punto de entrada Spring Boot. |
| `src/main/java/.../config/LlmProperties.java` | `@ConfigurationProperties(prefix="llm")`. |
| `src/main/java/.../client/LlmClient.java` | Llamada manual a `/chat/completions` con RestClient. |
| `src/main/java/.../controller/ChatController.java` | `POST /api/chat?message=X`. |
| `src/main/resources/application.yml` | `llm.api-key: ${OPENAI_API_KEY:}`, defaults. |
| `src/test/.../AiIntegrationApplicationTests.java` | `contextLoads`. |
| `src/test/.../client/LlmClientTest.java` | `MockRestServiceServer` — stub POST y parseo. |
| `src/test/.../controller/ChatControllerTest.java` | MockMvc standalone: 503 sin key, 200 con mock. |
| `build.sh` / `build.ps1` | Toolchain portable (JDK 21 + Maven 3.9.16). |

### Nota — Modulo 58
El modulo 58 introduce **Spring AI** (ChatClient, prompts, embeddings, vector stores). Aqui vimos "el motor a la vista"; alli veremos el framework encima.
