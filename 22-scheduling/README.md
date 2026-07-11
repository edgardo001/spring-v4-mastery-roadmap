## 22 — Tareas Programadas (@Scheduled y Cron)

### Propósito
Aprender a ejecutar código automáticamente en tu aplicación Spring Boot en base a un horario, un intervalo de tiempo fijo o una expresión Cron, utilizando la anotación `@Scheduled`.

### Problema que resuelve
En la mayoría de las aplicaciones empresariales, necesitas procesos automáticos que se ejecuten "de fondo" (background jobs) sin intervención del usuario:
- Enviar reportes de ventas diarios a las 8:00 AM.
- Borrar tokens de seguridad expirados de la base de datos cada 15 minutos (limpieza).
- Consultar un banco externo cada hora para verificar el estado de las transferencias pendientes.
- Procesar nóminas el día 30 de cada mes a la medianoche.

Hacer esto manualmente o usar hilos infinitos (`while(true) { sleep(); }`) es una muy mala práctica, consume recursos y es difícil de sincronizar o gestionar.

### Cómo lo resuelve
Spring provee la anotación `@Scheduled`, que permite convertir cualquier método vacío (sin argumentos) en una tarea programada administrada por el TaskScheduler nativo de Spring.

### Por qué aprenderlo
El procesamiento Batch y los Cron Jobs son piezas angulares de cualquier backend moderno. Si no sabes programar tareas repetitivas, tendrías que depender de herramientas externas (como Crontab en Linux) para llamar a tus endpoints, fragmentando la lógica de negocio y complicando el despliegue de tu aplicación.

```mermaid
graph TD
    A["TaskScheduler de Spring"] -->|"fixedRate=5000"| B["Método 1<br/>(Cada 5 segundos)"]
    A -->|"cron='0 0 8 * * *'"| C["Método 2<br/>(Todos los días 8 AM)"]
    
    B --> D["Limpia Base de Datos"]
    C --> E["Envía Reporte por Correo"]

    style A fill:#339af0,color:#fff
    style B fill:#51cf66,color:#fff
    style C fill:#ffa94d,color:#fff
```

---

### Glosario Básico

#### `@EnableScheduling`
Anotación colocada en tu clase de configuración o Main para activar el soporte de tareas programadas. Obligatoria.

#### `@Scheduled`
Se coloca sobre el método que deseas ejecutar automáticamente. Soporta diferentes estrategias de programación (`fixedRate`, `fixedDelay`, `cron`).

#### `fixedRate`
Ejecuta la tarea cada `X` milisegundos, contando **desde que inició** la ejecución anterior.

#### `fixedDelay`
Ejecuta la tarea `X` milisegundos **después de que terminó** la ejecución anterior.

#### `Cron Expression`
Una cadena de 6 campos (Segundos, Minutos, Horas, Día del Mes, Mes, Día de la Semana) que define un patrón de tiempo exacto (Ej: `0 15 10 * * MON-FRI` = 10:15 AM de lunes a viernes).

---

### Conceptos

#### 1. Configuración Básica e Intervalos Fijos
- **Qué es** — Activar la anotación central y configurar un método para que corra cada cierta cantidad de milisegundos.
- **Por qué importa** — Es la solución ideal para procesos de "polling" (revisar si hay cambios continuamente).
- **Código** — Tarea de intervalo fijo:
  ```java
  @SpringBootApplication
  @EnableScheduling // <-- Paso 1: Activar Scheduler
  public class ScheduledApp { ... }
  ```
  
  ```java
  @Service
  @Slf4j
  public class CleanupService {
  
      // Se ejecutará cada 10 segundos, no importa cuánto tarde
      @Scheduled(fixedRate = 10000)
      public void cleanupExpiredSessions() {
          log.info("Iniciando limpieza de sesiones a las: {}", LocalDateTime.now());
          // lógica: repository.deleteExpired();
      }
      
      // Esperará 5 segundos DESPUÉS de terminar para volver a correr
      @Scheduled(fixedDelay = 5000, initialDelay = 1000) 
      public void pollExternalApi() {
          log.info("Revisando API externa...");
          // initialDelay hace que la primera vez espere 1 segundo antes de arrancar
      }
  }
  ```
