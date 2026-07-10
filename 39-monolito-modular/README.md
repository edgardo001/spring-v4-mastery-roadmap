## 39 — Monolito Modular (Spring Modulith)

### Propósito
Aprender a estructurar una aplicación gigante en un **Monolito Modular** utilizando `Spring Modulith`. Esta arquitectura divide tu código en módulos estrictamente aislados (Ventas, Inventario, Envíos) dentro del *mismo* proyecto Maven, garantizando que el código no se vuelva espagueti sin tener que pagar la extrema complejidad de los Microservicios.

### Problema que resuelve
El dilema eterno: "Monolito Espagueti vs Microservicios Prematuros".
- **Monolito Espagueti**: Todos programan en el mismo código. El módulo de Ventas importa directamente la Entidad JPA de Inventario. Cuando cambias una columna en Inventario, la compilación de Ventas se rompe.
- **Microservicios Prematuros**: Decides separarlo todo en 5 proyectos distintos (Microservicios) desde el día 1. Ahora sufres manejando 5 repositorios, llamadas HTTP que fallan, latencia de red, despliegues complejos y transacciones distribuidas. Tu equipo pierde semanas configurando infraestructura en vez de aportar valor al negocio.

### Cómo lo resuelve
**El Monolito Modular** es el término medio perfecto. Mantienes todo el código en 1 solo proyecto (1 despliegue, 1 Base de Datos, llamadas a memoria ultrarrápidas). Pero, usas `Spring Modulith` para poner **muros de contención** (reglas arquitectónicas).
Ventas no puede importar la Entidad de Inventario (Spring Modulith lanza error de compilación si lo intentas). Solo se comunican a través de Interfaces públicas (APIs internas) o mediante Eventos (Publicar/Suscribir).

### Por qué aprenderlo
La industria se está devolviendo. Muchos equipos que migraron a microservicios descubrieron que su negocio no justificaba tanta complejidad ("Distributed Monolith"). El Monolito Modular, impulsado oficialmente por Spring (Modulith), es la arquitectura por defecto recomendada hoy para proyectos nuevos, ya que permite extraer microservicios fácilmente más adelante si *realmente* se requiere.

```mermaid
graph TD
    subgraph Monolito Modular (1 Solo Proyecto / JVM)
        
        subgraph Módulo Ventas
            V_API["API Pública (Interface)"]
            V_INT["Internals (Entidades, Repos, Privado)"]
        end
        
        subgraph Módulo Inventario
            I_API["API Pública (Interface)"]
            I_INT["Internals (Entidades, Repos, Privado)"]
        end
        
        V_API --> V_INT
        I_API --> I_INT
        
        V_INT -->|"❌ PROHIBIDO (Error Test)"| I_INT
        V_INT -->|"✅ PERMITIDO"| I_API
        
        V_INT -->|"✅ EVENTO ASÍNCRONO"| I_API
    end

    style V_INT fill:#ffc9c9,stroke:#e03131
    style I_INT fill:#ffc9c9,stroke:#e03131
    style V_API fill:#d3f9d8,stroke:#2b8a3e
    style I_API fill:#d3f9d8,stroke:#2b8a3e
```

---

### Glosario Básico

#### `Monolito Modular`
Un diseño donde el código fuente se agrupa por Bounded Contexts (Contextos de Negocio, ej. Facturación, RRHH), manteniendo límites estrictos de acceso entre ellos, pero desplegándose como un solo archivo `.jar`.

#### `Spring Modulith`
Librería oficial (Introducida formalmente con Spring Boot 3.x) que provee validación de arquitectura, pruebas aisladas por módulo y soporte nativo para eventos transaccionales, ayudando a construir Monolitos Modulares robustos.

#### `ApplicationModules`
Clase core de Spring Modulith usada en los Tests para escanear tus paquetes y verificar que ningún módulo esté violando la privacidad de otro módulo.

#### `Eventos (ApplicationEvent)`
El mecanismo recomendado para que dos módulos se comuniquen sin acoplarse. Ventas publica un evento `OrderPlacedEvent` a la memoria. Inventario está suscrito y lo recibe, reduciendo el stock.

---

### Conceptos

#### 1. Estructura de Paquetes Strict (Por Dominio)
- **Qué es** — En Modulith, los paquetes de primer nivel definen los "Módulos". Todo lo que está *directamente* en ese paquete es público para otros módulos. Todo lo que está en sub-paquetes es PRIVADO.
- **Código** — Estructura obligatoria:
  ```text
  com.empresa.app
  ├── orders/                      # Módulo 'Orders'
  │   ├── OrderFacade.java         # PÚBLICO (Otros módulos pueden inyectarlo)
  │   ├── OrderPlacedEvent.java    # PÚBLICO (El evento compartido)
  │   └── internal/                # PRIVADO (Restringido por Modulith)
  │       ├── Order.java           # Entidad JPA
  │       └── OrderRepository.java # Repositorio
  ├── inventory/                   # Módulo 'Inventory'
  │   ├── InventoryFacade.java     # PÚBLICO
  │   └── internal/                # PRIVADO
  │       ├── Product.java
  │       └── InventoryRepository.java
  ```

