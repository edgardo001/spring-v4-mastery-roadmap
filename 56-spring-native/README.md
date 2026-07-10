## 56 — Spring Boot Native (GraalVM)

### Propósito
Aprender a compilar una aplicación Spring Boot 4.1.0 a un **ejecutable nativo** usando **GraalVM `native-image`** y el motor **Spring AOT**, obteniendo binarios pequeños con arranque en milisegundos y bajo consumo de memoria — ideales para serverless y contenedores.

### Problema que resuelve
Una aplicación Spring Boot corriendo sobre la JVM tradicional tiene tres problemas críticos para entornos modernos (AWS Lambda, Google Cloud Run, Kubernetes con `scale-to-zero`):

- **Arranque lento** → 5 a 10 segundos solo para levantar el contexto de Spring, escanear beans, cargar Hibernate y calentar el JIT.
- **Consumo alto de RAM** → 300 MB o más por instancia, incluso para un microservicio trivial. Multiplícalo por 50 pods en Kubernetes y la factura AWS duele.
- **Costos elevados en serverless** → Cada invocación de Lambda paga el "cold start" completo de la JVM. Un endpoint que responde en 10ms de lógica termina tardando 8s la primera vez.

Sin Spring Native, la única alternativa era migrar todo a Go, Node o Quarkus, perdiendo el ecosistema Spring.

### Cómo lo resuelve
Spring Boot 4 integra **GraalVM native-image** con el **Spring AOT engine** para transformar el bytecode + configuración de Spring en un binario ejecutable nativo:

1. **Spring AOT** analiza el `ApplicationContext` **en tiempo de compilación** (no en runtime): resuelve qué beans existen, qué reflection se necesita, qué proxies, qué recursos.
2. Genera automáticamente **RuntimeHints** (metadata JSON) que le dice a GraalVM: "estas clases se usan por reflection, incluye sus métodos".
3. **GraalVM native-image** compila todo (tu código + Spring + JDK + hints) usando la **closed-world assumption** (todo el código conocido en build time) y produce un **binario nativo** independiente.
4. Resultado: binario `<50MB`, arranque `<100ms`, consumo `<80MB RAM`.

### Por qué aprenderlo
Es la piedra angular del **Spring en la nube moderna**:

- **AWS Lambda / Azure Functions**: cold start de 100ms en vez de 8s. Tu API SLA se cumple sin provisioned concurrency.
- **Google Cloud Run / Knative**: `scale-to-zero` viable — tu servicio arranca cuando llega la request, sin latencia perceptible.
- **Kubernetes de bajo costo**: 200 pods de 80MB caben donde antes cabían 50 pods de 300MB. Reducción directa de nodos EC2.
- **Fast Startup CLI tools**: escribir herramientas de línea de comandos con Spring Shell que arrancan como si fueran binarios en Go.

```mermaid
graph LR
    A["Código Java<br/>@Service, @Controller"] --> B["Spring AOT Engine<br/>(analiza contexto)"]
    B --> C["RuntimeHints<br/>reflect-config.json"]
    C --> D["GraalVM native-image<br/>(closed-world compile)"]
    D --> E["Binario Nativo<br/><50MB, arranque <100ms"]

    style A fill:#339af0,color:#fff
    style B fill:#51cf66,color:#fff
    style D fill:#f76707,color:#fff
    style E fill:#f03e3e,color:#fff
```

---

### Glosario Básico

#### `GraalVM`
Distribución OpenJDK de Oracle que incluye el compilador `native-image`. Requiere GraalVM 21+ para Spring Boot 4.

#### `AOT` (Ahead-Of-Time)
Compilación **antes de ejecutar**. Opuesto a JIT. Spring AOT genera código Java optimizado que sustituye la introspección de runtime.

#### `JIT` (Just-In-Time)
Compilación en caliente que hace la JVM tradicional. Rápido en régimen, lento al arrancar.

