## 29 — Configuración Centralizada (Spring Cloud Config)

### Propósito
Aprender a externalizar y centralizar la configuración de tus aplicaciones (`application.yml`) utilizando Spring Cloud Config. En lugar de que cada microservicio guarde sus propias contraseñas y configuraciones internamente, todos las leerán de un servidor de configuración único al momento de arrancar.

### Problema que resuelve
Imagina que tienes una arquitectura de 10 microservicios (Ventas, Inventario, Usuarios, etc.) y la contraseña de la Base de Datos cambia. 
- **Infierno de Mantenimiento:** Debes ir a los repositorios de los 10 proyectos, cambiar 10 archivos `application.yml`, recompilar 10 proyectos y hacer 10 despliegues (deploys) en producción.
- **Auditoría:** No tienes un registro claro de quién cambió qué configuración y cuándo, porque las configuraciones están dispersas.

### Cómo lo resuelve
Se crea un nuevo microservicio llamado **Config Server**. Este servidor se conecta a un repositorio Git (ej: GitHub) donde guardas todos tus `.yml`.
Cuando el microservicio de "Ventas" arranca, antes de hacer cualquier cosa, le pregunta al Config Server: *"Hola, soy Ventas, pásame mi configuración para el entorno de Producción"*. El servidor lee el Git y se la entrega. Si la contraseña cambia, solo haces un commit en el Git central.

### Por qué aprenderlo
Es el pilar fundamental del patrón arquitectónico de Microservicios (conocido como *Externalized Configuration* de los 12-Factor App). Ninguna empresa moderna con microservicios guarda configuraciones directamente en el código fuente del microservicio.

```mermaid
graph TD
    A["Repositorio Git Central (YAMLs)"] -->|Lee los archivos| B["Spring Cloud Config Server<br/>(Puerto 8888)"]
    
    C["Microservicio Ventas (Client)"] -->|1. Request: /ventas/prod| B
    B -->>|2. Retorna JSON de config| C
    C -->|3. Arranca con la BD correcta| DB1[(Base Datos Prod)]
    
    D["Microservicio Usuarios (Client)"] -->|1. Request: /usuarios/dev| B
    B -->>|2. Retorna JSON de config| D
```

---

### Glosario Básico

#### `Config Server`
La aplicación Spring Boot central que actúa como servidor. Expone una API REST para entregar configuraciones. Se activa con `@EnableConfigServer`.

#### `Config Client`
Tus microservicios de negocio (Ventas, Usuarios). Llevan una dependencia especial que intercepta el arranque (boot) de la aplicación, hace la petición HTTP al servidor, descarga las variables y luego continúa arrancando.

#### `@RefreshScope`
Anotación mágica de Spring Cloud. Si cambias un dato en el Git, puedes enviarle una petición `POST /actuator/refresh` a tu microservicio. Spring destruirá los beans anotados con `@RefreshScope` y los volverá a crear inyectando la nueva configuración, **sin necesidad de reiniciar la aplicación**.

---

### Conceptos

#### 1. Levantando el Config Server
- **Qué es** — Es un proyecto Spring Boot completamente independiente.
- **Código** — Así se configura:
  
  **pom.xml**
  ```xml
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-config-server</artifactId>
  </dependency>
  ```
  
  **Application.java**
  ```java
  @SpringBootApplication
  @EnableConfigServer // Activa el rol de servidor
  public class ConfigServerApplication {
      public static void main(String[] args) {
          SpringApplication.run(ConfigServerApplication.class, args);
      }
  }
  ```
  
  **application.yml del Servidor**
  ```yaml
  server:
    port: 8888 # Puerto estándar de la industria para el Config Server
  
  spring:
    cloud:
      config:
        server:
          git:
            # URL del repositorio Git donde están guardados todos los YML de todos los microservicios
            uri: https://github.com/tu-usuario/repo-de-configuraciones.git
            default-label: main
  ```

#### 2. Estructura del Repositorio Git
En el repositorio Git (`repo-de-configuraciones.git`), simplemente creas archivos `.yml` con nombres específicos. El servidor los sirve automáticamente:
- `application.yml` -> Configuración global (Aplica a TODOS los microservicios)
- `ventas.yml` -> Configuración exclusiva para el microservicio llamado "ventas"
- `ventas-prod.yml` -> Configuración exclusiva de "ventas" para el entorno de Producción.

Si accedes a `http://localhost:8888/ventas/prod` en el navegador, verás cómo el servidor mezcla inteligentemente `application.yml` + `ventas.yml` + `ventas-prod.yml` en un solo JSON enorme.

