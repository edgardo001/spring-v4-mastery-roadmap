## 16 — Subida y Descarga de Archivos (Multipart)

### Propósito
Aprender a manejar la carga (upload) y descarga (download) de archivos en una API REST de Spring Boot, utilizando la interfaz `MultipartFile` y entendiendo cómo almacenar los archivos en el sistema local de forma segura, o prepararlos para la nube (AWS S3, Google Cloud Storage).

### Problema que resuelve
Las aplicaciones web necesitan manejar documentos adjuntos, imágenes de perfil o reportes generados. Sin una estrategia clara para la subida de archivos, te enfrentas a problemas como:
- **Exhaustión de Memoria (OOM):** Subir un archivo de 2GB directo a la RAM colgará tu servidor si no usas flujos (streams) o límites.
- **Riesgos de Seguridad:** Un usuario sube un archivo `script.sh` o `shell.php` y, si lo guardas mal, podrías permitir la ejecución remota de código (RCE).
- **Sobrecarga de Base de Datos:** Guardar archivos binarios pesados (BLOBs) en la base de datos deprime el rendimiento drásticamente.

### Cómo lo resuelve
Spring abstrae el manejo de peticiones de tipo `multipart/form-data` mediante la interfaz `MultipartFile`. Spring se encarga de guardar el archivo temporalmente en disco durante la carga para no saturar la memoria RAM. Luego tú decides qué hacer con él: validarlo, renombrarlo, y almacenarlo en un directorio del servidor o transferirlo a un servicio de almacenamiento en la nube (Object Storage).

### Por qué aprenderlo
Prácticamente todos los sistemas empresariales modernos manejan documentos: sistemas de RRHH (currículums), comercio electrónico (imágenes de productos), bancos (comprobantes en PDF). Entender los límites del tamaño, el manejo de excepciones de sobrecarga, y las validaciones de tipo de contenido, es un conocimiento de alto valor comercial.

```mermaid
graph TD
    A["Cliente (Frontend)"] -->|POST /api/files<br/>(multipart/form-data)| B["Spring DispatcherServlet"]
    B --> C{"Verificar Límites<br/>(max-file-size)"}
    C -->|"Excede límite"| D["MaxUploadSizeExceededException"]
    D --> E["413 Payload Too Large"]
    C -->|"Tamaño OK"| F["FileController"]
    F --> G["FileService"]
    G --> H{"Validar extensión<br/>y mimetype"}
    H -->|"Archivo no válido"| I["400 Bad Request"]
    H -->|"Archivo OK"| J["Guardar en Disco (o S3)"]
    J --> K["200 OK + URL del archivo"]

    style C fill:#ffa94d,color:#fff
    style D fill:#ff6b6b,color:#fff
    style J fill:#51cf66,color:#fff
```

---

### Glosario Básico

#### `MultipartFile`
La interfaz de Spring que representa un archivo cargado. Permite acceder a los bytes, el nombre original, el tamaño y el Content-Type.
```java
@PostMapping("/upload")
public String upload(@RequestParam("file") MultipartFile file) {
    long size = file.getSize(); // bytes
    String name = file.getOriginalFilename(); // ej: foto.jpg
}
```

#### `multipart/form-data`
El Content-Type HTTP diseñado específicamente para el envío de archivos y grandes volúmenes de datos binarios, a diferencia de `application/json`.

#### `MaxUploadSizeExceededException`
Excepción lanzada automáticamente por Spring si el cliente envía un archivo más grande de lo configurado en las propiedades `spring.servlet.multipart.max-file-size`.

#### `Resource` y `UrlResource`
Interfaces de Spring que representan un recurso físico (como un archivo en disco). Se utilizan para retornar el archivo descargable en una respuesta HTTP.

---

### Conceptos

