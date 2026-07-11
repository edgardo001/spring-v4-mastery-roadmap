## 42 — Domain-Driven Design (DDD) Táctico en Spring Boot

### Propósito
Aprender a implementar los patrones tácticos de Domain-Driven Design (Entidades, Value Objects, Agregados, Repositorios y Servicios de Dominio) en una aplicación Spring Boot. Este módulo complementa la Arquitectura Hexagonal (Módulo 38) enfocándose puramente en cómo se modela el código del corazón del negocio.

### Problema que resuelve
El "Modelo de Dominio Anémico".
En el 90% de los proyectos Spring, las clases del modelo (`@Entity`) son solo bolsas de datos con `Getters` y `Setters`. La lógica de negocio (validaciones, cálculos) está toda tirada en un gigantesco `OrderService.java` de 3000 líneas.
- Si quieres calcular el total de una orden, tienes que buscar en el Service.
- Cualquier programador novato puede hacer `order.setTotal(-500)` y corromper el estado, porque no hay encapsulamiento real.
- El código no habla el lenguaje del negocio (Ubiquitous Language); habla lenguaje de base de datos ("Set", "Get", "Update").

### Cómo lo resuelve
En DDD táctico, los objetos son ricos y **protegen sus propias invariantes**.
- En lugar de usar `Double` para el dinero, creas un **Value Object** llamado `Money` que no permite valores negativos.
- En lugar de `OrderService`, la clase `Order` (El **Agregado**) tiene un método `order.addItem(product, quantity)` que ella misma valida y recalcula su propio total.
- Los "Setters" públicos están **prohibidos**. El estado solo muta a través de métodos con intención de negocio (`approve()`, `cancel()`, `ship()`).

### Por qué aprenderlo
DDD es el estándar de oro para modelar lógicas de negocio complejas en sistemas Core. Aplicarlo junto a Spring Data JPA (venciendo las limitaciones del framework) es una habilidad técnica de nivel Arquitecto.

```mermaid
graph TD
    subgraph DDD Táctico (Dentro del Hexágono)
        
        subgraph Aggregate [Agregado: Order]
            A[Order (Root Entity)]
            B[OrderItem (Entity)]
            C[Money (Value Object)]
            D[Address (Value Object)]
            
            A --> B
            A --> C
            A --> D
        end
        
        E((Application Service))
        F((Domain Service))
        G[(Repository Interface)]
        
        E -->|Orquesta| A
        E -.->|Inyecta| F
        E -->|Guarda| G
    end

    style Aggregate fill:#e6fcf5,stroke:#20c997,stroke-width:2px,color:#000
    style C fill:#fff3bf,color:#000
    style D fill:#fff3bf,color:#000
```

---

### Glosario Básico

#### `Ubiquitous Language` (Lenguaje Ubicuo)
El glosario compartido entre los desarrolladores y los expertos del negocio. Si el negocio dice "El cliente *Inactiva* su cuenta", el código no debe tener un método `user.setActivo(false)`, debe tener un método `user.deactivate()`.

#### `Value Object` (Objeto de Valor)
Un objeto que representa un valor descriptivo y no tiene identidad (ID). Ej: `Color`, `Money`, `Address`. Si dos billetes de $100 tienen las mismas propiedades, valen lo mismo. Son 100% **inmutables**.

#### `Entity` (Entidad de Dominio)
Un objeto con identidad (ID) que persiste en el tiempo. Sus propiedades pueden cambiar, pero sigue siendo la misma entidad (Una persona puede cambiar de nombre, pero sigue siendo la misma persona).

#### `Aggregate Root` (Raíz de Agregado)
La Entidad "Padre" que controla todo un grupo de objetos relacionados. Por ejemplo, `Order` (Raíz) controla una lista de `OrderItem`s. Las reglas de DDD dictan que desde fuera, nadie puede tocar un `OrderItem` directamente; todos deben pasar por `Order`. El Repositorio solo guarda y lee el *Aggregate Root*.

---

### Conceptos