#### 3. Configurando el Config Client (Tus Microservicios)
- **Qué es** — Tus aplicaciones de negocio necesitan saber dónde está el Config Server para preguntarle por sus variables antes de intentar conectarse a la Base de Datos.
- **Código** — Configuración de un cliente:
  
  **pom.xml**
  ```xml
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-config</artifactId>
  </dependency>
  ```
  
  **application.yml del Cliente** (¡Ojo! En versiones viejas de Spring era en un archivo llamado `bootstrap.yml`)
  ```yaml
  spring:
    application:
      name: ventas-service # Este nombre debe coincidir con el ventas.yml en el Git
    config:
      import: "optional:configserver:http://localhost:8888/" # Dónde está el servidor
  ```
- **Analogía** — El Client es como un empleado en su primer día de trabajo. En lugar de adivinar cuál es su escritorio, lo primero que hace al entrar al edificio es ir a Recursos Humanos (Config Server) y decir: "Soy el de Ventas". RH le entrega su manual de procesos (YAML) y recién ahí el empleado empieza a trabajar.

#### 4. Recarga en Caliente (`@RefreshScope`)
- **Qué es** — Normalmente, si una variable cambia, tienes que reiniciar el microservicio para que lea el `.yml` de nuevo. Spring Cloud permite recargar Beans específicos al vuelo usando Actuator.
- **Código**:
  ```java
  @RestController
  @RefreshScope // Si cambian variables en el Git, este Controller se reconstruye al vuelo
  public class TestController {
  
      @Value("${mensaje.bienvenida:Mensaje por defecto}")
      private String mensaje;
  
      @GetMapping("/saludo")
      public String getMensaje() {
          return this.mensaje;
      }
  }
  ```
  Para disparar la recarga, haces un `POST` al cliente:
  ```bash
  curl -X POST http://localhost:8080/actuator/refresh
  ```

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| El cliente no arranca por falta de DB | El servidor de Config no está levantado, el cliente no tiene las credenciales de BD e intenta conectarse con null. | Siempre levanta el Config Server primero. Puedes usar Docker Compose con `depends_on`. |
| `spring.config.import` es ignorado | Versión antigua de Spring Cloud (Anterior a 2020.0.0) | En versiones viejas, la URL del servidor iba en un archivo especial llamado `bootstrap.yml`. En las nuevas, va en el `application.yml` usando `import:`. |
| Secretos expuestos en el Git | Subiste contraseñas en texto plano al Git de configuraciones. | Muy mala práctica. Debes usar encriptación nativa de Config Server (JCE / `{cipher}`) o integrarlo con HashiCorp Vault. |
| El `refresh` no funciona | Falta dependencia de Actuator o el endpoint no está expuesto. | Añadir `spring-boot-starter-actuator` y exponerlo: `management.endpoints.web.exposure.include=refresh`. |

---

### Ejercicios
1. Crea un proyecto independiente (Config Server) en el puerto `8888`.
2. En tu GitHub, crea un repositorio público llamado `config-repo`. Dentro crea un archivo `mi-app.yml` con la clave `saludo: Hola desde GitHub!`. Vincula este repositorio al Config Server.
3. En tu proyecto de trabajo actual (Módulo 29), añade el starter de Config Client y ponle nombre a tu app: `spring.application.name=mi-app`. Enlázalo al puerto 8888.
4. Crea un `@RestController` con `@Value("${saludo}")`. Arranca primero el servidor, luego el cliente. Visita el endpoint y verifica que lee la configuración remota.
5. **(Avanzado)** Cambia el saludo en GitHub y haz commit. Luego envía un `POST` a `/actuator/refresh` en tu cliente y verifica que el endpoint muestra el nuevo saludo sin haber reiniciado la app.

---

## ⚠️ Advertencia — Compatibilidad Spring Cloud + Spring Boot 4.1.0

A la fecha del roadmap (2026-07), **Spring Cloud NO tiene aún un release compatible con Spring Boot 4.1.0**:

- Spring Cloud `2024.x` / `2025.x` → soportan Boot **3.3 / 3.4**.
- `spring-cloud-config-server`, `spring-cloud-starter-config` y `spring-cloud-context` (donde vive `@RefreshScope`) no publican todavía versión Boot-4-compatible.

**Consecuencia práctica:** en este módulo implementamos una **variante simplificada** que enseña el mismo patrón (*Externalized Configuration*) usando únicamente Spring Boot 4 puro. En cuanto Spring Cloud publique release para Boot 4, la migración es mecánica (agregar `spring-cloud-starter-config` + `bootstrap` YAML + `@RefreshScope`).

