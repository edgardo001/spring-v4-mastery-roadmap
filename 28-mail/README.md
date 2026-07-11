# Módulo 28 - Mail (Envío de emails con Spring)

Envío de emails desde una aplicación Spring Boot 4.1.0 usando `JavaMailSender`, tanto en texto plano (`SimpleMailMessage`) como en HTML (`MimeMessage` + `MimeMessageHelper`).

## Stack

- Spring Boot 4.1.0
- Java 21
- Maven portable (`../tools/apache-maven-3.9.9`)
- `spring-boot-starter-mail`
- `spring-boot-starter-thymeleaf` (para plantillas HTML)
- MockMvc standalone (sin Lombok)

## Estructura

```
28-mail/
├── pom.xml
├── build.sh / build.ps1
└── src/
    ├── main/
    │   ├── java/com/springroadmap/mail/
    │   │   ├── MailApplication.java
    │   │   ├── controller/EmailController.java
    │   │   ├── dto/EmailRequest.java
    │   │   └── service/EmailService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── application-test.yml
    │       └── templates/welcome.html
    └── test/java/com/springroadmap/mail/
        ├── MailApplicationTests.java
        ├── controller/EmailControllerTest.java
        └── service/EmailServiceTest.java
```

## Build

```bash
./build.sh          # Linux/Mac/Git Bash
./build.ps1         # Windows PowerShell
```

Artefacto: `target/mail-1.0.0.jar`

## Endpoints

### `POST /api/emails/simple`
```json
{ "to": "alumno@datasoft.cl", "subject": "Hola", "body": "Texto plano" }
```

### `POST /api/emails/html`
```json
{ "to": "alumno@datasoft.cl", "subject": "Hola", "body": "<h1>HTML</h1>" }
```

Respuesta 200:
```json
{ "status": "sent", "type": "simple", "to": "alumno@datasoft.cl" }
```

## Antes vs Ahora

### Antes (Java EE puro con `javax.mail`)

```java
Properties props = new Properties();
props.put("mail.smtp.host", "smtp.gmail.com");
props.put("mail.smtp.port", "587");
props.put("mail.smtp.auth", "true");
props.put("mail.smtp.starttls.enable", "true");

Session session = Session.getInstance(props, new Authenticator() {
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("user", "pass");
    }
});

Message msg = new MimeMessage(session);
msg.setFrom(new InternetAddress("no-reply@x.cl"));
msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("a@b.cl"));
msg.setSubject("Hola");
msg.setText("Cuerpo");
Transport.send(msg);
```

Cada llamada abre conexión, maneja `MessagingException` a mano, y las credenciales viven en el código. Testear implica realmente conectarse a un SMTP o inventar wrappers.

### Ahora (Spring Boot 4 con `JavaMailSender`)

Configuración declarativa en `application.yml`:
```yaml
spring:
  mail:
    host: localhost
    port: 2525
```

Servicio con constructor injection:
```java
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimple(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}
```

Ventajas:
- Configuración externa por perfiles (`application-test.yml`).
- Test unitario con `mock(JavaMailSender.class)` sin abrir sockets.
- `MimeMessageHelper` simplifica multipart, HTML, adjuntos.
- Autoconfiguración: si hay `spring.mail.host` Spring crea el bean solo.

## Tests

- `MailApplicationTests.contextLoads` - arranque de contexto con perfil `test`.
- `EmailServiceTest` (unitario puro):
  ```java
  JavaMailSender sender = mock(JavaMailSender.class);
  EmailService service = new EmailService(sender);
  service.sendSimple("a@b.cl", "hi", "hello");
  verify(sender).send(any(SimpleMailMessage.class));
  ```
- `EmailControllerTest` - MockMvc standalone con `EmailService` mockeado.

Ejecutar solo tests:
```bash
../tools/apache-maven-3.9.9/bin/mvn test
```

## FAQ Alumno

**¿Por qué no envía correo cuando corro los tests?**
Porque `EmailServiceTest` usa `mock(JavaMailSender.class)`. No hay SMTP real, solo verificamos que `send(...)` fue invocado con el tipo correcto. Esto es lo correcto en un test unitario: prueba la lógica, no la infraestructura de terceros.

**¿Y para probar el envío real en mi máquina?**
Levanta un servidor SMTP local de desarrollo. Dos opciones estándar:
- **MailHog** (`docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog`). SMTP en 1025, UI web en `http://localhost:8025` donde ves los correos capturados.
- **Ethereal Email** (`https://ethereal.email`). Genera credenciales SMTP temporales y ves los mensajes en su UI, sin instalar nada.
Ajusta `spring.mail.host`, `spring.mail.port`, `username` y `password` según el proveedor.

**¿Diferencia entre `SimpleMailMessage` y `MimeMessage`?**
`SimpleMailMessage` es texto plano, API trivial, sin adjuntos ni HTML. `MimeMessage` (con `MimeMessageHelper`) permite HTML (`setText(html, true)`), adjuntos, imágenes inline y multipart. Regla práctica: notificación interna corta = simple; correo a cliente con branding = HTML.

**¿Por qué `MimeMessageHelper` y no `MimeMessage` directo?**
Porque `MimeMessage` es API JavaMail cruda. `MimeMessageHelper` es el wrapper de Spring que expone `setTo(String)`, `setText(html, true)`, `addAttachment(...)` sin exponer `InternetAddress`, `Multipart`, `BodyPart`, etc.

**¿Dónde configuro remitente por defecto (`From`)?**
En Boot 4 puedes fijarlo por propiedad extendida o mejor: setéalo en el `SimpleMailMessage` / `MimeMessageHelper.setFrom(...)`. Para uniformidad, expón un `@ConfigurationProperties` o inyecta `@Value("${app.mail.from}")` y aplícalo dentro de `EmailService`.

**¿Por qué inyecto `JavaMailSender` por constructor y no con `@Autowired` en campo?**
Constructor injection es la convención del roadmap: hace explícitas las dependencias, permite `final`, y permite construir `new EmailService(mock)` en tests sin Spring.

**¿Thymeleaf para qué está aquí si no lo uso en el ejemplo?**
Para el siguiente paso natural: renderizar HTML desde una plantilla (`templates/welcome.html`) usando `TemplateEngine`, y pasarlo a `sendHtml(...)`. Queda como ejercicio: inyectar `SpringTemplateEngine`, hacer `engine.process("welcome", context)` y enviar el resultado.

**¿Puedo mandar adjuntos?**
Sí: `helper.addAttachment("factura.pdf", new FileSystemResource(file))`. Requiere `MimeMessageHelper(mime, true, "UTF-8")` (el `true` habilita multipart).

**¿Y si el SMTP tarda o falla?**
`send(...)` es síncrono. Para producción envuelve el envío con `@Async` (módulo 21) o publica un evento y consúmelo en un worker. Nunca bloquees un endpoint HTTP esperando SMTP.