#### 1. Implementando Value Objects
- **Qué es** — En Java moderno, un Value Object es simplemente un `Record` o una clase final sin setters. Protege sus propias reglas de negocio en el constructor.
- **Código**:
  ```java
  public record Money(BigDecimal amount, String currency) {
      public Money {
          if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
              throw new IllegalArgumentException("El dinero no puede ser negativo");
          }
          if (currency == null || currency.isBlank()) {
              throw new IllegalArgumentException("La moneda es requerida");
          }
      }
  
      // Los Value Objects son inmutables. Sumar devuelve un NUEVO objeto.
      public Money add(Money other) {
          if (!this.currency.equals(other.currency)) {
              throw new IllegalArgumentException("No se pueden sumar monedas distintas");
          }
          return new Money(this.amount.add(other.amount), this.currency);
      }
  }
  ```

#### 2. Modelando la Entidad Raíz (Aggregate Root)
- **Qué es** — Una clase de dominio pura que contiene lógica rica. No tiene setters.
- **Código**:
  ```java
  public class Order {
      private Long id; // Identidad
      private OrderStatus status;
      private List<OrderItem> items;
      private Money totalAmount; // Value Object
  
      // Solo el constructor puede crear el estado inicial válido
      public Order() {
          this.status = OrderStatus.DRAFT;
          this.items = new ArrayList<>();
          this.totalAmount = new Money(BigDecimal.ZERO, "USD");
      }
  
      // LÓGICA DE NEGOCIO (El dominio NO es anémico)
      public void addItem(Product product, int quantity) {
          if (this.status != OrderStatus.DRAFT) {
              throw new IllegalStateException("Solo se pueden agregar items a órdenes en borrador");
          }
          
          Money itemCost = product.getPrice().multiply(quantity);
          this.items.add(new OrderItem(product.getId(), quantity, itemCost));
          
          // El agregado se auto-calcula
          this.totalAmount = this.totalAmount.add(itemCost);
      }
      
      public void confirm() {
          if (this.items.isEmpty()) throw new IllegalStateException("No se puede confirmar una orden vacía");
          this.status = OrderStatus.CONFIRMED;
      }
  }
  ```

#### 3. El Conflicto con JPA/Hibernate
- **Qué es** — DDD dice: "La Entidad de Dominio no debe tener tecnología". Pero Spring Data exige poner `@Entity` y un constructor vacío público en las clases para mapearlas a tablas.
- **La Solución Arquitectónica** — Tienes 2 opciones:
  1. **Enfoque Purista (Hexagonal Completo):** Tienes tu clase de dominio `Order` sin anotaciones, y una clase paralela `OrderJpaEntity` con anotaciones. Un Mapper traduce entre ambas (Módulo 38). (Muy limpio, mucho trabajo).
  2. **Enfoque Pragmático (DDD Lite con Spring Data):** Pones las anotaciones `@Entity` directamente en tu Agregado, pero escondes el constructor vacío usándolo como `protected` y sigues prohibiendo los Setters.
- **Código Pragmático**:
  ```java
  @Entity
  @Table(name = "orders")
  public class Order {
      
      @Id @GeneratedValue
      private Long id;
      
      @Enumerated(EnumType.STRING)
      private OrderStatus status;
      
      // Mapeando un Value Object de Java a Columnas de BD usando @Embedded
      @Embedded
      @AttributeOverrides({
          @AttributeOverride(name="amount", column=@Column(name="total_amount")),
          @AttributeOverride(name="currency", column=@Column(name="currency"))
      })
      private Money totalAmount;
  
      // Constructor protegido exigido por Hibernate. El negocio NO lo puede usar.
      protected Order() {} 
      
      // ... Resto de la lógica rica (addItem, confirm, etc).
  }
  ```