#### `native-image`
Herramienta de GraalVM que empaqueta bytecode + JDK + dependencias en un único binario ELF/PE/Mach-O ejecutable.

#### `RuntimeHints`
API de Spring 6+/4 para declarar programáticamente qué clases, recursos y proxies necesitan reflection en el binario nativo.

#### `@ImportRuntimeHints`
Anotación que registra un `RuntimeHintsRegistrar` en el contexto para que Spring AOT lo procese.

#### `closed-world assumption`
Regla de GraalVM: **todo** el código que existirá en runtime debe ser conocido en build time. No hay `Class.forName()` dinámico.

#### `Buildpack`
Imagen Docker de Paketo/Cloud Native Buildpacks que compila el nativo sin instalar GraalVM local. Usado por `mvn spring-boot:build-image`.

#### `tracing agent`
Agente JVM (`-agentlib:native-image-agent`) que graba en JSON toda la reflection usada durante una ejecución normal, para generar hints automáticos.

---

### Conceptos

#### 1. Configuración con `spring-boot-starter-parent` y goal `native:compile`
- **Qué es** — El `pom.xml` heredando de `spring-boot-starter-parent:4.1.0` ya trae el plugin `native-maven-plugin` de GraalVM configurado. Solo activas el perfil `native`.
- **Código**:
  ```xml
  <parent>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-parent</artifactId>
      <version>4.1.0</version>
  </parent>

  <profiles>
      <profile>
          <id>native</id>
          <build>
              <plugins>
                  <plugin>
                      <groupId>org.graalvm.buildtools</groupId>
                      <artifactId>native-maven-plugin</artifactId>
                  </plugin>
              </plugins>
          </build>
      </profile>
  </profiles>
  ```
- **Ejecución**: `mvn -Pnative native:compile` produce `target/app` (binario).

#### 2. Buildpacks vs native-image manual
- **Qué es** — Dos formas de obtener el binario:
  - **Buildpacks** (`mvn spring-boot:build-image -Pnative`) → NO necesitas GraalVM instalado; usa un contenedor Paketo. Ideal para CI/CD reproducible. Resultado: imagen Docker `docker.io/library/app:0.0.1-SNAPSHOT`.
  - **native-image manual** (`mvn -Pnative native:compile`) → Requiere GraalVM 21+ instalado localmente. Más rápido para iterar en desarrollo.
- **Cuándo cada uno**: buildpacks para producción/CI, manual para depurar.

#### 3. Reflection Hints (`RuntimeHintsRegistrar` + `@ImportRuntimeHints`)
- **Qué es** — Cualquier código de terceros que use `Class.forName()`, `Method.invoke()` o Jackson sobre un DTO desconocido debe **declarar sus hints**. Si no, en runtime nativo obtienes `ClassNotFoundException`.
- **Código**:
  ```java
  public class AppRuntimeHints implements RuntimeHintsRegistrar {
      @Override
      public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
          // Declara que Product se accederá por reflection (Jackson serializer)
          hints.reflection().registerType(Product.class,
              MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
              MemberCategory.INVOKE_DECLARED_METHODS);
          // Declara un recurso empaquetado en el JAR
          hints.resources().registerPattern("data/*.json");
      }
  }

  @Configuration
  @ImportRuntimeHints(AppRuntimeHints.class)
  public class NativeConfig { }
  ```

#### 4. Testing nativo con `@GraalvmNative`
- **Qué es** — El plugin `native-maven-plugin` puede correr JUnit tests **contra el binario nativo ya compilado**. Solo se ejecutan los tests marcados.
- **Código**:
  ```java
  @SpringBootTest
  class NativeSmokeTest {
      @Test
      void contextLoadsInNativeImage() {
          // Este test se ejecuta con: mvn -PnativeTest test
      }
  }
  ```
- **Comando**: `mvn -PnativeTest test` compila el nativo y ejecuta los tests contra el binario.

