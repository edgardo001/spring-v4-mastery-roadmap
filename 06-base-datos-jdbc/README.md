## 06 — Base de Datos con JDBC (DataSource, JdbcTemplate, H2 y @Transactional)

### Propósito
Aprenderás cómo Spring Boot se conecta a una base de datos relacional (H2 en memoria) mediante un `DataSource`, cómo ejecutar operaciones CRUD limpias y seguras usando `JdbcTemplate`, y cómo garantizar la integridad de los datos utilizando transacciones gestionadas con la anotación `@Transactional`.

### Problema que resuelve
El código tradicional de JDBC en Java (usando `Connection`, `PreparedStatement`, y `ResultSet`) es extremadamente verboso, propenso a errores humanos (olvidar cerrar una conexión o un statement, causando memory leaks) y te obliga a lidiar con las excepciones comprobadas (`SQLException`) llenando tu código de bloques `try-catch-finally`. Además, gestionar que varias operaciones se completen todas juntas o ninguna (transacciones) requiere controlar el commit y rollback manualmente, lo que ensucia la lógica de negocio.

### Cómo lo resuelve
Spring abstrae toda la complejidad y el código repetitivo de JDBC a través de la clase `JdbcTemplate`, que se encarga automáticamente de abrir y cerrar conexiones, preparar las sentencias y mapear resultados. Traduce las horribles `SQLException` en una jerarquía coherente de excepciones de Spring (`DataAccessException`), que además son `RuntimeException`, limpiando tu código de bloques try-catch innecesarios. Finalmente, la anotación `@Transactional` implementa un proxy alrededor de tus servicios para iniciar, comitear o deshacer transacciones automáticamente.

### Por qué aprenderlo
Interactuar con bases de datos relacionales es una de las tareas más críticas y comunes en cualquier aplicación empresarial (banca, e-commerce, logística). Aunque hoy en día ORMs como Hibernate (JPA) son muy populares, muchas empresas prefieren la velocidad pura y el control directo sobre las consultas SQL complejas o reportes masivos usando `JdbcTemplate`. Comprender este nivel más cercano a la base de datos es fundamental antes de pasar a abstracciones más altas, y entender cómo funcionan las transacciones (`@Transactional`) te salvará de corromper la base de datos en producción.

```mermaid
graph TD
    A["Aplicación Spring Boot"] -->|Inyecta| B["JdbcTemplate"]
    B -->|Usa| C["DataSource (HikariCP)"]
    C -->|Pool de Conexiones| D[("Base de Datos (H2, PostgreSQL, etc.)")]
    
    E["Servicio (@Transactional)"] -.->|Inicia/Comitea Transacción| B
    
    style A fill:#4CAF50,stroke:#388E3C,stroke-width:2px,color:#fff
    style B fill:#2196F3,stroke:#1976D2,stroke-width:2px,color:#fff
    style C fill:#FFC107,stroke:#FFA000,stroke-width:2px,color:#000
    style D fill:#9C27B0,stroke:#7B1FA2,stroke-width:2px,color:#fff
    style E fill:#F44336,stroke:#D32F2F,stroke-width:2px,color:#fff
```

### Glosario Básico
- **`DataSource`**: Interfaz estándar de Java para obtener conexiones a una base de datos. Spring Boot por defecto usa HikariCP, un pool de conexiones extremadamente rápido y eficiente.
- **`JdbcTemplate`**: La clase principal de Spring para ejecutar sentencias SQL. Elimina todo el "boilerplate" (código repetitivo) de abrir y cerrar conexiones, lidiar con PreparedStatements, etc.
- **`RowMapper<T>`**: Interfaz de Spring utilizada por `JdbcTemplate` para mapear las filas (`ResultSet`) de una consulta SQL a objetos Java.
- **`@Transactional`**: Anotación de Spring que indica que un método o clase debe ejecutarse dentro de una transacción de base de datos (todo o nada).
- **H2 Database**: Una base de datos relacional escrita en Java. Se puede usar en modo "in-memory" (los datos se pierden al apagar la app), siendo ideal para pruebas, desarrollo rápido o ejemplos de aprendizaje.

### Conceptos

#### 1. DataSource (Pool de Conexiones HikariCP)

**Qué es:**
Un `DataSource` es una fábrica de conexiones a la base de datos. En vez de abrir una conexión lenta y costosa cada vez que se requiere una consulta (que podría tardar decenas de milisegundos), Spring Boot (mediante HikariCP) abre de antemano un conjunto de conexiones (pool) al arrancar. Cuando `JdbcTemplate` necesita una, simplemente la "toma prestada" del pool, ejecuta la consulta y la "devuelve". 