#### 4. Domain Services vs Application Services
- **Qué es** — 
  - **Application Service:** Orquesta (Transacción de BD, Log, Seguridad, Llama al Agregado). Es el viejo y conocido `@Service`.
  - **Domain Service:** Una clase que encapsula una regla de negocio que involucra a 2 Agregados y no pertenece a ninguno. (Ej: `TransferFundsDomainService(Account a, Account b)`). Reside en el Dominio.

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Setter injection o Reflection Data Binding | Usar tu clase de Dominio (`Order`) directamente en el `@RequestBody` del Controller. Spring/Jackson intentará usar setters o constructores vacíos para inyectar el JSON, saltándose tus validaciones ricas. | **NUNCA expongas tu dominio al exterior.** Usa siempre un DTO tonto para el Controller (`CreateOrderRequest`), valida el DTO, y luego que el Servicio llame a `order.addItem(...)`. |
| Agregados Gigantescos | Quieres asegurar que no se vendan productos sin stock. Haces que `Order` cargue TODOS los productos de la base de datos en una lista para validarlo localmente. ¡OutOfMemoryError! | Los Agregados deben ser **pequeños**. `Order` no debe contener la entidad `Product` entera, solo su `productId` (Referencia por ID). Usa un Domain Service para validar el stock consultando al `InventoryRepository` en tiempo real. |
| Inyectar Repositorios en la Entidad | Haces un `@Autowired UserRepository` dentro de la clase `Order`. | **Anti-patrón masivo.** Las entidades se crean con `new` y no son manejadas por el Contexto de Spring (IoC). Si la entidad necesita un dato externo para tomar una decisión, pásaselo por parámetro: `order.confirm(pricingService.calculateTaxes())`. |

---

### Ejercicios
1. Crea un Value Object `Password` (un `Record` inmutable). En su constructor, exige que tenga mínimo 8 caracteres y una mayúscula.
2. Crea el Agregado `User` (Entidad pragmática con JPA). Ponle un `protected User() {}` y prohíbe los setters.
3. El `User` debe tener el Value Object `Password` como `@Embedded` (o usando un `@Converter` JPA).
4. Agrega un método de negocio: `user.changePassword(Password oldPass, Password newPass)`. Valida internamente.
5. Intenta guardar el `User` usando un simple `JpaRepository` en un Test. Disfruta tener un dominio rico y totalmente asegurado por compilación.

### Antes vs Ahora (Java 8 → Java 21) aplicado a DDD

| Concepto | ANTES (Java 8 — modelo anémico) | AHORA (Java 21 — Aggregate rico) |
|---|---|---|
| Value Object | `public class Money { private double amount; setAmount(); getAmount(); }` | `public record Money(BigDecimal amount, String currency) { public Money { validar... } }` |
| Cambio de estado | `order.setStatus("APPROVED"); order.setApprovedAt(new Date());` | `order.approve();` (encapsula validación + evento) |
| ID de dominio | `String orderId;` | `public record OrderId(String value) { }` |
| Domain Event | `class OrderApprovedEvent { getters/setters... }` | `record OrderApprovedEvent(OrderId id, Instant occurredAt)` |
| Suma segura | `order.total += item.price;` (mutable, `double`) | `total = total.add(item.subtotal());` (inmutable, `BigDecimal`) |
| Setter público | `order.setTotal(-500);` — corrompe el estado | No existe. Solo constructor validado + métodos de negocio. |

### FAQ del Alumno

