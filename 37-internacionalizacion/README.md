## 37 — Internacionalización (i18n)

### Propósito
Aprender a construir aplicaciones y APIs que soporten múltiples idiomas (inglés, español, francés) utilizando el sistema de mensajes centralizado de Spring (`MessageSource`) y la resolución del Locale solicitada por el cliente mediante el header `Accept-Language`.

### Problema que resuelve
Si hardcodeas strings en tu código como `throw new RuntimeException("El usuario no existe")` o devuelves mensajes de validación como `"La contraseña es muy corta"`, tu backend solo podrá ser consumido por usuarios hispanohablantes.
Si mañana tu empresa decide lanzar la aplicación en Estados Unidos, tendrías que reescribir toda la aplicación o hacer espagueti de código con condicionales `if (idioma == "en") return "User not found"`.

### Cómo lo resuelve
La Internacionalización (abreviada i18n porque hay 18 letras entre la 'i' y la 'n' de *internationalization*) externaliza todos los mensajes legibles por humanos a archivos de propiedades especiales (`messages_en.properties`, `messages_es.properties`). Spring leerá el header `Accept-Language` que envía el navegador del usuario y seleccionará automáticamente el archivo de idioma correcto.

### Por qué aprenderlo
Cualquier producto de software global o B2B requiere soporte multi-idioma, no solo para respuestas de la API, sino para correos electrónicos (Módulo 28) y PDFs. Entender la abstracción `Locale` en Java y cómo Spring gestiona los mensajes es esencial para arquitecturas que escalan internacionalmente.

```mermaid
graph TD
    A["Cliente Frontend"] -->|"GET /api/saludo<br/>Header: Accept-Language: fr"| B["Spring Dispatcher"]
    
    B --> C["LocaleResolver"]
    C -->|"Detecta 'fr' (Francés)"| D["Controlador"]
    
    D --> E["MessageSource (Gestor de Textos)"]
    
    E -.-> F["messages.properties (Default/Inglés)"]
    E -.-> G["messages_es.properties (Español)"]
    E -->|"Selecciona este archivo"| H["messages_fr.properties (Francés)"]
    
    H -->> E: Retorna "Bonjour!"
    E -->> D: Retorna "Bonjour!"
    D -->> A: JSON { "mensaje": "Bonjour!" }

    style C fill:#339af0,color:#fff
    style H fill:#51cf66,color:#fff
```

---

### Glosario Básico

#### `i18n`
Abreviatura estándar de la industria para Internacionalización.

#### `Locale`
Una clase core de Java (`java.util.Locale`) que representa una región geográfica, política o cultural específica. (Ej: `Locale.US`, `Locale.FRENCH`, o un `new Locale("es", "AR")` para Español de Argentina).

#### `MessageSource`
La interfaz de Spring encargada de resolver mensajes (strings) pasándole una clave (Key) y un Locale.

#### `LocaleResolver`
El componente de Spring MVC que decide cómo averiguar el idioma del usuario. Puede hacerlo por Cookies, Parámetros en la URL (`?lang=es`) o por el header HTTP estándar (`Accept-Language`).

---

### Conceptos

#### 1. Configurando los Archivos de Mensajes (Resource Bundles)
- **Qué es** — En lugar de escribir texto en las clases Java, defines "Llaves" (Keys) en archivos de texto dentro de la carpeta `src/main/resources`.
- **Código** — Estructura de archivos:
  
  **`messages.properties`** (Archivo por defecto. Suele ser en Inglés por convención global).
  ```properties
  greeting.hello=Hello User!
  user.not.found=The user with ID {0} does not exist.
  ```

  **`messages_es.properties`** (Para español en general)
  ```properties
  greeting.hello=¡Hola Usuario!
  user.not.found=El usuario con ID {0} no existe.
  ```

  **`messages_es_AR.properties`** (Opcional, para dialectos específicos como Español de Argentina)
  ```properties
  greeting.hello=¡Che, hola Usuario!
  ```

#### 2. Configuración en Spring Boot
- **Qué es** — Por defecto, Spring Boot ya busca archivos que se llamen `messages`, pero necesitamos configurar el `LocaleResolver` para que lea el header HTTP de las peticiones REST.
- **Código**:
  ```java
  @Configuration
  public class I18nConfig {
  
      // Usamos el AcceptHeaderLocaleResolver, ideal para APIs REST Stateless
      @Bean
      public LocaleResolver localeResolver() {
          AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
          resolver.setDefaultLocale(Locale.US); // Idioma si el cliente no envía el header
          return resolver;
      }
      
      // Opcional: Para cambiar el "basename" si en lugar de messages.properties
      // quieres llamarlos i18n/textos.properties
      @Bean
      public MessageSource messageSource() {
          ResourceBundleMessageSource source = new ResourceBundleMessageSource();
          source.setBasename("messages");
          source.setDefaultEncoding("UTF-8"); // Evita caracteres raros en acentos y ñ
          return source;
      }
  }
  ```