**Por qué importa:**
Abrir conexiones de base de datos físicamente (TCP handshakes, autenticación) es letalmente lento en un entorno web de alto tráfico. Usar un pool de conexiones como HikariCP es obligatorio para la escalabilidad, y Spring Boot lo autoconfigura por ti con solo añadir la dependencia `spring-boot-starter-jdbc` y las propiedades en `application.yml`.

**Código (Configuración de application.yml):**
```yaml
spring:
  datasource:
    # URL para base de datos H2 en memoria. 
    # mem: testdb significa que se llama testdb y se pierde al apagar la app.
    url: jdbc:h2:mem:testdb
    # Usuario por defecto de H2
    username: sa
    # Contraseña por defecto (vacía)
    password: 
    # Configuración opcional para tuning del pool HikariCP (Edge cases de alto tráfico)
    hikari:
      maximum-pool-size: 10 # Número máximo de conexiones simultáneas
      connection-timeout: 20000 # 20s. Tiempo máximo que un hilo espera por una conexión
  h2:
    console:
      # Habilita la consola web de H2 en http://localhost:8080/h2-console
      enabled: true 
      path: /h2-console
```

**Analogía:**
Imagina una empresa de taxis. Si cada vez que alguien llama pidiendo un viaje, la empresa tuviera que comprar un auto, registrarlo, contratar a un conductor (abrir la conexión real), tardarían horas en llegar. En cambio, tienen un estacionamiento con 10 taxis ya listos con conductor (el Connection Pool). Si llamas, te mandan uno al instante. Cuando tu viaje termina, el taxi vuelve al estacionamiento para el siguiente cliente.

**Casos de Uso Empresariales:**
En cualquier microservicio transaccional. Por ejemplo, en Netflix, cada servicio que necesita persistencia utiliza un pool de conexiones optimizado según el volumen de carga que reciba. Ajustar el `maximum-pool-size` previene caídas de base de datos bajo ataques de peticiones (DDoS) o cuellos de botella.

---

#### 2. Consultas CRUD con JdbcTemplate y RowMapper

**Qué es:**
`JdbcTemplate` simplifica radicalmente las operaciones de Create, Read, Update, Delete. Permite inyectar los parámetros de forma segura (previniendo inyección SQL) e iterar los resultados mapeándolos directamente a tus Records o Clases de dominio (DTOs) usando la interfaz `RowMapper`. 

**Por qué importa:**
Aunque frameworks como Hibernate son mágicos, `JdbcTemplate` te da control total sobre la sentencia SQL. Esto es crucial cuando necesitas consultas muy complejas, reportes, o cuando el rendimiento en la inserción/búsqueda es una prioridad absoluta y el ORM añade demasiada sobrecarga.

**Código:**
```java
package com.springroadmap.jdbc.repository;

import com.springroadmap.jdbc.domain.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// @Repository indica que esta clase interactúa con la BD. 
// Traduce excepciones de BD (SQLException) a DataAccessException de Spring.
@Repository
public class UsuarioRepository {

    private static final Logger log = LoggerFactory.getLogger(UsuarioRepository.class);
    
    // JdbcTemplate es thread-safe después de configurarse.
    private final JdbcTemplate jdbcTemplate;

    // Inyección por constructor (Buena Práctica)
    public UsuarioRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // El RowMapper convierte un registro SQL (ResultSet) a un objeto Java.
    private final RowMapper<Usuario> usuarioRowMapper = (rs, rowNum) -> new Usuario(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("email")
    );

    // CREATE: Inserta un registro.
    public void guardar(final Usuario usuario) {
        // Uso de parámetros (?) para prevenir INYECCIÓN SQL. 
        // ¡NUNCA concatenar Strings en la query!
        final String sql = "INSERT INTO usuarios (nombre, email) VALUES (?, ?)";
        int filasAfectadas = jdbcTemplate.update(sql, usuario.nombre(), usuario.email());
        log.info("Usuario guardado. Filas afectadas: {}", filasAfectadas);
    }

    // READ: Busca por ID. Edge Case: ¿Qué pasa si el ID no existe?
    public Optional<Usuario> buscarPorId(final Long id) {
        final String sql = "SELECT id, nombre, email FROM usuarios WHERE id = ?";
        try {
            // queryForObject espera EXACTAMENTE un resultado. 
            // Si devuelve 0 o más de 1, lanza excepción.
            Usuario usuario = jdbcTemplate.queryForObject(sql, usuarioRowMapper, id);
            return Optional.ofNullable(usuario);
        } catch (EmptyResultDataAccessException e) {
            // CASO DE ERROR: El registro no existe. 
            // Atrapamos la excepción de Spring y devolvemos Optional vacío.
            log.warn("Usuario con ID {} no encontrado.", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            // Manejar otras fallas (servidor de base de datos caído, error de sintaxis SQL)
            log.error("Error grave accediendo a la base de datos para buscar usuario con ID: {}", id, e);
            throw new RuntimeException("Error interno del servidor", e);
        }
    }

    // READ ALL: Busca todos
    public List<Usuario> buscarTodos() {
        final String sql = "SELECT id, nombre, email FROM usuarios";
        // query() maneja cualquier cantidad de resultados, devolviendo una lista.
        return jdbcTemplate.query(sql, usuarioRowMapper);
    }
    
    // UPDATE
    public boolean actualizarEmail(final Long id, final String nuevoEmail) {
        final String sql = "UPDATE usuarios SET email = ? WHERE id = ?";
        int filasAfectadas = jdbcTemplate.update(sql, nuevoEmail, id);
        return filasAfectadas > 0;
    }
}
```

