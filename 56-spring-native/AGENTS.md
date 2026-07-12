# AGENTS - 56-spring-native

## Convenciones obligatorias
- Spring Boot **4.1.0**, Java **21**, Maven portable (`apache-maven-3.9.16` en la raiz del roadmap).
- `groupId=com.springroadmap`, `artifactId=spring-native`, `finalName=spring-native-1.0.0`.
- Package raiz: `com.springroadmap.nativeaot` (NO usar `native` como identificador, es palabra reservada Java).
- **PROHIBIDO** usar `spring.jackson:` en `application.yml`.
- MockMvc **standalone** en tests de controllers (`MockMvcBuilders.standaloneSetup(...)`).
- Sin **Lombok**. Sin `@Autowired` en campos: usar **constructor injection**.
- README debe incluir seccion **"Antes vs Ahora"** y **FAQ**.

## Estructura
```
56-spring-native/
  pom.xml                 # Spring Boot parent 4.1.0 + perfil `native`
  build.sh / build.ps1    # `mvn clean verify` con toolchain portable
  README.md
  MEMORY.md
  AGENTS.md
  src/main/java/com/springroadmap/nativeaot/
      SpringNativeApplication.java   # @SpringBootApplication + @ImportRuntimeHints
      controller/HelloController.java
      domain/Message.java             # record
      config/AppRuntimeHints.java     # RuntimeHintsRegistrar
  src/main/resources/application.yml
  src/test/java/com/springroadmap/nativeaot/
      SpringNativeApplicationTests.java   # contextLoads
      controller/HelloControllerTest.java # MockMvc standalone
```

## Build
- Normal (JAR): `./build.sh` o `./build.ps1` -> `target/spring-native-1.0.0.jar`.
- Nativo (opcional, requiere GraalVM 21+): `mvn -Pnative native:compile` -> `target/hello-native`.
- Buildpacks (sin GraalVM local, requiere Docker): `mvn -Pnative spring-boot:build-image`.

## Restricciones
- El build nativo **no** se ejecuta en CI ni en los scripts por defecto.
- Los scripts solo garantizan la construccion del JAR y la ejecucion de tests JVM.