#### 1. Configuración de Límites de Subida
- **Qué es** — Spring Boot impone límites de tamaño a los archivos (por defecto 1MB) para evitar ataques de denegación de servicio (DoS). Debes ajustar estos límites en el `application.yml` para los casos de uso reales de tu negocio.
- **Por qué importa** — Si no los cambias, los usuarios verán errores misteriosos al subir imágenes de sus teléfonos. Si los pones muy altos, un usuario malicioso puede tumbar tu servidor subiendo archivos gigantes simultáneamente.
- **Código** — Configuración en YAML:
  ```yaml
  spring:
    servlet:
      multipart:
        enabled: true
        # Tamaño máximo POR ARCHIVO individual
        max-file-size: 5MB
        # Tamaño máximo de TODA la petición (si envían varios archivos a la vez)
        max-request-size: 20MB
        # Umbral tras el cual el archivo se escribe en disco temporal (evita uso de RAM)
        file-size-threshold: 2KB
        
  app:
    # Propiedad custom para definir dónde guardar los archivos
    upload:
      dir: ./uploads
  ```

#### 2. Controlador de Upload (Subida)
- **Qué es** — El endpoint que recibe el `MultipartFile`. En las APIs REST, a menudo mezclamos JSON con archivos usando diferentes "parts" de la petición.
- **Código** — El Controlador REST:
  ```java
  @RestController
  @RequestMapping("/api/files")
  public class FileController {
  
      private final FileStorageService storageService;
  
      public FileController(FileStorageService storageService) {
          this.storageService = storageService;
      }
  
      @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
      public ResponseEntity<FileResponse> uploadFile(
              @RequestParam("file") MultipartFile file,
              @RequestParam(value = "description", required = false) String description) {
          
          // El servicio guarda el archivo y devuelve el nombre final
          String fileName = storageService.storeFile(file);
          
          // Construir URL de descarga amigable
          String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                  .path("/api/files/download/")
                  .path(fileName)
                  .toUriString();
                  
          return ResponseEntity.ok(new FileResponse(fileName, downloadUrl, file.getContentType(), file.getSize()));
      }
  }
  ```
- **Analogía** — El Controlador es el empleado de ventanilla de paquetería. Solo verifica que el paquete (archivo) no sea demasiado grande. Luego se lo pasa al de almacén (`FileStorageService`) para que lo guarde en el estante correcto.

#### 3. Servicio de Almacenamiento (Seguridad y Guardado)
- **Qué es** — La clase que ejecuta la lógica de negocio para validar y guardar físicamente el archivo. NUNCA se debe confiar en el nombre original del archivo.
- **Por qué importa** — Previene inyecciones de path (Directory Traversal) como guardar archivos en `../../../etc/passwd` y asegura que los nombres sean únicos.
- **Código** — El Servicio seguro:
  ```java
  @Service
  @Slf4j
  public class FileStorageService {
  
      private final Path fileStorageLocation;
  
      public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
          // Normaliza la ruta (convierte ./uploads a una ruta absoluta segura)
          this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
          
          try {
              // Crea el directorio si no existe
              Files.createDirectories(this.fileStorageLocation);
          } catch (Exception ex) {
              throw new RuntimeException("No se pudo crear el directorio de subidas.", ex);
          }
      }
  
      public String storeFile(MultipartFile file) {
          if (file.isEmpty()) {
              throw new BusinessRuleException("El archivo está vacío");
          }
  
          // Limpia el nombre del archivo (quita rutas maliciosas)
          String originalName = StringUtils.cleanPath(file.getOriginalFilename());
          
          // Validación de Path Traversal Attack
          if (originalName.contains("..")) {
              throw new BusinessRuleException("El archivo contiene una ruta inválida: " + originalName);
          }
  
          // Generar un nombre único para evitar sobreescritura (UUID)
          String extension = "";
          if (originalName.contains(".")) {
              extension = originalName.substring(originalName.lastIndexOf("."));
          }
          String uniqueFileName = UUID.randomUUID().toString() + extension;
  
          try {
              // Resolver la ruta final y copiar el stream de bytes
              Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
              Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
              
              log.info("Archivo guardado exitosamente: {}", uniqueFileName);
              return uniqueFileName;
              
          } catch (IOException ex) {
              throw new RuntimeException("No se pudo guardar el archivo " + originalName, ex);
          }
      }
  }
  ```