**Analogía:**
Piensa en el `JdbcTemplate` como un traductor y gestor de recados al mismo tiempo. Tú (el desarrollador) le das una orden precisa y segura (SQL parametrizado) y los datos a rellenar; él se encarga de ir a la bóveda (Base de Datos), pedir prestada una llave al guardia (Connection de HikariCP), ejecutar la acción de forma impecable y traerte de regreso la información perfectamente organizada en tus carpetas (gracias a `RowMapper`).

**Casos de Uso Empresariales:**
Creación de microservicios "core" donde el performance es rey. Equipos de Data Engineering y Reporting que deben hacer procesos en lote (Batch) prefieren usar el módulo JDBC (como `JdbcBatchItemWriter` en Spring Batch) en lugar de ORMs que podrían ahogar la memoria.

---

#### 3. Transacciones con @Transactional

**Qué es:**
Una transacción es un conjunto de operaciones de base de datos que se ejecutan como una única unidad lógica. Se rige por el principio ACID (Atomicidad, Consistencia, Aislamiento, Durabilidad). La anotación `@Transactional` le dice a Spring que intercepte la llamada al método, inicie una conexión en la BD (BEGIN), ejecute todo lo contenido allí, y si todo sale bien, guarde los cambios de manera permanente (COMMIT). Si se produce un error no controlado (ej. una `RuntimeException`), Spring anulará automáticamente todos los cambios (ROLLBACK).

**Por qué importa:**
Imagina una transferencia bancaria donde a la cuenta de origen se le resta dinero, pero al sumar a la cuenta destino ocurre un error (ej. se va el internet o la app colapsa por disco lleno). Si no hay transacciones, el dinero desaparece de origen pero jamás llega a destino. Si hay transacción, como la segunda parte falló, la primera parte (restar) también se deshace (Rollback), dejando los datos íntegros tal y como estaban. No utilizar transacciones en procesos multi-paso garantiza la corrupción de datos en tu base de datos empresarial.

**Código:**
```java
package com.springroadmap.jdbc.service;

import com.springroadmap.jdbc.exception.SaldoInsuficienteException;
import com.springroadmap.jdbc.repository.CuentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferenciaService {
    
    private static final Logger log = LoggerFactory.getLogger(TransferenciaService.class);
    
    // Dependencia inyectada (repositorio que usa JdbcTemplate en el fondo)
    private final CuentaRepository cuentaRepository;
    
    public TransferenciaService(final CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    /**
     * @Transactional es la clave aquí.
     * Si cualquier RuntimeException es lanzada dentro de este método,
     * Spring realizará un ROLLBACK automático de TODOS los cambios en base de datos.
     * Si el método termina con éxito total, Spring hace un COMMIT automático.
     */
    @Transactional
    public void transferirDinero(final String cuentaOrigen, final String cuentaDestino, final double cantidad) {
        log.info("Iniciando transferencia de {} desde {} hacia {}", cantidad, cuentaOrigen, cuentaDestino);
        
        // Operación 1: Restar de la cuenta origen
        boolean restado = cuentaRepository.restarSaldo(cuentaOrigen, cantidad);
        
        if (!restado) {
            // Lanza una excepción de Runtime, ¡esto provocará un ROLLBACK automático!
            // No se toca la BD, pero es buena práctica para frenar el flujo e informarlo arriba.
            throw new SaldoInsuficienteException("La cuenta origen no tiene saldo suficiente");
        }
        
        // Simulación de un error de sistema inesperado en medio del flujo
        if (cuentaDestino.equals("CUENTA_INEXISTENTE")) {
            // Otra RuntimeException que provoca ROLLBACK automático. 
            // El saldo restado de la cuentaOrigen ¡volverá a su estado original!
            throw new RuntimeException("Error crítico: la cuenta destino no existe, provocando rollback general.");
        }
        
        // Operación 2: Sumar a la cuenta destino
        cuentaRepository.sumarSaldo(cuentaDestino, cantidad);
        
        log.info("Transferencia completada con éxito. Spring hará COMMIT automático.");
    }
}
```

