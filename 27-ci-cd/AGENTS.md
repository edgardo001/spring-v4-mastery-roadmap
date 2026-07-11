# AGENTS - Módulo 27 CI/CD

Instrucciones para agentes que trabajen sobre este módulo.

## Reglas duras
- **NO** modificar `groupId`, `artifactId`, `finalName`, package base.
- **NO** introducir Lombok.
- **NO** ejecutar builds automáticos: dejar el `mvn` para el usuario o para el runner de GitHub Actions.
- **NO** mover `.github/workflows/ci.yml` fuera del módulo (debe vivir en `27-ci-cd/.github/workflows/`).

## Reglas blandas
- Todo YAML del workflow debe llevar comentarios línea por línea.
- Todo controlador/test debe incluir bloque de comentario "por qué".
- El README debe mantener el patrón "Antes vs Ahora" + FAQ Alumno.

## Estructura esperada
```
27-ci-cd/
  .github/workflows/ci.yml
  src/main/java/com/springroadmap/cicd/
  src/test/java/com/springroadmap/cicd/
  pom.xml
  build.sh / build.ps1
  README.md / MEMORY.md / AGENTS.md
```
