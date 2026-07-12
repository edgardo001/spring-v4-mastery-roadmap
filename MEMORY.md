# Memoria del Proyecto — Spring v4 Mastery Roadmap

## Estado de Progreso
- **READMEs profundos:** 01 al 61 (100%).
- **Implementación de código (proyectos ejecutables):**
  - ✅ **01-fundamentos-java** — Java puro (sin build tool). Compila con `javac`, ejecuta con `java`. Los 9 tests self-checking pasan.
  - 🟡 Pendientes: 02 al 61.

## Reglas Operacionales Críticas

### ❗ MEMORIA — Usar SIEMPRE este archivo `MEMORY.md`
- **NUNCA usar la memoria interna del agente Claude** (`~/.claude/projects/.../memory/`). Al migrar entre LLMs o agentes, esa memoria se pierde y se rompe la continuidad.
- Todo hallazgo, decisión, error o convención va en este `MEMORY.md` de la raíz del roadmap. Fin.

### ❗ CÓDIGO — Comentarios exhaustivos para principiantes
- **Feedback del usuario (2026-07-10):** el código debe ser legible para alguien que no sabe nada del lenguaje ni de Spring. Ya está en AGENTS.md pero se estaba omitiendo.
- Cada archivo `.java` debe incluir:
  - Javadoc de clase con propósito + analogía del mundo real.
  - Comentario por método explicando qué hace y por qué.
  - Comentarios línea a línea sobre palabras clave (`static`, `final`, `record`, lambdas, method references, `Optional`, streams).
  - Nunca asumir que el lector conoce Java: explicar hasta lo obvio (qué es `import`, qué hace `System.exit`, etc.).
- Si el revisor debe googlear un término del código para entenderlo, faltó un comentario.

### ❗ ARTEFACTO — JAR/WAR obligatorio (nunca `.class` sueltos)
- Todo módulo debe producir un `.jar` o `.war` ejecutable en `target/` (Maven) o `build/libs/` (Gradle).
- Módulos sin build tool (como el 01) usan `jar --create --main-class=...` desde el JDK.

### ❗ GIT COMMITS — Descriptivos y limpios
- **NO agregar** firmas automáticas, trailers de co-autoría, ni ninguna referencia a herramientas/asistentes en los mensajes de commit.
- El mensaje describe únicamente EL CAMBIO técnico realizado. Nada más.
- Seguir Conventional Commits (definidos en AGENTS.md rol Git):
  - `feat(NN):` implementación de módulo NN
  - `fix(NN):` corrección
  - `docs(NN):` cambios de README/documentación
  - `chore:` config global (toolchain, .gitignore)
- **Un commit por unidad lógica atómica** (p.ej. "feat(02): Spring Boot Hello World con MockMvc").
- **Cargar módulo por módulo** (un commit por módulo terminado con tests verdes).

### Multi-agente
- Se pueden lanzar agentes en paralelo (como indica AGENTS.md) para acelerar tareas independientes (p.ej. escribir READMEs, generar scaffolding).
- Pero la regla "cada proyecto debe compilar y pasar tests antes de continuar" se mantiene: la validación es secuencial por proyecto.
- **Antes de escribir código de cada módulo**, lanzar en paralelo a los 10 agentes de AGENTS.md (Líder Técnico, Arquitecto, Profesor, Desarrollador, QA, Seguridad, DevOps, Testing, Documentación, Alumno). Consolidar consenso y RECIÉN implementar. Documentar las decisiones consolidadas en el README del módulo.

### ❗ PERFIL DEL DESARROLLADOR (contexto obligatorio)
- **El usuario aprendió Java 1.8 y usa Java 17 con sintaxis 1.8** (verificado 2026-07-10).
- No usa records, pattern matching, streams avanzados, var, switch expressions ni Optional habitualmente.
- **Implicación:** cada archivo `.java` debe incluir una sección o bloque de comentario "**ANTES (Java 8) vs AHORA (Java 21)**" que muestre el mismo código en ambas sintaxis, para que el lector reconozca el equivalente clásico.