**Analogía:**
Las transacciones son como el carrito de compras en internet. Tú seleccionas 10 artículos y los vas metiendo. Hasta que no ingresas tu tarjeta y das click en "Pagar" (COMMIT), nada es tuyo y no te descuentan dinero real. Si justo después de meter el artículo 5 se corta la luz (Error), no pierdes dinero y la tienda sigue conservando sus artículos (ROLLBACK). ¡Es un paquete de "todo o nada"!

**Casos de Uso Empresariales:**
Sistemas de inventario, procesos de compra y pagos, reserva de butacas para cine o avión, transferencia de archivos financieros. Absolutamente CUALQUIER proceso de negocio que altere dos o más tablas que dependen una de la otra en la base de datos DEBE ser envuelto en `@Transactional`.

### Ejercicios
1. Crea una tabla adicional `productos (id, nombre, precio, stock)`. En `src/main/resources/schema.sql` escribe el DDL y en `data.sql` inserta 3 productos.
2. Crea un `ProductoRepository` usando `JdbcTemplate`. Implementa un método `comprar(Long productoId, int cantidad)`.
3. Crea un `TiendaService` con un método `@Transactional` que:
   - Verifique si el usuario comprador existe.
   - Verifique si el producto existe y tiene stock suficiente.
   - Si no hay stock, lance una `RuntimeException` (provocando Rollback).
   - Si hay stock, reste el stock del producto en la base de datos e inserte un registro en la tabla `compras`.
4. Intenta ejecutar una compra donde el usuario no existe y verifica, accediendo a la consola de H2, que el stock del producto no se haya modificado (gracias al rollback de la transacción fallida).

### Cómo ejecutar
1. Asegúrate de tener JDK 21 y Maven instalados.
2. Abre tu terminal en el directorio `06-base-datos-jdbc`.
3. Compila y ejecuta el proyecto:
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```
4. Si dejaste H2 console activo en tu `application.yml`, entra desde tu navegador web a `http://localhost:8080/h2-console`
   - URL de JDBC: `jdbc:h2:mem:testdb`
   - Usuario: `sa`
   - Password: (vacío)
   Desde ahí podrás ver tus tablas en memoria y ejecutar sentencias SQL manualmente para comprobar tus resultados.

### Archivos del Proyecto

| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Define las dependencias del módulo, en especial `spring-boot-starter-jdbc` y el driver `h2`. |
| `src/main/resources/application.yml` | Configuración del DataSource, pool HikariCP y la consola de H2. |
| `src/main/resources/schema.sql` | Archivo auto-ejecutado por Spring al arrancar. Crea las tablas (DDL). |
| `src/main/resources/data.sql` | Archivo auto-ejecutado por Spring al arrancar. Inserta los datos de prueba iniciales (DML). |
| `com/springroadmap/jdbc/domain/Usuario.java` | Record o Clase (entidad de dominio) que representa un registro en la base de datos. |
| `com/springroadmap/jdbc/repository/UsuarioRepository.java` | Contiene la lógica de acceso a datos utilizando `JdbcTemplate` y `RowMapper`. |
| `com/springroadmap/jdbc/service/TransferenciaService.java` | Lógica de negocio transaccional decorada obligatoriamente con `@Transactional`. |
| `com/springroadmap/jdbc/controller/UsuarioController.java` | (Opcional) Expone los repositorios o servicios a través de peticiones HTTP REST. |
| `com/springroadmap/jdbc/SpringJdbcApplication.java` | La clase principal de arranque de la aplicación Spring Boot. |
