# Modulo 39 - Monolito Modular

Monolito organizado por **modulos de dominio** (paquetes) que se comunican
por **eventos de Spring** en vez de llamadas directas.

- Spring Boot 4.1.0 + Java 21
- `groupId=com.springroadmap` / `artifactId=monolito-modular` / v1.0.0
- Paquete raiz: `com.springroadmap.modulith`
- Artefacto: `target/monolito-modular-1.0.0.jar`

## Estructura

```
com.springroadmap.modulith/
├── ModulithApplication.java
├── orders/                          <-- MODULO orders (API publica)
│   ├── Order.java                   (record publico)
│   ├── OrderService.java            (@Service publico)
│   ├── OrderController.java         (adaptador HTTP del modulo)
│   └── internal/                    <-- detalle, no usar desde fuera
│       ├── OrderRepository.java
│       └── OrderCreatedEvent.java
└── notifications/                   <-- MODULO notifications
    ├── NotificationListener.java    (@EventListener)
    └── internal/
        └── NotificationHistory.java
```

Regla de oro: **nada fuera del paquete `X` puede importar `X.internal.*`**
(excepto los eventos, que son el "contrato" entre modulos).

## Endpoint

```
POST /api/orders?customer=Ana
```

Devuelve `{ "id": 1, "customer": "Ana" }` y dispara `OrderCreatedEvent`.
`NotificationListener` lo captura e incrementa un contador.

## Antes vs Ahora

### Antes - Monolito espagueti

```java
@Service
class OrderService {
    private final NotificationService notifications; // acoplamiento directo
    private final AuditService audit;
    private final EmailService email;
    public Order create(String c) {
        Order o = repo.save(c);
        notifications.send(o);   // si notifications cae, orders cae
        audit.log(o);
        email.send(o);
        return o;
    }
}
```

Problemas:
- Un cambio en notifications rompe orders.
- Imposible testear orders sin cargar medio contexto.
- No se puede extraer notifications como microservicio sin cirugia mayor.
- Todo en un solo paquete: nadie sabe donde termina un dominio.

### Ahora - Monolito modular con eventos

```java
@Service
public class OrderService {
    private final OrderRepository repo;
    private final ApplicationEventPublisher publisher;
    public Order createOrder(String c) {
        Order o = repo.save(c);
        publisher.publishEvent(new OrderCreatedEvent(o.id(), o.customer()));
        return o;
    }
}

@Component
public class NotificationListener {
    @EventListener
    void on(OrderCreatedEvent e) { /* ... */ }
}
```

Ventajas:
- `orders` no conoce `notifications`. Se puede borrar el paquete `notifications` completo y `orders` sigue compilando.
- Agregar un nuevo consumidor (audit, email, metrics) es crear otro `@EventListener`, sin tocar `orders`.
- Cada modulo tiene su propia frontera (`internal/`). Los detalles no se filtran.
- Antesala natural a microservicios: cambiar `ApplicationEventPublisher` por un broker (Kafka/Rabbit) y listo (modulo 40 event-driven, modulo 41 microservicios).

## Por que NO se uso Spring Modulith

Spring Modulith 1.x depende de Spring Boot 3.x. Al momento de escribir este
modulo, **no hay version publicada compatible con Spring Boot 4.1.0**
(`spring-modulith-bom` no resuelve contra Boot 4).

En cuanto salga Spring Modulith 2.x (o 1.3+) compatible con Boot 4, la
migracion es trivial:

1. Agregar `spring-modulith-starter-core` al `pom.xml`.
2. Anotar cada paquete raiz de modulo con `package-info.java` y `@ApplicationModule`.
3. Agregar un test:
   ```java
   @Test void verify() { ApplicationModules.of(ModulithApplication.class).verify(); }
   ```
   Esto valida en compilacion/test que **nadie importe `internal`** de otro modulo.
4. Opcionalmente cambiar `@EventListener` por `@ApplicationModuleListener` para
   obtener transacciones y reintentos automaticos.

Mientras tanto, este modulo aplica los **mismos principios manualmente**:
convencion `internal/`, comunicacion por eventos, un `@Service` publico por
modulo.

## Build

```powershell
.\build.ps1
```

```bash
./build.sh
```

Ambos scripts usan el toolchain portable en `../tools/jdk-21` y `../tools/maven`.

## Tests

- `ModulithApplicationTests.contextLoads` - arranca el contexto.
- `OrderServiceTest` (`@SpringBootTest`) - crea orden real y verifica que
  `NotificationListener.getNotified()` se incremento (prueba end-to-end de
  la comunicacion inter-modulo via eventos).
- `OrderControllerTest` - MockMvc **standalone**, service mockeado con
  Mockito manual (sin `@MockBean`, sin `@WebMvcTest`).

## FAQ Alumno

**P: Si es un solo JAR, por que llamarlo "modular"?**
R: Modularidad no es sinonimo de multiples JARs. Es tener **fronteras claras**
entre dominios: un modulo puede evolucionar sin romper otros. En este proyecto
la frontera es el paquete y la convencion `internal/`.

**P: Y si alguien igual importa `orders.internal.OrderRepository` desde notifications?**
R: Compila y funciona. Por eso Spring Modulith existe: para **fallar el build**
si eso ocurre. Sin Modulith, la disciplina es humana + code review + reglas
de ArchUnit si quieres automatizar.

**P: Por que el evento vive en `orders.internal` y no en un paquete publico?**
R: Trade-off. En un modulo estricto los eventos son API publica (irian a
`orders.events`). Aqui lo dejamos "medio publico" para simplificar. En
produccion, evento = contrato = publico.

**P: `@EventListener` es sincrono, no bloquea el POST?**
R: Si, es sincrono por defecto y corre en el mismo hilo/tx. Para asincrono
se usa `@Async` + `@EventListener`, o `@TransactionalEventListener` para
esperar al commit. Ver modulo 21 (async) y 40 (event-driven).

**P: Cuando pasar de "monolito modular" a "microservicios"?**
R: Cuando un modulo necesita **escalar/desplegarse/fallar** de forma independiente.
Antes de eso, monolito modular = 90% del beneficio, 10% del costo operativo.
Este es exactamente el mensaje del modulo 41.

**P: Por que `ApplicationEventPublisher` y no un `MessageBus` propio?**
R: Porque ya viene con Spring, es sincrono/asincrono a eleccion, y migra
directo a `ApplicationModuleListener` de Spring Modulith cuando este soporte
Boot 4. Reinventarlo es costo sin beneficio.