### ❗ ESTRUCTURA HOMOGÉNEA DE TODO MÓDULO (checklist obligatoria)
Cada módulo (01..61) debe producir la MISMA experiencia de aprendizaje. Checklist mínima:

1. **Código Java** con:
   - Comentario de clase con propósito + analogía del mundo real.
   - Comentario por método explicando qué hace, para qué sirve, y las palabras clave (`static`, `final`, `record`, `var`, lambdas, method references, `Optional`).
   - Bloque **"ANTES (Java 8) vs AHORA (Java 21)"** con snippets comparativos para cada concepto moderno usado.
   - Preguntas ingenuas del Alumno respondidas inline como `// PREGUNTA DE ALUMNO — "…"`.
2. **README.md del módulo** con estructura AGENTS.md ampliada:
   - Propósito / Problema / Cómo lo resuelve / Por qué aprenderlo.
   - Diagrama Mermaid con colores.
   - Glosario Básico.
   - Conceptos con Qué es / Por qué importa / Código / Analogía / Casos de Uso Empresariales.
   - **Sección "Antes vs Ahora"** con tabla comparativa de sintaxis Java 8 → Java 21 aplicada al módulo.
   - **Sección "FAQ del Alumno"** con las preguntas ingenuas que un principiante haría (aportadas por el agente Alumno).
   - Ejercicios.
   - Cómo ejecutar (con `build.sh` / `build.ps1` / `mvn spring-boot:run` / `java -jar target/*.jar`).
   - Archivos del Proyecto (tabla).
3. **Artefacto ejecutable** `target/<artifactId>-1.0.0.jar` (o `.war`) — NO `.class` sueltos.
4. **Tests** con al menos `contextLoads` + 1 test por endpoint/servicio principal.
5. **Scripts `build.sh` + `build.ps1`** en la raíz del módulo, usando el toolchain portable.
6. **Coordenadas Maven consistentes:** `groupId=com.springroadmap`, `artifactId=<slug-sin-NN>`, `version=1.0.0`, `finalName=<artifactId>-1.0.0`, paquete `com.springroadmap.<slug>`.

## Entorno Local del Desarrollador
- **JDK 21 portable:** carpeta local `jdk-21.0.11+10/` en la raíz del roadmap (Temurin 21.0.11 LTS). El desarrollador mantiene Java 17 como JDK del sistema.
- **Ruta absoluta del `java`/`javac`:**
  - `C:\Users\datasoft\Desktop\edgardo001.github.com\00_spring-v4-mastery-roadmap\jdk-21.0.11+10\bin\java.exe`
  - `C:\Users\datasoft\Desktop\edgardo001.github.com\00_spring-v4-mastery-roadmap\jdk-21.0.11+10\bin\javac.exe`
- **`export JAVA_HOME=...` en Git Bash NO propaga a subprocesos** (verificado 2026-07-10). Preferir invocar `java`/`javac` con ruta absoluta, o usar PowerShell con `$env:JAVA_HOME`.
- **`.gitignore` creado en raíz** con `jdk-21.0.11+10/` excluida (patrón `jdk-*/`), + reglas Maven/Gradle/IDE/Node/Docker.

## Toolchain Portable en la Raíz del Roadmap

Los tres toolchains están en la raíz del proyecto y **excluidos del repo** por `.gitignore` (patrones `jdk-*/`, `apache-maven-*/`, `gradle-*/`). Se descargan manualmente en cada máquina de desarrollo.

