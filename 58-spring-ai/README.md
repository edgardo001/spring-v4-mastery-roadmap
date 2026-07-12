## 58 — Spring AI

### Proposito
Introducir el framework oficial **Spring AI**: la abstraccion `ChatClient` con API fluida, `Advisors` (memoria, logging, RAG), y la promesa de auto-configuracion multivendor (OpenAI, Anthropic, Ollama, Bedrock) sin cablear cada API a mano como en el modulo 57.

### Variante usada en este modulo: **DEMO simulado**

> **Compatibilidad Spring AI 1.x vs Spring Boot 4.1.0**
>
> Spring AI 1.0 GA (mayo 2025) y la linea 1.x estan compiladas contra **Spring Boot 3.x / Spring Framework 6.x**. Al momento de generar este modulo no existe una release estable de `spring-ai-*` compatible con Spring Boot 4.1.0 (Spring Framework 7). Forzar la dependencia `org.springframework.ai:spring-ai-openai-starter:1.0.0` sobre Boot 4.1.0 causa conflictos de clases y auto-configuracion.
>
> Para no bloquear el aprendizaje, este modulo publica **`ChatClientSimulator`**: un `@Component` que imita fielmente la API idiomatica de Spring AI:
>
> ```java
> chatClient.prompt()
>          .system("Eres un asistente medico")
>          .user("Como se trata la gripe?")
>          .call()
>          .content();
> ```
>
> Cuando la fundacion publique `spring-ai` para Boot 4, migrar es trivial: reemplazar el bean `ChatClientSimulator` por `org.springframework.ai.chat.client.ChatClient` (el codigo llamador no cambia).

### Antes (modulo 57) vs Ahora (modulo 58)

**Modulo 57 — integracion manual con `RestClient`:**
```java
final ChatRequest request = ChatRequest.builder()
        .systemPrompt("Eres un asistente medico")
        .userPrompt(question)
        .temperature(0.0)
        .model("gpt-4o-mini")
        .build();

final ChatResponse resp = openAiRestClient.post()
        .uri("/chat/completions")
        .body(request)
        .retrieve()
        .body(ChatResponse.class);

final String content = resp.choices().get(0).message().content();
```
- Tu escribes DTOs de request/response que espejan la API de cada vendor.
- Tu manejas retries, headers, parseo, `tokens_used`.
- Cambiar de OpenAI a Anthropic requiere re-escribir el cliente y sus DTOs.

**Modulo 58 — Spring AI (real cuando compile en Boot 4) o simulador equivalente:**
```java
final String content = chatClient.prompt()
        .system("Eres un asistente medico")
        .user(question)
        .temperature(0.0)
        .call()
        .content();
```
- Zero DTOs. Zero HTTP. Zero parseo.
- Cambiar de vendor = cambiar el starter (`spring-ai-openai-starter` -> `spring-ai-anthropic-starter`) y una property.
- Advisors se conectan tipo interceptor: `chatClient.addAdvisor(memoryAdvisor)` para historial, `RagAdvisor` para retrieval, `LoggingAdvisor` para observabilidad. En el simulador exponemos `addAdvisor(UnaryOperator<String>)` como didactica minima.

### Concepto clave: Advisors

Un `Advisor` en Spring AI es un interceptor de la cadena request/response. Casos tipicos:
- **`MessageChatMemoryAdvisor`** — inyecta el historial de la conversacion en cada llamada.
- **`QuestionAnswerAdvisor`** — RAG built-in: busca en un `VectorStore` y agrega el contexto al prompt.
- **`SafeGuardAdvisor`** — moderacion / PII scrubbing.
- **`SimpleLoggerAdvisor`** — traza request/response.

En este modulo el simulador acepta advisors como `UnaryOperator<String>` que post-procesan el contenido: suficiente para captar la idea de "cadena de interceptores" sin arrastrar la implementacion real.

### Endpoints
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| `POST` | `/api/chat` | Body: `{ "message": "...", "system": "..." (opcional) }`. Devuelve `{ "content": "..." }`. |

### Como ejecutar
```bash
# Windows PowerShell
./build.ps1

# Linux / macOS / Git Bash
./build.sh

java -jar target/spring-ai-1.0.0.jar

# Prueba
curl -X POST http://localhost:8080/api/chat \
     -H "Content-Type: application/json" \
     -d '{"message":"Explicame RAG en una frase"}'
```

### Archivos del proyecto
| Archivo | Proposito |
|---------|-----------|
| `pom.xml` | Spring Boot 4.1.0 + `spring-boot-starter-web` + test. Sin dependencia real de `spring-ai` (ver FAQ). |
| `application.yml` | Defaults del simulador (`system`, `model`, `temperature`). |
| `SpringAiApplication.java` | Bootstrap. |
| `chat/ChatClientSimulator.java` | API fluida `prompt().system().user().call().content()` + soporte de advisors. |
| `chat/ChatController.java` | `POST /api/chat` que consume el simulador. |
| `SpringAiApplicationTests.java` | `contextLoads`. |
| `chat/ChatClientSimulatorTest.java` | Unit test puro del builder fluido y advisors. |
| `chat/ChatControllerTest.java` | MockMvc standalone del endpoint. |

### FAQ

**Por que no uso Spring AI real?**
La linea 1.x depende de Spring Framework 6 / Boot 3. Este roadmap se ejerce sobre Boot 4.1.0 (Spring Framework 7). Cuando Spring AI publique una version compatible (probablemente 1.2.x o 2.x) solo hay que:
1. Anadir `<dependency>org.springframework.ai:spring-ai-openai-starter</dependency>` al `pom.xml`.
2. Configurar `spring.ai.openai.api-key` en `application.yml`.
3. Reemplazar `ChatClientSimulator` por el bean `ChatClient` autoconfigurado.
4. Borrar el simulador. El resto del codigo (controller, tests salvo el simulator test) queda igual.

**El simulador llama a un LLM real?**
No. Genera una respuesta deterministica que refleja el prompt. El objetivo es aprender la **forma** (API fluida + advisors) sin depender de una API key ni de red.

**Que aporta `ChatClient` frente al `RestClient` manual del modulo 57?**
- API fluida y auto-completable en el IDE.
- Portabilidad entre vendors (mismo codigo, otro starter).
- Advisors reutilizables (memoria, RAG, guardrails) como piezas Lego.
- `EmbeddingModel` y `VectorStore` unificados (PGVector, Redis, Milvus, Pinecone).
- Streaming, function calling (tools) y structured output (`.entity(MyRecord.class)`) integrados.

**Y RAG?**
En Spring AI real: `.advisors(new QuestionAnswerAdvisor(vectorStore))`. El simulador no incluye VectorStore; para practicar RAG usa el modulo 57 (pipeline manual). Cuando migres a Spring AI real puedes reemplazar todo ese pipeline por un advisor de una linea.

**Puedo forzar Spring AI 1.x aca?**
Puedes intentarlo (`<dependency>org.springframework.ai:spring-ai-openai-starter:1.0.0</dependency>`) pero probablemente falle la auto-configuracion por incompatibilidad con Spring Framework 7. Si te funciona en el momento en que leas esto: perfecto, reemplaza el simulador.