- **Analogía** — `fixedRate` es un tren que sale de la estación cada 10 minutos exactos, no importa si va vacío o lleno. `fixedDelay` es un taxi que espera a que termines tu viaje actual; una vez que te bajas, espera 5 minutos fumando un cigarrillo antes de tomar al siguiente pasajero.

#### 2. Expresiones Cron
- **Qué es** — En la industria, las horas importan. Para ejecutar un proceso en una fecha/hora exacta, usamos el estándar Unix `cron`. Spring soporta 6 campos: `Segundos Minutos Horas Día-de-mes Mes Día-de-semana`.
- **Por qué importa** — Permite reglas complejas como: "El último viernes de cada mes a las 23:59".
- **Código** — Tareas por calendario:
  ```java
  @Service
  @Slf4j
  public class ReportingService {
  
      // 0 = Segundos (en el segundo exacto cero)
      // 0 = Minutos
      // 8 = Horas (8 AM)
      // * = Todos los días del mes
      // * = Todos los meses
      // MON-FRI = Lunes a Viernes
      @Scheduled(cron = "0 0 8 * * MON-FRI", zone = "America/Bogota")
      public void sendDailyReport() {
          log.info("Enviando reporte diario corporativo a las 8 AM");
          // emailService.sendReport();
      }
      
      // Es MUY BUENA PRÁCTICA sacar los magic strings al application.yml
      @Scheduled(cron = "${app.cron.billing}")
      public void processMonthlyBilling() {
          log.info("Procesando cobros mensuales según configuración");
      }
  }
  ```
  ```yaml
  # application.yml
  app:
    cron:
      # "0 0 0 1 * *" -> Día 1 de cada mes a medianoche
      billing: "0 0 0 1 * *"
  ```

#### 3. Configuración Custom del Thread Pool
- **Qué es** — Por defecto, Spring usa un Thread Pool con **UN SOLO HILO (Pool Size = 1)** para TODAS las tareas `@Scheduled`. 
- **Por qué importa** — Si tienes 3 tareas programadas, y la Tarea 1 se bloquea por 5 horas esperando una API, la Tarea 2 y 3 **NUNCA SE EJECUTARÁN** porque el único hilo disponible está ocupado. Esto destruye aplicaciones en producción. Siempre debes configurar un Pool más grande.
- **Código** — Solución segura para Producción:
  ```java
  @Configuration
  @EnableScheduling
  public class SchedulerConfig implements SchedulingConfigurer {
  
      @Override
      public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
          ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
          
          scheduler.setPoolSize(10); // Permitir hasta 10 tareas al mismo tiempo
          scheduler.setThreadNamePrefix("CronThread-");
          scheduler.initialize();
          
          taskRegistrar.setTaskScheduler(scheduler);
      }
  }
  ```

#### 4. Ejecución Condicional con ShedLock (Microservicios)
- **Qué es** — En microservicios, despliegas tu app en 3 servidores diferentes. Si configuras un Cron para enviar correos a las 8:00 AM, **¡se enviarán 3 correos duplicados!** (uno por cada servidor).
- **Por qué importa** — Necesitas una librería de coordinación distribuida. `ShedLock` guarda un "candado" (lock) en la Base de Datos compartida. A las 8:00 AM, el servidor que llega primero pone el candado y ejecuta la tarea. Los otros servidores ven el candado y no hacen nada.
- **Casos de Uso Empresariales** — Facturación distribuida, reportes consolidados en contenedores Kubernetes.

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Tareas no se ejecutan | La tarea 1 se quedó colgada en un bucle infinito o esperando respuesta HTTP y el pool es 1 | Configurar un `ThreadPoolTaskScheduler` con size > 1. Añadir timeouts a las peticiones HTTP. |
| El cron no funciona en producción | Tu servidor local (Mac/Windows) está en tu zona horaria, pero el servidor AWS de producción está en UTC | Definir explícitamente el atributo `zone = "America/Mexico_City"` en el `@Scheduled(cron="...", zone="...")`. |
| `@Scheduled` y `@Async` | Son conceptos distintos. Si un `@Scheduled` llama a tareas pesadas de 1 segundo | Puedes mezclar ambos: que el método Cron sea corto y asigne tareas con `@Async` para liberar el hilo del cron de inmediato. |
| Ejecuciones duplicadas | Tu aplicación está corriendo en 2 contenedores Docker simultáneos | Integrar ShedLock o Quartz (usar base de datos como coordinador global). |