### JDK 21 (Microsoft Build of OpenJDK)
- **Versión instalada:** `jdk-21.0.11+10` (Temurin/Microsoft, LTS).
- **URL de descarga oficial:** https://learn.microsoft.com/es-mx/java/openjdk/download
- **Descarga directa (zip Windows x64):** https://aka.ms/download-jdk/microsoft-jdk-21.0.11-windows-x64.zip
- **Instalación en la raíz del roadmap:**
  ```powershell
  # PowerShell, ubicado en la raíz del roadmap
  Invoke-WebRequest -Uri "https://aka.ms/download-jdk/microsoft-jdk-21.0.11-windows-x64.zip" -OutFile jdk.zip
  Expand-Archive -Path jdk.zip -DestinationPath .
  # Renombrar si el zip trae un nombre distinto (Microsoft usa "jdk-21.0.11+10")
  Remove-Item jdk.zip
  ```
  ```bash
  # Git Bash equivalente
  curl -sSL -o jdk.zip "https://aka.ms/download-jdk/microsoft-jdk-21.0.11-windows-x64.zip"
  unzip -q jdk.zip && rm jdk.zip
  ```

### Maven 3.9.16
- **URL de descarga:** https://maven.apache.org/download.cgi
- **Zip directo:** https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip
- **Instalación:**
  ```bash
  cd <raíz-roadmap>
  curl -sSL -o maven.zip "https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip"
  unzip -q maven.zip && rm maven.zip
  ```
- **Binario:** `apache-maven-3.9.16/bin/mvn.cmd` (Windows) o `mvn` (Unix).

### Gradle 9.6.1
- **URL de descarga:** https://services.gradle.org/distributions/gradle-9.6.1-bin.zip (135 MB)
- **API de versiones:** https://services.gradle.org/versions/current
- **Instalación:**
  ```bash
  cd <raíz-roadmap>
  curl -sSL -o gradle.zip "https://services.gradle.org/distributions/gradle-9.6.1-bin.zip"
  unzip -q gradle.zip && rm gradle.zip
  ```
- **Binario:** `gradle-9.6.1/bin/gradle.bat` (Windows) o `gradle` (Unix).

### JAVA_HOME correcto al invocar Maven/Gradle
Maven detecta por defecto el JDK del sistema (Java 17). Para forzar el JDK 21 portable en la sesión:
```bash
# Git Bash
export JAVA_HOME="$(pwd)/jdk-21.0.11+10"
export PATH="$JAVA_HOME/bin:$PATH"
```
```powershell
# PowerShell
$env:JAVA_HOME = "$PWD\jdk-21.0.11+10"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```
**Nota:** en Git Bash `export` no se propaga a subprocesos externos como `mvn.cmd`. Preferir PowerShell para builds Maven/Gradle, o setear `JAVA_HOME` en `.mvn/jvm.config` por proyecto.

## Convención de Empaquetado (OBLIGATORIA)
- **Todo módulo debe producir un artefacto ejecutable `.jar` (o `.war` si aplica) en `target/<nombre>-<version>.jar`.** No sirven las carpetas de `.class` sueltas.
- El comando "Cómo ejecutar" de cada README debe terminar en `java -jar target/<nombre>.jar` (o `mvn spring-boot:run` para desarrollo, pero el artefacto debe existir).
- **Módulo 01** (Java puro, sin Maven): scripts `build.sh` (Git Bash) y `build.ps1` (PowerShell) que:
  1. Compilan con `javac` a `out/`.
  2. Empaquetan con `jar --create --main-class=Main` a `target/fundamentos-java-1.0.0.jar`.
  3. Ejecutan con `java -jar` y validan que todos los tests self-checking pasen.
- **Módulos 02+** (Maven/Gradle): el artefacto sale automáticamente en `target/*.jar` (Maven) o `build/libs/*.jar` (Gradle) con el goal `package`/`bootJar`. Configurar `<finalName>` para nombre estable.

## Estructura de Proyectos por Módulo
- **Módulo 01** (Java puro): `src/{records,streams,optional}/*.java` + `src/Main.java` + `build.sh` + `build.ps1`. Sin `pom.xml`.
- **Regla para tests self-checking sin JUnit** (útil cuando no hay build tool): la clase `Main` orquesta helpers `assertEqual`/`assertTrue`/`assertThrows` y retorna exit code 0 si todo pasa, 1 si algún test falla.

