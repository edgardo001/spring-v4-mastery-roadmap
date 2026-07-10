package com.springroadmap.files.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de almacenamiento de archivos.
 *
 * <p>Analogía: es el "empleado del almacén" que recibe paquetes
 * (archivos multipart), les asigna un código de barras único (UUID)
 * y los guarda en un estante conocido (carpeta en disco). Cuando
 * alguien vuelve por el paquete, lo busca por su código y lo entrega.</p>
 *
 * <p>Este servicio guarda los archivos en {@code ${java.io.tmpdir}/roadmap-uploads}
 * (carpeta temporal del sistema, ej: {@code C:\Users\...\AppData\Local\Temp\roadmap-uploads}).
 * Se elige la temp porque es didáctico, atómico y evita ensuciar el proyecto.
 * En producción se preferiría S3, GCS o un volumen dedicado.</p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES (Java 8, estilo clásico):
 * String uploadDir = System.getProperty("java.io.tmpdir") + "/roadmap-uploads";
 * File dir = new File(uploadDir);
 * if (!dir.exists()) { dir.mkdirs(); }
 *
 * // AHORA (Java 21, NIO.2 con java.nio.file):
 * Path root = Paths.get(System.getProperty("java.io.tmpdir"), "roadmap-uploads");
 * Files.createDirectories(root);
 * // Ventaja: Path es inmutable y multiplataforma; Files.copy soporta streams grandes.
 * </pre>
 */
@Service
public class FileStorageService {

    // 'final' = el campo se asigna UNA sola vez en el constructor y no cambia.
    // Ayuda a que la clase sea inmutable y thread-safe.
    private final Path rootLocation;

    /**
     * Constructor por defecto: usa {@code ${java.io.tmpdir}/roadmap-uploads}.
     * Spring llamará este constructor al arrancar (constructor injection sin dependencias).
     */
    public FileStorageService() {
        // Delegamos al otro constructor pasando la ruta calculada.
        this(Paths.get(System.getProperty("java.io.tmpdir"), "roadmap-uploads"));
    }

    /**
     * Constructor de conveniencia para tests: permite inyectar una carpeta
     * temporal custom sin depender de {@code java.io.tmpdir}.
     */
    public FileStorageService(Path rootLocation) {
        // .toAbsolutePath().normalize() convierte "./x/../y" en "/abs/y",
        // eliminando ambigüedades y ataques de path traversal.
        this.rootLocation = rootLocation.toAbsolutePath().normalize();
        try {
            // Crea la carpeta si no existe. No falla si ya existe.
            Files.createDirectories(this.rootLocation);
        } catch (IOException ex) {
            // Envolvemos la excepción chequeada en una unchecked porque
            // si no podemos crear la carpeta, la app no tiene sentido.
            throw new IllegalStateException("No se pudo crear la carpeta de subidas: " + this.rootLocation, ex);
        }
    }

    /**
     * Guarda el archivo recibido en disco y devuelve el nombre único generado.
     *
     * @param file archivo multipart recibido por el controller.
     * @return nombre final del archivo en disco (ej: {@code "a1b2c3d4.pdf"}).
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío o es nulo");
        }

        // StringUtils.cleanPath elimina "../" y normaliza separadores; NO confiar
        // en el nombre original tal cual, viene del cliente y podría ser malicioso.
        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "archivo" : file.getOriginalFilename());

        // Defensa extra contra path traversal (ej: "..\\..\\etc\\passwd").
        if (originalName.contains("..")) {
            throw new IllegalArgumentException("Nombre de archivo inválido: " + originalName);
        }

        // Extraer extensión (todo lo que va del último '.' en adelante).
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            extension = originalName.substring(dot);
        }

        // UUID.randomUUID() genera un identificador único de 128 bits, hex.
        // Evita colisiones si dos usuarios suben "foto.jpg" al mismo tiempo.
        String uniqueName = UUID.randomUUID() + extension;

        // resolve() concatena de forma segura (usa el separador correcto según SO).
        Path target = this.rootLocation.resolve(uniqueName).normalize();

        try {
            // Files.copy con InputStream transfiere el archivo por streaming
            // (no carga TODO en memoria) — clave para no reventar la RAM.
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar el archivo " + originalName, ex);
        }

        return uniqueName;
    }

    /**
     * Carga un archivo previamente guardado como {@link Resource}.
     * Un {@code Resource} es la abstracción de Spring para "algo que puede leerse
     * como bytes" (archivo en disco, URL, classpath, etc.).
     *
     * @param filename nombre generado por {@link #store(MultipartFile)}.
     * @return recurso legible por el controller.
     */
    public Resource load(String filename) {
        try {
            Path file = this.rootLocation.resolve(filename).normalize();

            // Defensa: el path resuelto DEBE seguir dentro del rootLocation.
            // Si un atacante manda "../../etc/passwd" queremos rechazarlo.
            if (!file.startsWith(this.rootLocation)) {
                throw new IllegalArgumentException("Acceso fuera del directorio permitido: " + filename);
            }

            // UrlResource envuelve una URI. Path.toUri() produce file:///... válido.
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalStateException("Archivo no encontrado o ilegible: " + filename);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("URI mal formada para " + filename, ex);
        }
    }

    /** Getter útil para tests que quieran inspeccionar la carpeta destino. */
    public Path getRootLocation() {
        return this.rootLocation;
    }
}