---

### Ejercicios
1. Crea una tarea `@Scheduled` con `fixedRate=3000` que imprima un log con la hora actual. Inicia la app y mira la consola.
2. Agrega una pausa dentro de la tarea (`Thread.sleep(5000)`). ¿Qué pasa con el `fixedRate`? Cambia a `fixedDelay=3000` y observa cómo el comportamiento del logueo cambia.
3. Crea un Cron Job que se ejecute en los primeros 10 segundos de cada minuto (`"0-10 * * * * *"`).
4. **(Avanzado)** Modifica la clase de configuración implementando `SchedulingConfigurer` para darle un Pool de 5 hilos.
5. Inyecta la expresión Cron desde el `application.yml` usando `@Value` o la notación de propiedades `${...}` directamente en la anotación `@Scheduled`.

### Cómo ejecutar
```bash
cd 22-scheduling
mvn spring-boot:run

# No hay necesidad de curl, solo mira la consola para ver los logs apareciendo automáticamente.
```

### Archivos del Proyecto (implementación real de este módulo)
| Archivo | Propósito |
|---------|-----------|
| `SchedulingApplication.java` | Punto de entrada (`SpringApplication.run`). |
| `config/SchedulingConfig.java` | Activa `@EnableScheduling` (interruptor general del scheduler). |
| `service/HeartbeatService.java` | `@Scheduled(fixedRate=5000)` + `@Scheduled(cron="*/2 * * * * *")`, contadores atómicos. |
| `controller/HeartbeatController.java` | `GET /api/heartbeat` → `{tick, cron}`. |
| `application.yml` | `server.port=8080`, `spring.application.name`. |
| `build.sh` / `build.ps1` | Compila con JDK 21 + Maven portable → `target/scheduling-1.0.0.jar`. |
| `src/test/.../SchedulingApplicationTests.java` | `contextLoads` con `@SpringBootTest`. |
| `src/test/.../HeartbeatServiceTest.java` | Test unitario invocando los métodos directamente (rápido, sin `Thread.sleep`). |
| `src/test/.../HeartbeatControllerTest.java` | MockMvc **standalone** (Boot 4 no tiene `@WebMvcTest`). |

---

### Antes (Java 8 / manual) vs Ahora (Java 21 + Spring Boot 4)

Comparación aplicada al mismo problema: "quiero un método que se ejecute automáticamente cada 5 segundos y otro cada 2 segundos".

| Aspecto | ANTES (`java.util.Timer`) | AHORA (`@Scheduled`) |
|---------|---------------------------|----------------------|
| Activación | `new Timer().scheduleAtFixedRate(new TimerTask(){...}, 0, 5000);` en el main | Anotación `@Scheduled(fixedRate = 5000)` sobre el método |
| Ciclo de vida | Tú creas y matas el Timer manualmente | Spring lo gestiona junto con el contexto |
| Cron real | `Timer` no soporta cron; había que calcular a mano con `Calendar` | `@Scheduled(cron = "*/2 * * * * *", zone = "...")` |
| Reintentos / errores | Si la `TimerTask` lanza excepción, el Timer entero muere | Spring loguea y continúa |
| Configuración externa | Recompilar para cambiar el intervalo | `@Scheduled(cron = "${app.cron.billing}")` en YAML |
| Testeo | Muy difícil (hilos manuales) | Llamás el método directamente en JUnit |