#### 5. Trade-offs
- **Build time**: 5 a 15 minutos por compilación nativa (vs 20s en JVM). El pipeline CI/CD debe adaptarse.
- **Memoria peak durante build**: 4 a 8 GB de RAM. Los runners de GitHub Actions gratuitos (7GB) apenas alcanzan.
- **Sin dynamic class loading**: nada de `Class.forName("com.dinamica.Plugin")` en runtime. Los plugins deben conocerse en compile time.
- **Debug limitado**: no puedes conectar un IDE al binario nativo como a la JVM.

---

### Edge Cases

| Caso | Síntoma | Solución |
|------|---------|----------|
| Librería incompatible (algún driver JDBC exótico, MyBatis viejo) | `UnsupportedFeatureError` durante `native:compile` | Verificar en [Spring Native Reachability Metadata Repo](https://github.com/oracle/graalvm-reachability-metadata). Si no está, buscar alternativa (p.ej. R2DBC, Postgres JDBC 42+). |
| Reflection en librería de terceros sin hints | Runtime: `ClassNotFoundException` o `NoSuchMethodException` en el binario | Ejecutar con **tracing agent**: `java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar app.jar` haciendo ejercicio funcional. Se generan hints automáticos. |
| Lombok en build time | Sin problema | Lombok procesa anotaciones en tiempo de compilación Java (antes del AOT). El bytecode resultante ya no depende de Lombok en runtime. |
| Warm-up JIT vs performance nativa | El binario nativo puede ser 10-20% más lento en régimen sostenido que la JVM caliente | Aceptable: el nativo gana en **arranque** y **memoria**. Si necesitas throughput máximo sostenido (batch pesado), mantén JVM tradicional. |
| Perfiles Spring | `application-native.yml` no se lee | El perfil `native` se activa automáticamente; usa `@Profile("native")` en beans específicos. |

---

### Ejercicios
1. Crea un proyecto Spring Boot 4.1.0 con Spring Web y una ruta `/hello` que devuelva `"Hola Native"`.
2. Compila con JVM (`mvn spring-boot:run`) y mide arranque + memoria con `jps -v`.
3. Instala GraalVM 21 con SDKMAN (`sdk install java 21.0.2-graal`) y compila nativo con `mvn -Pnative native:compile`.
4. Ejecuta el binario `./target/app` y mide arranque con `time` y memoria con `ps aux | grep app`. Compara.
5. Agrega un DTO `Product` con Jackson, un endpoint POST, y usa el tracing agent para generar hints automáticos.

### Cómo ejecutar
**Requisitos**: GraalVM 21+ instalado (`sdk install java 21.0.2-graal`) y `native-image` disponible (`gu install native-image`).

```bash
cd 56-spring-native

# Opción A — Compilación nativa local (requiere GraalVM)
mvn -Pnative native:compile
./target/app
# Arranque esperado: <100ms

# Opción B — Vía Buildpacks (sin GraalVM local, requiere Docker)
mvn spring-boot:build-image -Pnative
docker run --rm -p 8080:8080 docker.io/library/spring-native:0.0.1-SNAPSHOT

# Ejecución JVM normal (para comparar)
mvn spring-boot:run
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Perfil `native` con `native-maven-plugin` de GraalVM y `spring-boot-starter-parent:4.1.0`. |
| `SpringNativeApplication.java` | Clase `@SpringBootApplication` principal. |
| `controller/HelloController.java` | Endpoint REST simple para validar arranque nativo. |
| `domain/Product.java` | DTO usado para demostrar hints de reflection con Jackson. |
| `config/NativeConfig.java` | `@Configuration` con `@ImportRuntimeHints(AppRuntimeHints.class)`. |
| `config/AppRuntimeHints.java` | `RuntimeHintsRegistrar` que declara reflection y recursos para GraalVM. |
| `application.yml` | Configuración base + perfil `native`. |
| `src/test/java/.../NativeSmokeTest.java` | Test ejecutado contra el binario nativo (`mvn -PnativeTest test`). |