## Errores y Soluciones

| Fecha | Proyecto | Error | Causa | Solución |
|-------|----------|-------|-------|----------|
| 2026-07-10 | 02-intro-spring | `package org.springframework.boot.test.autoconfigure.web.servlet does not exist` al compilar test con `@WebMvcTest` | **En Spring Boot 4.1.0 se ELIMINARON `@WebMvcTest` y `@AutoConfigureMockMvc`** (verificado: no están en NINGÚN JAR de Boot 4.1.0) | Patrón portable OBLIGATORIO: `MockMvcBuilders.standaloneSetup(new ControllerXxx(deps)).build()`. Sin anotaciones test-slice. |
| 2026-07-10 | 07-jpa-hibernate | `@DataJpaTest` no compila | **Eliminada en Boot 4.1.0** junto con las demás test-slices (`@JsonTest`, `@RestClientTest`, etc.) | Usar `@SpringBootTest` + `@Transactional` para rollback automático. |
| 2026-07-10 | 07-jpa-hibernate | `com.fasterxml.jackson.databind.ObjectMapper` no existe en test classpath | Jackson-databind no viaja transitivamente en algunos módulos de Boot 4.1.0 | Construir JSON literal como String en tests, o agregar dependencia explícita `com.fasterxml.jackson.core:jackson-databind`. |
| 2026-07-10 | 07-jpa-hibernate | `Pageable` en controller falla en MockMvc standalone con "No primary or single unique constructor" | En modo standalone no se registra el `PageableHandlerMethodArgumentResolver` | `MockMvcBuilders.standaloneSetup(controller).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build()` |
| 2026-07-10 | 08-migraciones-bd | Hibernate schema validation fail: "missing table [authors]" pese a `spring.flyway.enabled: true` | En **Spring Boot 4 la autoconfiguración de Flyway se movió a `spring-boot-starter-flyway`**. Usar solo `org.flywaydb:flyway-core` NO activa la auto-config | Usar `spring-boot-starter-flyway` en lugar de `flyway-core`. Aplica a TODOS los módulos con migraciones. |
| 2026-07-10 | 13/14/15 | `TestRestTemplate` no compila: `package org.springframework.boot.test.web.client does not exist` | **`TestRestTemplate` fue eliminado en Spring Boot 4.1.0** | Usar `RestClient.builder().baseUrl("http://localhost:" + port).build()` (parte de Spring Framework 7). |
| 2026-07-10 | 13/14/15 | `@LocalServerPort` en paquete incorrecto | En Boot 4.1.0 vive en `org.springframework.boot.test.web.server.LocalServerPort` (NO en `.web.server.test`) | Usar el import correcto. |
| 2026-07-10 | 20-spring-aop | `spring-boot-starter-aop` no existe versión 4.x | **Eliminado en Spring Boot 4**. Última versión es 3.5.x | Usar `org.springframework:spring-aspects` + `org.aspectj:aspectjweaver` directamente. |
| 2026-07-10 | 19-rest-client | `NoSuchBeanDefinitionException: No qualifying bean of type 'RestClient$Builder'` | **En Boot 4.1.0 no se autoconfigura `RestClient.Builder`** como bean | Construir con `RestClient.builder()` estáticamente en el `@Bean`, sin recibir el Builder por parámetro. |
| 2026-07-10 | 18-jpa-avanzado | `MockMvc` retorna 500 al serializar `new PageImpl<>(List.of(...))` sin metadata | Spring Boot 4 exige `Pageable` + `total` explícitos al serializar `PageImpl` | Usar `new PageImpl<>(content, pageable, totalElements)` con los 3 argumentos, no solo el content. |
| 2026-07-10 | 09-mapstruct | MapStruct falla en compile con "Unmapped target properties: id, internalNotes" | MapStruct 1.6+ exige explicit `@Mapping(ignore=true)` para propiedades no mapeadas | Agregar `@Mapping(target = "id", ignore = true)` y `@Mapping(target = "internalNotes", ignore = true)` en el método `toEntity`. |
| 2026-07-10 | 24-rest-avanzado | `package org.springframework.data.domain does not exist` sin JPA | En módulos sin JPA pero que usan `Pageable`/`Page`, falta `spring-data-commons` | Agregar dependencia directa `org.springframework.data:spring-data-commons` (viene con la version del BOM de Boot). |
| 2026-07-10 | 25-testing-avanzado | `@Testcontainers` arranca el `@Container` ANTES de `@BeforeAll`, así `Assumptions.assumeTrue(dockerAvailable)` no puede saltarlo | Ciclo de vida de la extension Testcontainers no respeta assumptions posteriores | Marcar la clase con `@Disabled("Requiere Docker Desktop")` y documentar cómo activar. El `contextLoads` sigue verde con H2 fallback. |
| 2026-07-10 | 26-docker | Maven falla parsear `pom.xml`: "in comment after two dashes (--) next character must be > not f" | XML no permite `--` dentro de comentarios. `COPY --from=builder` como texto en el comentario del pom rompe el parser | Evitar `--` en comentarios del pom.xml. Reformular con "COPY from=builder" o dividir en dos líneas. |
| 2026-07-10 | 29-spring-cloud-config | Spring Cloud Config Server 2024.x/2025.x no publicado para Boot 4.1.0 | Spring Cloud sigue basado en Boot 3.x | Módulo demo simplificado con `@ConfigurationProperties` + Actuator `/refresh`. Cloud Config Server real esperará release compatible. |
| 2026-07-10 | 30-resilience4j | `resilience4j-spring-boot3` está para Boot 3.x, no probado en Boot 4 | Autoconfig no homologada | Usar API programática: `resilience4j-circuitbreaker` + `resilience4j-retry` con `@Bean` que crea instancias y `Retry.decorateSupplier(retry, CircuitBreaker.decorateSupplier(cb, supplier))`. |
| 2026-07-11 | 32-graphql | `HttpGraphQlTester` requiere WebFlux/WebClient (`org.springframework.web.reactive.function.client.WebClient` no está en spring-web sync) | El slice `@GraphQlTest` tampoco existe en Boot 4.1.0 | Usar `RestClient` + `@LocalServerPort`, hacer POST manual a `/graphql` con `{"query":"..."}` como body. Portable y no requiere WebFlux. |
| 2026-07-11 | 34-oauth2 | `NoSuchBeanDefinitionException: HttpSecurity` al arrancar contexto | En Boot 4 hay que anotar la clase `SecurityConfig` con `@EnableWebSecurity` explícitamente (antes venía por defecto con `@Configuration` en algunos setups) | Agregar `@EnableWebSecurity` a la clase de config Security. |
| 2026-07-11 | 38-hexagonal | `NoEnumConstant tools.jackson.databind.SerializationFeature.write-dates-as-timestamps` | En Boot 4 el binder de propiedades de Jackson exige nombres en `UPPER_SNAKE_CASE` (nombres exactos del enum), no kebab-case | Escribir `WRITE_DATES_AS_TIMESTAMPS` (o directamente NO configurar Jackson en `application.yml` — usa defaults sensatos). |
| 2026-07-11 | 39-monolito-modular | `mvn: command not found` en build.sh | Agente generó script con `mvn` global (no existe) en vez de path portable | Usar el patrón portable `../apache-maven-3.9.16/bin/mvn` y `../jdk-21.0.11+10`. |
| 2026-07-12 | 44-spring-angular, 38-hexagonal | Contexto no arranca por `Could not bind properties to 'JacksonProperties'` | En Spring Boot 4, cualquier `spring.jackson.serialization.*` o `spring.jackson.deserialization.*` en `application.yml` requiere UPPER_SNAKE_CASE exacto del enum de Jackson y aun así rompe `JacksonAutoConfiguration` | Eliminar completamente el bloque `spring.jackson:` del yml. Los defaults de Spring Boot (ISO-8601 para fechas) son correctos. |
| 2026-07-12 | 50-spring-batch | Imports `org.springframework.batch.core.Job`, `Step`, `JobParameters`, `JobExecution` no encontrados | **Spring Batch 6.0 (viene con Boot 4.1.0) reorganizó paquetes**: `Job` → `core.job.Job`, `Step` → `core.step.Step`, `JobParameters`/`JobParametersBuilder` → `core.job.parameters.*`, `JobExecution` → `core.job.JobExecution`. Además `ItemReader`/`ItemWriter` → `infrastructure.item.*` | Actualizar todos los imports a los nuevos paquetes. Además `JpaItemWriter` ahora exige `EntityManagerFactory` por CONSTRUCTOR (no setter). |
| 2025-01 | 07-jpa-hibernate | LazyInitializationException | Acceso a colección lazy fuera de transacción | Cambiar a `@EntityGraph` |
| 2025-01 | 35-actuator | Pérdida de TraceID | Hilos asíncronos nativos rompen el `ThreadLocal` de Micrometer | Usar abstracciones de Spring o ContextSnapshot en lugar de `new Thread()` |
| 2025-01 | 39-modulith | Aislamiento roto | Módulo A importaba clase `internal` de Módulo B | Proveer API (`Facade`) en paquete raíz del Módulo B y forzar validación con `ApplicationModules.of(Main.class).verify()` |
| 2025-01 | 42-ddd | Modelos anémicos en JPA | Setter injection burla las reglas del negocio | Ocultar setters, usar constructor `protected` para JPA y métodos de negocio explícitos (`approve()`). NUNCA usar Entidades en el `@RequestBody`. |
| 2025-01 | 43/44-frontend | CORS en Desarrollo Local | Navegador bloquea peticiones de puerto 3000/4200 al 8080 | Usar el Proxy reverso nativo del CLI de Frontend (Vite proxy o Angular `proxy.conf.json`) para anular el CORS en desarrollo. |

