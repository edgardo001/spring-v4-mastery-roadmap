## 28 — Envío de Correos Electrónicos (Spring Boot Mail y Plantillas)

### Propósito
Aprender a integrar el envío de correos electrónicos desde tu aplicación Spring Boot utilizando el protocolo SMTP (Simple Mail Transfer Protocol), y generar cuerpos de correo dinámicos y atractivos (HTML) utilizando motores de plantillas como Thymeleaf o FreeMarker.

### Problema que resuelve
El registro de usuarios, las recuperaciones de contraseñas ("Olvidé mi contraseña"), y la confirmación de compras son interacciones esenciales que dependen del correo electrónico. 
- Enviar texto plano (`"Hola, gracias por registrarte"`) se ve muy poco profesional y no permite incluir botones, imágenes corporativas, ni enlaces formateados.
- Construir el HTML concatenando strings (`String html = "<h1>Hola " + usuario + "</h1>";`) es ilegible, frágil y una pesadilla de mantener.

### Cómo lo resuelve
Spring Boot provee `spring-boot-starter-mail`, una abstracción limpia sobre la clásica librería JavaMail. Para los correos HTML, integramos `spring-boot-starter-thymeleaf`, que nos permite diseñar los correos en archivos `.html` separados, inyectar variables (el nombre del usuario, el link de activación) en tiempo de ejecución, y renderizar el código HTML final antes de enviarlo por SMTP.

### Por qué aprenderlo
Ningún sistema empresarial está completo sin notificaciones por correo. Configurar un servidor SMTP (como SendGrid, AWS SES, o Mailgun) e integrar plantillas HTML es una tarea cotidiana para el backend. Adicionalmente, el envío de correos es lento (puede tardar 1 a 3 segundos), por lo que siempre se debe combinar con programación Asíncrona (Módulo 21).

```mermaid
graph TD
    A["Usuario (Se registra)"] --> B["UserService"]
    B --> C["Guarda Usuario en BD"]
    
    C --> D["@Async MailService"]
    D --> E["Thymeleaf Engine"]
    
    E -->|1. Lee plantilla bienvenida.html| D
    E -->|2. Inyecta {nombre: 'Edgardo'}| D
    
    D -->|3. Genera String de HTML| F["JavaMailSender"]
    F -->|4. Conecta por SMTP| G["Proveedor de Mail<br/>(SendGrid / AWS SES)"]
    G --> H["Bandeja de Entrada del Cliente"]

    style D fill:#339af0,color:#fff
    style E fill:#ffa94d,color:#fff
    style G fill:#ff6b6b,color:#fff
```

---

### Glosario Básico

#### `JavaMailSender`
La interfaz de Spring que encapsula la lógica de conexión SMTP y envío de mensajes. Se autoconfigura al poner las propiedades de tu proveedor de correo en el `application.yml`.

#### `SimpleMailMessage`
Clase sencilla para correos de texto plano (sin formato, sin HTML).

#### `MimeMessage` / `MimeMessageHelper`
Clases avanzadas que permiten enviar correos multipartes (HTML enriquecido, archivos adjuntos, imágenes en línea).

#### `Thymeleaf Context`
El "diccionario" de variables que le pasas al motor de plantillas de Thymeleaf. Spring inyecta estas variables dentro del archivo HTML (ej: en `<p th:text="${nombre}"></p>`).

---

### Conceptos

#### 1. Configuración del Servidor SMTP
- **Qué es** — Le indicas a Spring Boot a qué servidor de correo debe conectarse, con qué usuario y contraseña. Para desarrollo local, es altamante recomendable usar herramientas como **Mailtrap** o **Mailhog**, que simulan ser servidores SMTP pero atrapan los correos en una bandeja virtual sin enviarlos a la gente real.
- **Código** — Configuración en YAML:
  ```yaml
  spring:
    mail:
      # Datos de ejemplo usando Mailtrap (Para desarrollo)
      host: smtp.mailtrap.io
      port: 2525
      username: tu_usuario_mailtrap
      password: tu_password_mailtrap
      properties:
        mail:
          smtp:
            auth: true
            starttls:
              enable: true # Obligatorio para TLS/SSL en PROD
  ```

#### 2. Envío de Correo de Texto Plano (Básico)
- **Qué es** — La forma más rápida de enviar un correo, útil para alertas internas de sistema (ej: "Error grave en base de datos").
- **Código**:
  ```java
  @Service
  @Slf4j
  public class MailService {
  
      private final JavaMailSender mailSender;
      // Inyectamos el remitente desde las properties para no hardcodearlo
      @Value("${spring.mail.username}") 
      private String from;
  
      public MailService(JavaMailSender mailSender) {
          this.mailSender = mailSender;
      }
  
      @Async // SIEMPRE enviar correos asíncronamente
      public void enviarTextoPlano(String destinatario, String asunto, String texto) {
          log.info("Enviando correo plano a {}", destinatario);
          
          SimpleMailMessage message = new SimpleMailMessage();
          message.setFrom(from);
          message.setTo(destinatario);
          message.setSubject(asunto);
          message.setText(texto);
          
          mailSender.send(message);
          
          log.info("Correo plano enviado con éxito");
      }
  }
  ```

