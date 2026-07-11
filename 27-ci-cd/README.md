# 27 - CI/CD con GitHub Actions (implementación)

Módulo práctico del roadmap Spring v4. Contiene una app Spring Boot mínima y un
workflow real de GitHub Actions que la compila, testea y publica el JAR como
artefacto descargable en cada push.

## Objetivo del módulo
Aprender a escribir un pipeline de **Integración Continua** en YAML: el mismo
YAML que usan las empresas hoy para proteger la rama `main` y automatizar el
empaquetado del artefacto.

## Estructura
```
27-ci-cd/
  .github/workflows/ci.yml         Workflow CI (scoped al módulo)
  src/main/java/com/springroadmap/cicd/
    CiCdApplication.java           Boot de Spring
    web/PingController.java        GET /api/ping -> "pong"
  src/test/java/com/springroadmap/cicd/
    CiCdApplicationTests.java      contextLoads
    web/PingControllerTest.java    MockMvc standalone
  pom.xml                          Boot 4.1.0 / Java 21 / finalName ci-cd-1.0.0
  build.sh / build.ps1             Build local
```

## Cómo compilar en local
```bash
# Linux / macOS / Git Bash
./build.sh

# Windows PowerShell
.\build.ps1

# o directo
mvn -B clean verify
```

Artefacto: `target/ci-cd-1.0.0.jar`.

## Cómo probar el endpoint
```bash
java -jar target/ci-cd-1.0.0.jar
# En otra terminal:
curl http://localhost:8027/api/ping
# -> pong
```

## Cómo activar el workflow en GitHub
1. Haz commit de este módulo y push al repo del roadmap:
   ```bash
   git add 27-ci-cd/
   git commit -m "feat(27): add ci/cd module"
   git push
   ```
2. Ve a la pestaña **Actions** del repo en GitHub.
3. Verás el workflow **"CI Module 27"** ejecutándose.
4. Al terminar en verde, en la sección **Artifacts** del run podrás descargar
   `ci-cd-jar` (contiene `ci-cd-1.0.0.jar`).
5. (Opcional) En **Settings -> Branches -> Branch protection rules**,
   marca la rama `main` y exige el check `build` de `CI Module 27` como
   requerido antes de hacer merge.

> El workflow está *scoped* con `paths: ['27-ci-cd/**']`. Solo se dispara si
> tocas archivos del módulo 27, no cuando editas otros módulos del roadmap.

---

## Antes vs Ahora

### Antes: Jenkins con XML jobs
```xml
<!-- config.xml del job en /var/lib/jenkins/jobs/mi-api/ -->
<project>
  <scm class="hudson.plugins.git.GitSCM">
    <userRemoteConfigs>
      <hudson.plugins.git.UserRemoteConfig>
        <url>git@github.com:empresa/mi-api.git</url>
      </hudson.plugins.git.UserRemoteConfig>
    </userRemoteConfigs>
  </scm>
  <builders>
    <hudson.tasks.Maven>
      <targets>clean verify</targets>
      <mavenName>Maven-3.9</mavenName>
    </hudson.tasks.Maven>
  </builders>
  <publishers>
    <hudson.tasks.ArtifactArchiver>
      <artifacts>target/*.jar</artifacts>
    </hudson.tasks.ArtifactArchiver>
  </publishers>
</project>
```
Dolor real:
- Un servidor Jenkins dedicado que hay que **parchar, respaldar y escalar**.
- Config vive en la UI de Jenkins (fuera del repo). Si el disco muere, adiós.
- Plugins que se rompen entre versiones. XML editado a mano.
- Un solo Jenkins para toda la empresa se vuelve cuello de botella.

### Ahora: GitHub Actions con YAML en el repo
```yaml
name: CI Module 27
on:
  push: { paths: ['27-ci-cd/**'] }
jobs:
  build:
    runs-on: ubuntu-latest
    defaults: { run: { working-directory: 27-ci-cd } }
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: 'temurin', java-version: '21', cache: 'maven' }
      - run: mvn -B clean verify
      - uses: actions/upload-artifact@v4
        with:
          name: ci-cd-jar
          path: 27-ci-cd/target/ci-cd-1.0.0.jar
```
Ganancias:
- **Cero servidores** que administrar. Runners los presta GitHub.
- La config **vive en el repo**: si borras el proyecto y lo clonas, el CI vuelve solo.
- **Versionable**: `git blame` sobre `ci.yml` te dice quién rompió el pipeline.
- **Paralelismo gratis**: cada PR levanta su propio runner.

---

## FAQ Alumno

**P: ¿Por qué `paths: ['27-ci-cd/**']` en el trigger?**
R: El roadmap tiene 40+ módulos. Sin este filtro, cambiar el README del módulo 5
dispararía el CI del módulo 27 (y de todos los demás). Con el filtro, cada módulo
solo se testea cuando realmente cambia.

**P: ¿Por qué `defaults.run.working-directory: 27-ci-cd`?**
R: Por defecto los comandos `run:` se ejecutan en la raíz del repo. Nuestro `pom.xml`
vive en `27-ci-cd/pom.xml`. Sin `working-directory`, `mvn` fallaría con
"no pom in this directory".

**P: Pero el `path:` del `upload-artifact` sí lleva `27-ci-cd/target/...`. ¿Por qué?**
R: Truco fino: `working-directory` solo aplica a los `run:` (comandos shell).
Las *actions* (`uses:`) resuelven paths desde la **raíz del repo**. Por eso
`checkout` y `setup-java` no necesitan prefijo, pero `upload-artifact` sí.

**P: ¿Qué diferencia hay entre `mvn test` y `mvn verify`?**
R: `test` corre pruebas unitarias. `verify` corre unitarias + integración +
plugins de calidad (checkstyle, jacoco, etc.). En CI siempre usar `verify` para
atrapar más bugs.

**P: ¿Y el `-B`?**
R: "Batch mode". Elimina las barras de progreso interactivas de Maven. Los logs
del CI se leen mucho mejor sin caracteres de control.

**P: ¿Por qué el test de `PingController` es standalone y no `@WebMvcTest`?**
R: Standalone solo instancia el controlador (milisegundos). `@WebMvcTest` levanta
media capa web de Spring. Para un endpoint trivial, standalone es más rápido =
CI más barato = feedback más rápido al programador.

**P: ¿Cuánto cuesta esto?**
R: En repos **públicos**, GitHub Actions es gratis e ilimitado. En repos privados,
2000 minutos/mes gratis en el plan Free. Este workflow tarda ~1 minuto, así que
puedes hacer ~2000 pushes al mes gratis.

**P: ¿Cómo agrego el paso de deploy (CD)?**
R: En el módulo 26 (Docker) tienes el `Dockerfile`. Agregas un segundo job
`deploy:` con `needs: build`, que usa `docker/build-push-action` para subir la
imagen a un registry, y `appleboy/ssh-action` para reiniciar el contenedor en
producción. Fuera del alcance de este módulo.

**P: ¿El workflow se ejecuta si otro alumno hace push a su fork?**
R: Sí, en su fork. Pero los `secrets` (contraseñas) no se propagan a forks por
seguridad, así que un fork solo puede correr CI (sin CD).
