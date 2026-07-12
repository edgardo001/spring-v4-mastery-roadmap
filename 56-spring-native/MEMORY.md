# MEMORY - 56-spring-native

## Contexto
Modulo del roadmap Spring v4 dedicado a **GraalVM Native Image** y **Spring Boot AOT**.

## Decisiones
- **Spring Boot 4.1.0**, Java 21, `groupId=com.springroadmap`, `artifactId=spring-native`, package `com.springroadmap.nativeaot` (evita colision con palabra reservada `native`).
- `finalName=spring-native-1.0.0`; artefacto: `target/spring-native-1.0.0.jar`.
- Sin `spring.jackson:` en `application.yml`.
- MockMvc **standalone** (sin `@SpringBootTest` en tests de controller). Sin Lombok. Constructor injection.
- Perfil Maven `native` con `native-maven-plugin` de GraalVM (`imageName=hello-native`, `--no-fallback`).
- `AppRuntimeHints implements RuntimeHintsRegistrar` registra `Message.class` con `MemberCategory.INVOKE_PUBLIC_METHODS`.
- `@ImportRuntimeHints(AppRuntimeHints.class)` en la clase `@SpringBootApplication`.
- Scripts `build.sh` / `build.ps1` ejecutan solo `mvn clean verify` (JAR normal, NO `-Pnative`).

## Alcance
- App minima: `HelloController` -> `GET /api/hello` retorna literal `"Hello from AOT/Native"`.
- `Message` record (id: Long, content: String) para demostrar hints de reflection.
- Tests: `contextLoads` (SpringBootTest) + `HelloControllerTest` (MockMvc standalone).

## Fuera de alcance
- Ejecucion real del build nativo (requiere GraalVM `native-image` instalado).
- Buildpacks (documentado en README pero no ejecutado en CI).
- Tracing agent y generacion automatica de hints.

## Antes vs Ahora
- **Antes:** Spring Boot sobre JVM tradicional -> arranque 5-10s, RAM 300MB+, cold start inaceptable en Lambda.
- **Ahora:** binario nativo GraalVM -> arranque <100ms, RAM <80MB, cold start viable en serverless.