---

## Implementación del Módulo (variante simplificada)

### Qué hace este proyecto
- Externaliza dos feature flags en `application.yml` (`app.features.beta-enabled`, `app.features.max-retries`).
- Las tipifica con `@ConfigurationProperties(prefix = "app.features")` en `FeatureFlags`.
- Expone `GET /api/features` con los valores actuales.
- Deja el endpoint `POST /actuator/refresh` **declarado** en `management.endpoints.web.exposure.include` — cuando exista `spring-cloud-context` para Boot 4, disparará la recarga en caliente de los beans anotados con `@RefreshScope`.

### Antes vs Ahora (tabla resumen del módulo)

| Aspecto | ANTES (hardcoded) | AHORA (externalizado + preparado para Cloud Config) |
|---------|-------------------|-----------------------------------------------------|
| Ubicación de valores | Constantes en `.java` | `application.yml` (mañana: Git remoto) |
| Cambio de valor | Recompilar + redesplegar | Editar YAML + reiniciar (con Cloud Config real: `POST /actuator/refresh`) |
| Lectura tipada | `Properties.get("...")` + cast manual | `@ConfigurationProperties` con getters tipados |
| Auditoría | Ninguna (perdido en el binario) | Historial `git log` del repo de configs (con Cloud Config real) |
| Multi-entorno | `if (env.equals("prod")) ...` en código | `application-prod.yml` / `spring-cloud-config-prod.yml` remoto |

### FAQ del Alumno

- **¿Por qué no usaste `@RefreshScope` directamente?**
  Vive en `spring-cloud-context`, que aún no tiene release para Boot 4.1.0. Al agregar el starter (cuando exista), basta con anotar `FeatureFlags` o `FeatureController` con `@RefreshScope` y el `POST /actuator/refresh` recreará el bean con los nuevos valores.
- **¿Por qué `FeatureFlags` no es un `record`?**
  Porque `@ConfigurationProperties` necesita setters para el <i>rebind</i> tras un refresh. Los records son inmutables → no sirven aquí.
- **¿Y si quiero probar Spring Cloud Config Server real hoy mismo?**
  Crea un proyecto separado con Spring Boot 3.4 + `spring-cloud-config-server` (Spring Cloud 2024.x). Este módulo del roadmap se queda en Boot 4 hasta que Cloud publique compatibilidad.
- **¿Qué pasa si borro `include: refresh` del YAML?**
  Nada en esta variante (no hay `@RefreshScope` que refrescar). Lo dejamos para que el `application.yml` sea idéntico al que usarías con Cloud Config real.
- **¿Por qué el test del controller no usa `@WebMvcTest`?**
  Fue **eliminada en Spring Boot 4.1.0** junto a las demás test-slices. El patrón portable del roadmap es `MockMvcBuilders.standaloneSetup(...)`.

### Cómo ejecutar

```bash
# Compilar + tests + JAR
./build.sh          # Git Bash
# o
./build.ps1         # PowerShell

# Ejecutar
java -jar target/spring-cloud-config-1.0.0.jar

# Probar
curl http://localhost:8080/api/features
# -> {"betaEnabled":false,"maxRetries":3}

# En cloud config real (cuando exista para Boot 4):
# 1) Cambiar app.features.beta-enabled=true en el repo Git de configs.
# 2) curl -X POST http://localhost:8080/actuator/refresh
# 3) curl http://localhost:8080/api/features → nuevo valor SIN reiniciar.
```

### Archivos del Proyecto

| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Coordenadas Maven, `spring-boot-starter-web` + `-actuator` + `-test`. |
| `build.sh` / `build.ps1` | Scripts portables con JDK 21 + Maven 3.9.16 locales. |
| `src/main/resources/application.yml` | Valores externalizados (`app.features.*`) + expone `refresh` en actuator. |
| `src/main/java/.../CloudConfigApplication.java` | Main + `@ConfigurationPropertiesScan`. |
| `src/main/java/.../FeatureFlags.java` | POJO tipado enlazado a `app.features.*`. |
| `src/main/java/.../FeatureController.java` | `GET /api/features` con los valores vigentes. |
| `src/test/java/.../CloudConfigApplicationTests.java` | `contextLoads` (smoke test). |
| `src/test/java/.../FeatureControllerTest.java` | MockMvc standalone del endpoint. |

**Artefacto:** `target/spring-cloud-config-1.0.0.jar`
