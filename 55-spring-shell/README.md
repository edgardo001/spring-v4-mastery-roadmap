# 55 — Spring Shell

CLI interactiva con Spring Boot 4.1.0 + Java 21.

## Variante implementada: **REPL con `CommandLineRunner` + `Scanner`**

### Por qué esta variante (limitación conocida)
`spring-shell-starter` 3.4.x depende de Spring Framework 6.2. Spring Boot 4.1.0
trae Spring Framework 7.x. A la fecha de este módulo **no existe una release
homologada de Spring Shell para Boot 4**, así que agregar
`org.springframework.shell:spring-shell-starter:3.4.0` rompe el classpath.

Para respetar la promesa de "build reproducible" del roadmap, este módulo
implementa una **versión simplificada** del ciclo de Spring Shell:

- `ShellRunner implements CommandLineRunner` — lee stdin línea a línea.
- `MathCommands` como `@Component` — expone `add` y `mul`.
- `PromptProvider` como `@Component` — devuelve el string `"roadmap> "`.

Cuando VMware publique `spring-shell-starter` compatible con Boot 4:
1. Reemplazar `spring-boot-starter` por `spring-shell-starter`.
2. Anotar `MathCommands` con `@ShellComponent` y cada método con
   `@ShellMethod(key = "add")`.
3. Convertir `PromptProvider` en un `@Bean` que devuelva `AttributedString`.
4. Borrar `ShellRunner` (Spring Shell trae su propio REPL con JLine).

---

## Antes vs Ahora

### Antes: script bash externo
```bash
#!/usr/bin/env bash
# calc.sh
case "$1" in
    add) echo $(( $2 + $3 )) ;;
    mul) echo $(( $2 * $3 )) ;;
    *) echo "uso: calc.sh add|mul a b"; exit 1 ;;
esac
```
Problemas:
- No accede al `ApplicationContext` (ni servicios, ni transacciones, ni JPA).
- Sin autocompletado, sin historial, sin ayuda estructurada.
- Multiplataforma es un dolor (bash en Windows requiere Git Bash / WSL).
- Cada script vive suelto; nadie los mantiene.

### Ahora: CLI Java con Spring
```
$ java -jar target/spring-shell-1.0.0.jar
Spring Shell (variante REPL) - escribe 'help' o 'exit'
roadmap> add 2 3
5
roadmap> mul 4 5
20
roadmap> help
Comandos: add <a> <b> | mul <a> <b> | help | exit
roadmap> exit
bye
```
Beneficios:
- Los "comandos" son beans: pueden inyectar `Repositories`, `Services`, etc.
- Un solo jar multiplataforma (Windows/Linux/macOS).
- Testeable con JUnit (ver `MathCommandsTest`, `ShellRunnerTest`).
- En la variante completa de Spring Shell: autocompletado con TAB, colores ANSI,
  historial persistente, `TableBuilder`, `ComponentFlow` para wizards.

---

## Estructura
```
55-spring-shell/
├── pom.xml
├── build.sh / build.ps1
├── src/main/java/com/springroadmap/shell/
│   ├── SpringShellApplication.java
│   ├── command/MathCommands.java
│   └── runner/
│       ├── PromptProvider.java
│       └── ShellRunner.java
├── src/main/resources/application.yml
└── src/test/java/com/springroadmap/shell/
    ├── SpringShellApplicationTests.java  (contextLoads)
    ├── command/MathCommandsTest.java
    └── runner/ShellRunnerTest.java
```

## Build
```powershell
# Windows
./build.ps1
```
```bash
# Linux / macOS / Git Bash
./build.sh
```
Ambos scripts usan el JDK y Maven **portables** que viven en la raíz del
roadmap (`../jdk-21.0.11+10`, `../apache-maven-3.9.16`), así que no dependes
de un `JAVA_HOME` global.

## Ejecución
```bash
java -jar target/spring-shell-1.0.0.jar
```
Para saltarse el REPL (útil en CI):
```bash
java -jar target/spring-shell-1.0.0.jar --no-shell
```

---

## FAQ

**¿Por qué no `spring-shell-starter`?**
No hay release compatible con Spring Boot 4.1.0 / Framework 7. Este módulo
documenta la limitación y muestra el equivalente conceptual.

**¿Se pierden `@ShellMethod`, `@ShellOption`, `Availability`, `PromptProvider`,
`ComponentFlow`?**
Sí, esos son features de la librería real. Los conceptos siguen aplicando y
el README teórico del módulo (arriba de este archivo, en versiones históricas)
los explica igual. La migración a la variante oficial es cambiar 4-5 líneas.

**¿Por qué no usar Picocli / JCommander?**
Son excelentes para CLIs *one-shot* (parseo de args + salida + `System.exit`).
Spring Shell (y su fallback aquí) apunta a **REPL interactivo** con contexto
de Spring vivo entre comandos.

**¿Por qué `spring.main.web-application-type=none`?**
No queremos Tomcat: esta app es una consola, no un servidor HTTP. Boot arranca
el contexto sin buscar puerto.

**¿Cómo se testea un REPL sin bloquear stdin?**
`ShellRunner.loop(BufferedReader, PrintStream)` acepta streams inyectables,
así el test alimenta comandos desde un `StringReader` y captura salida en un
`ByteArrayOutputStream`. Ver `ShellRunnerTest`.

**¿Y `contextLoads` no cuelga esperando stdin?**
`@SpringBootTest(args = "--no-shell")` le dice al `ShellRunner` que no entre
al bucle.

**¿Puedo añadir comandos con dependencias JPA?**
Sí: cualquier `@Component` puede inyectar `Repository`s por constructor. Es
justamente la ventaja frente a un script bash.