#### 3. Uso en el Código Java (Respuestas y Excepciones)
- **Qué es** — Inyectar el `MessageSource` para traducir dinámicamente un string antes de devolverlo en un JSON o una Excepción.
- **Código**:
  ```java
  @RestController
  @RequestMapping("/api/greetings")
  public class GreetingController {
  
      private final MessageSource messageSource;
  
      public GreetingController(MessageSource messageSource) {
          this.messageSource = messageSource;
      }
  
      @GetMapping
      public String sayHello(@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
          // El método getMessage requiere la Llave, un arreglo de parámetros (si hay {0}), y el Locale actual
          return messageSource.getMessage("greeting.hello", null, locale);
      }
      
      // Forma MÁS MODERNA (Sin pedir el Locale como argumento en cada método)
      @GetMapping("/advanced")
      public String sayHelloAdvanced() {
          // LocaleContextHolder extrae el Locale del hilo de la petición actual (gracias al LocaleResolver)
          Locale locale = LocaleContextHolder.getLocale();
          return messageSource.getMessage("greeting.hello", null, locale);
      }
  }
  
  // Uso en Servicios (Lanzando excepciones traducidas)
  @Service
  public class UserService {
      
      private final MessageSource messageSource;
      
      public void findUser(Long id) {
          // ... si no existe:
          String errorMessage = messageSource.getMessage(
              "user.not.found", 
              new Object[]{id}, // Reemplaza el {0} en el archivo properties
              LocaleContextHolder.getLocale()
          );
          throw new ResourceNotFoundException(errorMessage);
      }
  }
  ```

#### 4. Validaciones Multi-idioma (Bean Validation)
- **Qué es** — Si usas `@NotBlank` o `@Size` (Módulo 10), puedes internacionalizar los mensajes de error (que por defecto salen en inglés o en el idioma del servidor).
- **Código**:
  En el DTO pones la "Llave" entre llaves `{}`:
  ```java
  public record UserRequest(
      @NotBlank(message = "{user.name.required}")
      String name,
      
      @Email(message = "{user.email.invalid}")
      String email
  ) {}
  ```
  Y en el `messages_es.properties` agregas:
  ```properties
  user.name.required=El nombre es un campo obligatorio.
  user.email.invalid=El formato del correo electrónico no es válido.
  ```
  *Magia:* Spring Validation leerá esto automáticamente usando tu configuración de i18n, sin que debas inyectar `MessageSource`.

#### 5. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Los acentos salen como `?` o caracteres extraños | El archivo `.properties` fue guardado en formato ISO-8859-1 (por defecto en Java 8-) o falta configurar el encoding en Spring. | Asegurarse de poner `spring.messages.encoding=UTF-8` en el `application.yml` y que el IDE (IntelliJ/Eclipse) guarde los archivos explícitamente en UTF-8. |
| Fallo en Correos Asíncronos (`@Async`) | Llamar a `LocaleContextHolder.getLocale()` dentro de un método asíncrono para enviar un correo en otro idioma. | El `Locale` vive en el ThreadLocal de Tomcat. Al saltar a un hilo de background (`@Async`), se pierde y se pone el default. Debes pasar el Locale como parámetro al método asíncrono. |
| Archivos no encontrados | Escribir los properties como `message_es` (sin la 's' final). | El nombre base predeterminado es `messages`. Por lo tanto debe ser `messages_es.properties`. |

---

### Ejercicios
1. Crea los archivos `messages.properties`, `messages_es.properties` y `messages_fr.properties` en `src/main/resources`.
2. Agrega la propiedad `app.welcome` y ponle "Welcome", "Bienvenido" y "Bienvenue" respectivamente.
3. Crea un Controlador REST con el endpoint `/api/welcome`. Usa `LocaleContextHolder.getLocale()` y el `MessageSource` para devolver este string.
4. Usa Postman o cURL para probar el endpoint:
   - `curl -H "Accept-Language: es" http://localhost:8080/api/welcome`
   - `curl -H "Accept-Language: fr" http://localhost:8080/api/welcome`
   - `curl http://localhost:8080/api/welcome` (Debe salir el default).

### Cómo ejecutar
```bash
cd 37-internacionalizacion
mvn spring-boot:run

# Probar la internacionalización por Headers
curl -H "Accept-Language: es-ES" http://localhost:8080/api/greetings
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `config/I18nConfig.java` | Configuración del `AcceptHeaderLocaleResolver`. |
| `resources/messages.properties` | Archivos Resource Bundle con las llaves de traducción (Inglés). |
| `resources/messages_es.properties` | Traducciones al Español. |
| `controller/GreetingController.java` | Extracción de traducciones vía `MessageSource` y `LocaleContextHolder`. |
| `dto/UserRequest.java` | Uso de llaves `{llave}` en anotaciones de validación (Bean Validation). |