## Decisiones de Arquitectura

| Fecha | Decisión | Razón |
|-------|----------|-------|
| 2025-01 | Constructor injection en todos los ejemplos | Mejor testabilidad, inmutabilidad, y buenas prácticas |
| 2025-01 | Reemplazar RestTemplate por RestClient | Spring deprecó orgánicamente RestTemplate. RestClient (Fluent API) es el estándar desde Spring Boot 3.2 |
| 2025-01 | Despliegue Frontend | Módulos 43 y 44 recomiendan despliegue separado (API First) y manejo de proxy de desarrollo, en vez de obligar al uso monolítico con Maven plugin. |
| 2025-01 | DDD Táctico Pragmático | En Módulo 42 se decidió priorizar la mezcla de `@Entity` con el Root Aggregate escondiendo los constructores, evitando el overhead purista de doble mapeo para proyectos medianos. |

## Convenciones

| Fecha | Convención | Detalle |
|-------|------------|---------|
| 2025-01 | Comentarios desde cero | Todo .java debe tener comentarios explicativos |
| 2025-01 | Analogías del mundo real | Cada concepto debe tener una analogía |
| 2025-01 | Casos de Error (Edge Cases) | Documentar fallos comunes y su solución arquitectónica |

## Dependencias

| Dependencia | Versión | Notas |
|-------------|---------|-------|
| spring-boot-starter-parent | 4.1.0 | BOM del proyecto |
| spring-boot-starter-web | 4.1.0 | APIs REST |
| spring-cloud-dependencies | 2024.x | Control de versiones Cloud |
| spring-modulith-bom | 1.x | BOM para el monolito modular |
| togglz-spring-boot-starter | 3.3.3 | Framework de Feature Flags |
