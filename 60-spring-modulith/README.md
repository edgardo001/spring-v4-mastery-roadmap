# Modulo 60 - Spring Modulith avanzado (simulado)

## Que se aprende

Los patrones criticos de Spring Modulith 1.x que hacen viable un monolito
modular en produccion:

- **Event Publication Registry**: persistencia transaccional de cada evento
  publicado, con marca de completado por listener.
- **`@ApplicationModuleListener`**: listener asincrono transaccional que se
  ejecuta tras el commit y cierra el registro cuando termina.
- **Scenario API / Documenter**: (mencion — requiere la libreria real).

## Limitacion tecnica (importante)

**Spring Modulith 1.x depende de Spring Boot 3.x**. No existe (aun) release
compatible con Boot 4.1.0 — ver MEMORY.md, entrada del modulo 39. Por eso
este modulo simula manualmente los patrones. Cuando publiquen Modulith 2.x
compatible con Boot 4, migrar es sustituir tres piezas (ver seccion
"Patron de migracion").

## Antes vs Ahora

### Antes (modulo 39 - eventos in-memory)

```java
publisher.publishEvent(new OrderCreatedEvent(id, customer));
```

- El evento vive en un `ApplicationEventMulticaster` en memoria.
- Si el proceso se cae entre el commit del `Order` y la ejecucion del
  listener, la notificacion se **pierde para siempre**.
- No hay auditoria de que se publico, cuando, ni si se proceso.

### Ahora (modulo 60 - Event Publication Registry)

```java
registeredPublisher.publish("Order", new OrderCreatedEvent(id, customer));
```

- **1)** El evento se persiste en `event_publication` DENTRO de la misma
  transaccion que salva el `Order`. Atomicidad garantizada por JPA/JDBC.
- **2)** El listener corre `@Async @TransactionalEventListener(AFTER_COMMIT)`.
- **3)** Al terminar, hace `registry.markCompleted(pubId)` — deja
  `completed_at != null`.
- **4)** Publicaciones huerfanas (`completed_at is null` con >N segundos)
  pueden ser reprocesadas al arrancar.

## Estructura

```
com.springroadmap.modulith2
├── ModulithApplication.java          (bootstrap + @EnableAsync)
├── registry/                         (nuestro Event Publication Registry)
│   ├── EventPublication.java         (entidad JPA)
│   ├── EventPublicationRepository.java
│   ├── EventPublicationRegistry.java (API @Component)
│   ├── RegisteredEventPublisher.java (publisher transaccional)
│   └── RegisteredEvent.java          (wrapper que lleva el pubId)
├── orders/                           (modulo dominio)
│   ├── Order.java                    (API publica)
│   ├── OrderCreatedEvent.java        (API publica)
│   ├── OrderService.java
│   ├── OrderController.java
│   └── internal/OrderRepository.java (detalle no exportado)
└── notifications/                    (modulo dominio)
    ├── AsyncNotificationListener.java
    └── internal/NotificationHistory.java
```

## Build

```powershell
.\build.ps1
```

```bash
./build.sh
```

Genera `target/spring-modulith-1.0.0.jar`.

## Ejecutar

```bash
java -jar target/spring-modulith-1.0.0.jar
curl -X POST "http://localhost:8080/api/orders?customer=Ana"
```

## Tests

- `ModulithApplicationTests.contextLoads` — arranque completo con H2.
- `EventPublicationTest.publicationIsPersistedAndThenCompletedAsync`:
  publica un evento, verifica que existe en el registry, espera 500ms al
  listener async, verifica que `completed_at` quedo seteado.

## FAQ

**Q: Por que Modulith real no funciona con Boot 4.1?**
A: Modulith 1.x fija dependencias sobre APIs internas de Boot 3 (spring-boot
autoconfigure, jmolecules, event API). Boot 4.1 rompio esas APIs. Hay que
esperar a Modulith 2.x.

**Q: Por que un wrapper `RegisteredEvent` y no publico directo el evento?**
A: El listener necesita saber cual fila del registry cerrar. Sin el id
tendria que buscar por contenido — fragil. `@ApplicationModuleListener`
real lo hace igual: envuelve tu evento con metadata de la publicacion.

**Q: Por que `@TransactionalEventListener(AFTER_COMMIT)` y no `@EventListener`?**
A: AFTER_COMMIT garantiza que el listener NUNCA ve un evento cuya
transaccion despues hizo rollback. Sin esto, notifications avisaria de
ordenes que en realidad no existen.

**Q: Por que `@Async` ademas?**
A: Sin `@Async`, el listener corre en el hilo del commit y bloquea la
respuesta HTTP. Con `@Async` liberas el hilo y el registry pasa a ser
tu red de seguridad ante fallos del listener.

**Q: Como reprocesar publicaciones huerfanas al arrancar?**
A: Al `ApplicationReadyEvent`, hacer `registry.findAll()` filtrando
`completed_at is null` y republicar los `RegisteredEvent`. En Modulith
real esto se hace solo — ver `IncompleteEventPublications`.

**Q: Y el Documenter / Scenario API?**
A: Requieren la libreria real. `ApplicationModules.of(App.class).verify()`
valida arquitectura (nadie importa `internal` ajeno) y
`ScenarioTests` orquestan publicacion + espera + assert. Cuando migres
al Modulith real, apuntan a los mismos paquetes que ya tienes.

## Patron de migracion (Modulith real cuando compatible con Boot 4)

1. Anadir dependencia:

   ```xml
   <dependency>
     <groupId>org.springframework.modulith</groupId>
     <artifactId>spring-modulith-starter-jpa</artifactId>
   </dependency>
   ```

2. Reemplazar `RegisteredEventPublisher` por `ApplicationEventPublisher`
   plano. Modulith intercepta la publicacion y persiste sola.

3. Reemplazar `@Async @TransactionalEventListener(AFTER_COMMIT)` por
   `@ApplicationModuleListener` — hace lo mismo pero cierra el registro
   automaticamente.

4. Borrar entidad `EventPublication`, repositorio y `EventPublicationRegistry`.
   Modulith trae su propio schema (`schema-h2.sql`, `schema-postgresql.sql`).

5. Anadir test:

   ```java
   @Test
   void verifiesModuleBoundaries() {
       ApplicationModules.of(ModulithApplication.class).verify();
   }
   ```

## Convenciones (compartidas con el resto del roadmap)

- Spring Boot 4.1.0, Java 21.
- `groupId = com.springroadmap`, package raiz `com.springroadmap.modulith2`.
- Scripts portables `build.ps1` / `build.sh` que resuelven JDK y Maven
  desde el directorio padre (`../jdk-21.0.11+10`, `../apache-maven-3.9.16`).
- YAML sin bloque `spring.jackson:`.
- Sin Lombok. MockMvc standalone donde aplique.
