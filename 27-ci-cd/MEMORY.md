# MEMORY - Módulo 27 CI/CD

## Estado
- Implementado: app mínima Spring Boot 4.1.0 + workflow GitHub Actions.
- Endpoint: `GET /api/ping` -> `"pong"`.
- Artefacto: `target/ci-cd-1.0.0.jar`.

## Decisiones
- **Workflow scoped por path** (`paths: ['27-ci-cd/**']`): evita ejecutar el CI del módulo 27 cuando se modifican otros módulos del roadmap.
- **`working-directory: 27-ci-cd`** en defaults: los comandos shell corren dentro del módulo, pero el path del artifact debe llevar el prefijo `27-ci-cd/` porque `upload-artifact` resuelve desde la raíz del repo.
- **Test standalone MockMvc** en vez de `@WebMvcTest`: más rápido en CI.
- **Sin Lombok**.

## Convenciones
- groupId: `com.springroadmap`
- artifactId: `ci-cd`
- package: `com.springroadmap.cicd`
- finalName: `ci-cd-1.0.0`
- Java 21, Spring Boot 4.1.0.

## Próximos pasos sugeridos
- Añadir job `deploy` (Docker build + push) cuando se integre con módulo 26.
- Branch protection en `main` requiriendo el check `CI Module 27`.