- **¿Qué es un Aggregate Root?** Es la entidad "puerta de entrada" a un grupo de objetos que deben ser consistentes juntos. En este módulo, `Order` es el root; `OrderItem` solo se toca a través de `Order`. Nadie modifica un `OrderItem` desde fuera.
- **¿Por qué el constructor de `Order` es `protected`?** JPA necesita un constructor sin argumentos para reconstruir la entidad al leerla desde la BD. Lo marcamos `protected` para que el código cliente no pueda usarlo — así garantizamos que las órdenes se creen SIEMPRE con el constructor validado.
- **¿Qué es un Value Object?** Un objeto sin identidad propia que se compara por valor. Dos `Money(100, "USD")` son iguales aunque sean instancias distintas. Es inmutable, y sus invariantes se protegen en el constructor.
- **¿Por qué usar `BigDecimal` y no `double` para el dinero?** `double` sufre errores de redondeo (0.1 + 0.2 no es exactamente 0.3). En dominios financieros se usa `BigDecimal` para evitar bugs de centavos.
- **¿Qué es un Domain Event?** Un hecho relevante que YA pasó en el dominio ("OrderApproved"). Es inmutable, se nombra en pasado, y sirve para que otros componentes reaccionen (enviar email, generar factura...).
- **¿Por qué el `record` no puede ser `@Entity`?** JPA (Hibernate) requiere setters/mutabilidad y un constructor sin args; los `record` son inmutables. Por eso usamos `record` para Value Objects (`Money`, `OrderId`, `OrderApprovedEvent`) pero `class` con constructor `protected` para el Aggregate `Order`.
- **¿Qué es `@Embeddable`?** Le dice a JPA que la clase NO tiene su propia tabla; sus campos se guardan dentro de la tabla de la entidad que la contiene (o en una tabla auxiliar si es `@ElementCollection`).
- **¿Por qué `getItems()` devuelve una lista inmutable?** Porque si devolvieras la lista interna, el cliente podría hacer `order.getItems().add(...)` y saltarse las validaciones del aggregate. Devolver `Collections.unmodifiableList` bloquea eso.
- **¿Qué es "modelo anémico"?** Un modelo donde las entidades son solo bolsas de datos (getters/setters) y toda la lógica vive en el service. Es el anti-patrón que DDD combate.
- **¿Qué hace `Objects.requireNonNull`?** Lanza `NullPointerException` con un mensaje descriptivo si el argumento es `null`. Es la forma idiomática de validar que un parámetro no sea nulo.

### Ejercicios adicionales
- Añade un método `Order.cancel()` que solo permita cancelar si el estado es `PENDING`. Registra un `OrderCancelledEvent`.
- Crea un Value Object `Address(String street, String city, String zip)` y agrégalo como `@Embedded` en `Order`.
- Usa `ApplicationEventPublisher` de Spring para publicar realmente los Domain Events (en vez de imprimirlos en consola).

### Cómo ejecutar
```bash
# Compilar y empaquetar (JDK 21 + Maven portables desde la raíz del roadmap):
cd 42-ddd
./build.sh          # Git Bash
# o
./build.ps1         # PowerShell

# Ejecutar el JAR generado:
java -jar target/ddd-1.0.0.jar

# O bien, en modo desarrollo:
../apache-maven-3.9.16/bin/mvn spring-boot:run
```

Ejemplos de request:
```bash
# Crear orden:
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customer":"Juan","items":[{"productName":"Pizza","quantity":2,"unitPrice":10.00,"currency":"USD"}]}'

# Aprobar orden:
curl -X PUT http://localhost:8080/api/orders/{id}/approve
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Coordenadas Maven (Spring Boot 4.1.0, Java 21, artifactId=ddd). |
| `build.sh` / `build.ps1` | Scripts portables que usan JDK y Maven de la raíz del roadmap. |
| `src/main/resources/application.yml` | Configuración H2 en memoria y JPA. |
| `DddApplication.java` | Punto de entrada Spring Boot. |
| `order/domain/OrderId.java` | Value Object (record) — identidad de la orden. |
| `order/domain/Money.java` | Value Object (record) — cantidad + moneda con invariantes. |
| `order/domain/OrderStatus.java` | Enum de estados (PENDING/APPROVED/CANCELLED). |
| `order/domain/OrderItem.java` | Value Object (@Embeddable) — línea de la orden. |
| `order/domain/Order.java` | Aggregate Root (@Entity) con `approve()` y Domain Events. |
| `order/domain/OrderApprovedEvent.java` | Domain Event (record) publicado al aprobar. |
| `order/application/OrderService.java` | Application Service — orquesta creación y aprobación. |
| `order/infrastructure/OrderRepository.java` | JpaRepository del aggregate. |
| `order/infrastructure/OrderController.java` | REST adapter (POST /api/orders, PUT /{id}/approve). |
| `src/test/.../DddApplicationTests.java` | `contextLoads` — arranque del contexto. |
| `src/test/.../OrderTest.java` | Tests unitarios puros del aggregate (approve, doble approve). |
| `src/test/.../MoneyTest.java` | Tests unitarios puros del Value Object Money. |
| `src/test/.../OrderControllerTest.java` | MockMvc standalone con service mockeado. |