#### 2. Validación de Arquitectura (Los Test de Modulith)
- **Qué es** — Un programador novato podría intentar inyectar `OrderRepository` dentro de `InventoryFacade.java`. ¡Esto rompe el aislamiento! Spring Modulith te permite crear un Test Unitario que falla si alguien rompe las reglas.
- **Código** — Archivo `ArchitectureTest.java`:
  ```xml
  <!-- En pom.xml -->
  <dependency>
      <groupId>org.springframework.modulith</groupId>
      <artifactId>spring-modulith-starter-test</artifactId>
      <scope>test</scope>
  </dependency>
  ```
  ```java
  class ArchitectureTest {
      
      // Analiza todo el proyecto a partir del paquete raíz
      ApplicationModules modules = ApplicationModules.of(MainApplication.class);
  
      @Test
      void verifyModularStructure() {
          // Esto fallará (lanzará excepción) si el módulo de Inventario 
          // importa una clase del sub-paquete 'internal' de Órdenes
          modules.verify();
      }
      
      @Test
      void createModuleDocumentation() {
          // Magia: Genera diagramas UML PlantUML y Canvas de arquitectura automáticamente!
          new Documenter(modules).writeModulesAsPlantUml();
      }
  }
  ```

#### 3. Comunicación por Eventos (Desacoplamiento Extremo)
- **Qué es** — En vez de que `OrderFacade` llame a `InventoryFacade.reducirStock()`, usa los eventos nativos de Spring (Memory Pub/Sub) para no saber siquiera que el Inventario existe.
- **Código**:
  
  **El Módulo Productor (Orders):**
  ```java
  package com.empresa.app.orders.internal;
  // ... imports ...
  
  @Service
  public class OrderService {
  
      private final ApplicationEventPublisher events; // Nativo de Spring
  
      public void createOrder(Order order) {
          repository.save(order);
          // Publicamos el evento al aire (memoria)
          events.publishEvent(new OrderPlacedEvent(order.getId(), order.getProductId()));
      }
  }
  ```
  
  **El Módulo Consumidor (Inventory):**
  ```java
  package com.empresa.app.inventory.internal;
  // ... imports ...
  
  @Service
  public class InventoryService {
  
      // Se suscribe al evento.
      // @ApplicationModuleListener es de Modulith. Es un alias para @Async + @TransactionalEventListener.
      // Asegura que el evento se procese asíncronamente y SOLO SI la transacción de la orden fue exitosa.
      @ApplicationModuleListener
      public void on(OrderPlacedEvent event) {
          log.info("Reduciendo stock para el producto: {}", event.productId());
          // lógica de BD...
      }
  }
  ```

#### 4. Documentación Auto-Generada
Al correr el test `Documenter(modules).writeModulesAsPlantUml()`, Spring Modulith analiza tu código y genera archivos `.puml` en `target/spring-modulith-docs/`. Estos diagramas muestran exactamente qué módulo se comunica con cuál y a través de qué eventos, manteniendo tu documentación arquitectónica 100% sincronizada con tu código real (Code as Architecture).

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Inyecciones circulares (Ciclos) | Ventas llama la API de Inventario, y la API de Inventario llama a la API de Ventas | Modulith bloquea ciclos por defecto (Lanzará error en el test `verify()`). Resuelve el ciclo usando Eventos asíncronos en lugar de inyecciones de interfaz directas. |
| Evento perdido si la app se apaga | Usaste `events.publishEvent()`, pero el servidor se reinició antes de que el Inventario terminara su hilo asíncrono | Usar Event Publication Registry (Spring Modulith JDBC). Guarda el evento en la BD temporalmente y marca si se completó. Si la app cae, al reiniciar vuelve a emitir los eventos incompletos (Transactional Outbox Pattern). |
| ¿Pueden compartir tablas de BD? | Modulith no obliga a separar bases de datos, pero deberías separar tablas | Un Monolito Modular debe tener "límites de tabla". Ventas tiene su tabla, Inventario la suya. No hagas JOINs cruzados en JPQL; pide los datos por la Facade (API) del otro módulo. |

---

### Ejercicios
1. Crea un proyecto con `spring-modulith-starter-core` y `spring-modulith-starter-test`.
2. Crea una estructura de paquetes: `com.app.ventas` y `com.app.marketing`. Dentro de cada uno crea un paquete `internal`.
3. Crea una clase en `ventas/internal/VentasLogica.java`.
4. En `marketing/internal/MarketingService.java`, intenta inyectar/importar la clase `VentasLogica` del paso anterior.
5. Crea el test unitario `ApplicationModules.of(App.class).verify()`. Ejecútalo en Maven y observa cómo el test FALLA (y te regaña) por violar la encapsulación, obligándote a crear una API pública en la raíz del módulo de ventas.

### Cómo ejecutar
```bash
cd 39-monolito-modular
mvn test # Corre el test de arquitectura que valida que los muros de Modulith no estén rotos
mvn spring-boot:run
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Dependencias core de Modulith. |
| `src/test/.../ModulithTest.java` | Las pruebas obligatorias que validan la arquitectura aisalda de los paquetes. |
| `orders/OrderPlacedEvent.java` | Evento público usado para comunicarse sin acoplamiento. |
| `orders/internal/OrderService.java` | Publicación de eventos. |
| `inventory/internal/InventoryService.java` | Consumo de eventos asíncronos (`@ApplicationModuleListener`). |