Snippet ANTES (Java 8):
```java
public static void main(String[] args) {
    Timer timer = new Timer(true);
    timer.scheduleAtFixedRate(new TimerTask() {
        int count = 0;
        @Override public void run() {
            count++;
            System.out.println("tick #" + count);
        }
    }, 0, 5000);
    // Y el cron cada 2s... implementar a mano con Calendar. Un dolor.
}
```

Snippet AHORA (Java 21 + Boot 4):
```java
@Service
public class HeartbeatService {
    private final AtomicInteger tickCount = new AtomicInteger();
    @Scheduled(fixedRate = 5000)
    public void heartbeat() { tickCount.incrementAndGet(); }

    @Scheduled(cron = "*/2 * * * * *")
    public void cronTick() { /* ... */ }
}
```

Concurrencia (contador):

| ANTES (Java 8) | AHORA (Java 21) |
|----------------|-----------------|
| `private int count; public synchronized void inc(){count++;}` | `private final AtomicInteger count = new AtomicInteger(); count.incrementAndGet();` |

---

### FAQ del Alumno

**P: ¿Qué diferencia hay entre `fixedRate` y `fixedDelay`?**
R: `fixedRate` mide desde el INICIO de la ejecución anterior. `fixedDelay` mide desde el FIN. Si tu método tarda 3s y ponés `fixedRate=5000`, entre inicios pasan 5s. Con `fixedDelay=5000`, entre fin e inicio pasan 5s (total 8s por ciclo).

**P: Si no pongo `@EnableScheduling`, ¿qué pasa con mis `@Scheduled`?**
R: Nada. Spring los ignora silenciosamente. No hay error de compilación ni de arranque. Es un error clásico de principiantes: buscar por qué "no corre mi tarea" y descubrir que faltaba activar el motor.

**P: ¿Puede un método `@Scheduled` recibir parámetros?**
R: No. Debe ser `void`, sin argumentos. Spring dispara la ejecución, no tiene forma de saber qué pasarle. Si necesitás datos, obtenelos dentro del método (repositorio, config, etc.).

**P: ¿Por qué mi test con `Thread.sleep(6000)` tarda 6 segundos?**
R: Porque el test real espera al scheduler. En este módulo evitamos ese enfoque llamando `service.heartbeat()` directamente (test <100ms). La lógica que probamos es "el contador sube", no "Spring sabe programar" (eso ya lo probó Spring).

**P: ¿Y si tengo 3 `@Scheduled` y todos se disparan al mismo segundo?**
R: Por defecto Spring usa **1 solo hilo**. Se ejecutan en secuencia y las siguientes esperan. Si una se cuelga, las demás no corren nunca. En producción hay que configurar un `ThreadPoolTaskScheduler` con `poolSize > 1` (ver concepto #3 del README).

**P: Si despliego 3 réplicas en Kubernetes, ¿se ejecuta 3 veces?**
R: SÍ. Y eso puede ser catastrófico (3 emails de facturación al mismo cliente). Solución: **ShedLock** (candado en BD compartida) o **Quartz** con almacenamiento JDBC.

**P: ¿Por qué usar `AtomicInteger` en vez de `int` con `synchronized`?**
R: Ambos funcionan. `AtomicInteger` es más liviano (usa CPU instructions atómicas), más idiomático y evita olvidar `synchronized` en un getter. En un contador simple es la elección natural.

**P: ¿Puedo tener `@Scheduled` en un `@RestController`?**
R: Técnicamente sí, pero es mala práctica. Separa responsabilidades: el controller responde HTTP, el service ejecuta la lógica programada. Este módulo lo respeta.

**P: ¿El scheduler corre en tests?**
R: Sí, en cuanto arrancás `@SpringBootTest` (por eso `SchedulingApplicationTests` puede tener contadores > 0 si el test durara). Para tests unitarios rápidos, usamos POJOs directos sin arrancar Spring.

**P: ¿Cómo paso el intervalo por configuración?**
R: `@Scheduled(fixedRateString = "${app.tick.rate:5000}")` o `@Scheduled(cron = "${app.cron.report}")`. El `:5000` es el default si la propiedad no existe.
