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
| 2026-07-10 | 02-intro-spring | `package org.springframework.boot.test.autoconfigure.web.servlet does not exist` al compilar test con `@WebMvcTest` | **En Spring Boot 4.1.0 se ELIMINÓ `@WebMvcTest`** (verificado: no está en `spring-boot-test-autoconfigure-4.1.0.jar` ni en ningún otro JAR de Boot 4.1.0) | Usar el patrón portable: `@SpringBootTest(webEnvironment = WebEnvironment.MOCK)` + `@AutoConfigureMockMvc` en la clase de test, y `MockMvc` autowired. Aplica a TODOS los módulos con tests de controllers en el roadmap. |
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