#### 3. Envío de Correos HTML con Thymeleaf
- **Qué es** — Cargar un archivo `.html`, inyectarle datos dinámicos y enviarlo.
- **Por qué importa** — Es la forma profesional de enviar notificaciones al cliente.
- **Código** — Integración con el motor de plantillas:
  
  **A. El Servicio:**
  ```java
  @Service
  public class AdvancedMailService {
  
      private final JavaMailSender mailSender;
      private final SpringTemplateEngine templateEngine; // Motor de Thymeleaf
  
      public AdvancedMailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
          this.mailSender = mailSender;
          this.templateEngine = templateEngine;
      }
  
      @Async
      public void enviarCorreoBienvenida(String destinatario, String nombre, String urlActivacion) {
          try {
              // 1. Preparar las variables para el HTML
              Context context = new Context();
              context.setVariable("nombre", nombre);
              context.setVariable("link", urlActivacion);
  
              // 2. Procesar el HTML (lee el archivo resources/templates/bienvenida.html)
              String htmlBody = templateEngine.process("bienvenida", context);
  
              // 3. Crear el mensaje complejo (MimeMessage)
              MimeMessage message = mailSender.createMimeMessage();
              MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
              
              helper.setTo(destinatario);
              helper.setSubject("¡Bienvenido a Nuestra App!");
              // true indica que el texto es código HTML
              helper.setText(htmlBody, true); 
              
              // 4. Enviar
              mailSender.send(message);
              
          } catch (MessagingException e) {
              throw new RuntimeException("Error al armar el correo HTML", e);
          }
      }
  }
  ```

  **B. La Plantilla HTML (`src/main/resources/templates/bienvenida.html`):**
  ```html
  <!DOCTYPE html>
  <html xmlns:th="http://www.thymeleaf.org">
  <head>
      <meta charset="UTF-8">
      <style>
          body { font-family: Arial, sans-serif; }
          .btn { background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; }
      </style>
  </head>
  <body>
      <h2>Hola <span th:text="${nombre}">Usuario</span>!</h2>
      <p>Gracias por unirte a nuestra plataforma.</p>
      
      <!-- Inyectamos el link en el href -->
      <a th:href="${link}" class="btn">Activar Cuenta</a>
      
      <p>Si el botón no funciona, copia y pega este enlace:</p>
      <p th:text="${link}"></p>
  </body>
  </html>
  ```

#### 4. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| Lentitud extrema de la API | Llamar a `mailSender.send()` dentro del Controller de forma síncrona | Poner `@Async` en el método que envía el correo. (Recuerda poner `@EnableAsync` en la clase principal). |
| `AuthenticationFailedException` | El servidor SMTP (como Gmail) bloqueó la conexión | Las contraseñas de las cuentas normales de Gmail no sirven. Debes activar "Contraseñas de aplicación" o usar servicios profesionales como SendGrid/Mailtrap. |
| El CSS no se ve en Gmail/Outlook | Usaste un archivo externo `<link rel="stylesheet">` o la etiqueta `<style>` global | Los clientes de correo son horribles leyendo CSS. Debes usar "CSS Inline" (`<p style="color:red">`) o usar herramientas externas que transformen (inlineen) tu HTML antes de enviarlo. |
| Plantilla no encontrada | Thymeleaf no encuentra el `.html` | Thymeleaf siempre busca dentro de la carpeta `src/main/resources/templates/` por defecto. No le pongas la extensión `.html` al llamar a `process("nombre", context)`. |

---

### Ejercicios
1. Regístrate en una cuenta gratuita de **Mailtrap** (https://mailtrap.io). Ve al In-box y copia tus credenciales SMTP (Host, Port, Username, Password). Pégalas en el `application.yml`.
2. Añade las dependencias de `spring-boot-starter-mail` y `spring-boot-starter-thymeleaf`.
3. Crea un Controller `/api/mail/test` que invoque un servicio para enviar un correo de texto plano. Prueba el endpoint y mira en la web de Mailtrap cómo llega el correo.
4. Crea una plantilla HTML en `resources/templates/factura.html`.
5. Modifica tu servicio para que inyecte un objeto `Factura (monto, fecha, cliente)` en el `Context` de Thymeleaf, procese la plantilla y envíe el correo en formato HTML. Verifica el resultado visual en Mailtrap.

### Cómo ejecutar
```bash
cd 28-mail
mvn spring-boot:run

# Probar envío asíncrono
curl -X POST http://localhost:8080/api/mail/test \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com", "nombre":"Juan"}'
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `pom.xml` | Dependencias `starter-mail` y `starter-thymeleaf`. |
| `application.yml` | Credenciales de Mailtrap/SendGrid. |
| `service/AdvancedMailService.java` | Lógica asíncrona usando JavaMailSender y SpringTemplateEngine. |
| `src/main/resources/templates/bienvenida.html` | Plantilla HTML con sintaxis básica de Thymeleaf (`th:text`). |