#### 4. Controlador de Download (Descarga)
- **Qué es** — Endpoint que sirve archivos previamente subidos. Debe setear correctamente el `Content-Type` y los Headers (como `Content-Disposition` para forzar la descarga o permitir visualizar en el navegador).
- **Código** — Descargar el archivo:
  ```java
      @GetMapping("/download/{fileName:.+}")
      public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
          
          // Cargar archivo como Recurso
          Resource resource = storageService.loadFileAsResource(fileName);
  
          // Determinar el Content-Type real
          String contentType = null;
          try {
              contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
          } catch (IOException ex) {
              log.info("No se pudo determinar el tipo de archivo.");
          }
  
          if (contentType == null) {
              contentType = "application/octet-stream"; // fallback seguro
          }
  
          return ResponseEntity.ok()
                  .contentType(MediaType.parseMediaType(contentType))
                  // 'attachment' fuerza la descarga, 'inline' lo muestra en el navegador (ej: PDFs/imágenes)
                  .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                  .body(resource);
      }
  ```

#### 5. Manejo Global de Error de Tamaño Máximo
- **Qué es** — Cuando se excede el `max-file-size`, la excepción ocurre *antes* de que llegue a tu controller. Por lo tanto, el Controller no puede hacer `try/catch`.
- **Por qué importa** — Sin un handler global en `@ControllerAdvice`, Tomcat/Undertow simplemente corta la conexión o devuelve un feo error HTML al frontend.
- **Código**:
  ```java
  @RestControllerAdvice
  public class FileExceptionHandler {
  
      @ExceptionHandler(MaxUploadSizeExceededException.class)
      public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
          ErrorResponse error = new ErrorResponse(
              413, "Payload Too Large", "FILE_TOO_LARGE",
              "El archivo excede el tamaño máximo permitido (5MB).",
              "/api/files/upload"
          );
          return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
      }
  }
  ```

#### 6. Edge Cases y Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `FileNotFoundException` al guardar | El directorio destino no existe | Usar `Files.createDirectories()` en el arranque del servicio |
| Path Traversal Attack | Guardar el archivo usando su `OriginalFilename` exacto | Usar `StringUtils.cleanPath()` y/o renombrar con `UUID.randomUUID()` |
| Archivos corrompidos | Guardar usando Strings o buffers parciales | Usar siempre `Files.copy(file.getInputStream(), ...)` que transfiere el Stream completo |
| OOM (Out Of Memory) | Cargar todos los bytes a RAM `file.getBytes()` en archivos pesados | Iterar/transferir Streams directamente en vez de `getBytes()` |
| Guardar en BD | Usar tipo `BLOB` (Mala Práctica) | Guardar la ruta/URL en BD (`varchar`), y el archivo binario en S3 o disco |

---

### Ejercicios
1. Configura el proyecto con `max-file-size=2MB`. Intenta subir una foto de 5MB y atrapa la excepción `MaxUploadSizeExceededException` en el ControllerAdvice.
2. Crea el servicio `FileStorageService` que guarde las subidas en la carpeta `mis-archivos` usando un UUID.
3. Escribe lógica de validación para aceptar SOLAMENTE archivos con extensión `.jpg` o `.png` y de tipo MIME `image/jpeg` o `image/png`.
4. Crea el endpoint `/download/{filename}`.
5. **(Avanzado)** Modifica la cabecera `Content-Disposition` a `inline` si el archivo es un JPG/PNG. Accede a la URL desde el navegador y verifica que la imagen se muestra en la pestaña en vez de descargarse forzosamente.

### Cómo ejecutar
```bash
cd 16-subida-archivos
mvn spring-boot:run

# Probar Subida:
curl -X POST http://localhost:8080/api/files/upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/ruta/a/tu/foto.jpg" \
  -F "description=Mi Foto"

# Probar Descarga:
curl -O http://localhost:8080/api/files/download/el-uuid-generado.jpg
```

### Archivos del Proyecto
| Archivo | Propósito |
|---------|-----------|
| `application.yml` | Configuración de límites `multipart`. |
| `service/FileStorageService.java` | Lógica segura de guardado y lectura de archivos. |
| `controller/FileController.java` | Endpoints `/upload` y `/download`. |
| `exception/FileExceptionHandler.java` | Manejador global para el error 413 (Payload Too Large). |
| `dto/FileResponse.java` | Payload JSON con la URL resultante. |
