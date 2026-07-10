package com.springroadmap.di.controller;

import com.springroadmap.di.repository.UserRepository;
import com.springroadmap.di.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST de demostración de DI.
 *
 * =====================================================================
 * CONSTRUCTOR INJECTION (el patrón MODERNO recomendado)
 * =====================================================================
 * Las dos dependencias (repository y service) se declaran como campos
 * `private final` y se reciben por el ÚNICO constructor. Desde Spring 4.3
 * NO hace falta @Autowired si solo hay un constructor: Spring lo detecta
 * automáticamente e inyecta los beans correspondientes al arrancar.
 *
 * Ventajas del constructor injection:
 *   1. Los campos son `final` -> INMUTABLES tras la construcción.
 *   2. Las dependencias son OBLIGATORIAS (si falta una, falla la compilación
 *      del contexto, no un NullPointerException en runtime).
 *   3. Se puede instanciar SIN Spring en tests: `new UserController(repo, svc)`.
 *   4. Facilita detectar "clases dios" (si el constructor tiene 8 parámetros,
 *      la clase está haciendo demasiado).
 *
 * =====================================================================
 * ANTES (Java 8 + Spring "clásico" — anti-patrón field injection)
 * =====================================================================
 * // MALO: no usar @Autowired en campo
 * @RestController
 * public class UserController {
 *     @Autowired private UserRepository repository;   // ← campo NO final
 *     @Autowired private NotificationService service; // ← se inyecta por reflexión
 *     // problemas: no se puede probar con `new`, permite valores null,
 *     //           oculta dependencias, IDE marca warning.
 * }
 *
 * =====================================================================
 * ANTES (Java 8 sin Spring — cableado manual)
 * =====================================================================
 * public class UserController {
 *     private final UserRepository repository;
 *     private final NotificationService service;
 *     public UserController() {
 *         this.repository = new UserRepository();       // ← acoplado a impl.
 *         this.service    = new NotificationService();  // ← acoplado a impl.
 *     }
 * }
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    // "private final" = referencia inmutable a la dependencia inyectada.
    private final UserRepository repository;
    private final NotificationService service;

    /**
     * Constructor UNICO -> Spring lo usa para inyectar las dependencias.
     * NO ponemos @Autowired: es redundante desde Spring 4.3.
     */
    public UserController(UserRepository repository, NotificationService service) {
        this.repository = repository;
        this.service = service;
    }

    /**
     * Endpoint que demuestra que Spring conectó Repository + Service en
     * este Controller.
     *
     * Flujo:
     *   1. Buscar el usuario por email en el repositorio.
     *   2. Si existe, notificarlo con el servicio y devolver 200.
     *   3. Si no, devolver 404 Not Found.
     *
     * @param email email del usuario a notificar
     * @param msg   mensaje que se "envía" en la notificación
     * @return ResponseEntity con el cuerpo formado o 404
     */
    @PostMapping("/notify")
    public ResponseEntity<String> notify(@RequestParam String email,
                                         @RequestParam(defaultValue = "hola") String msg) {
        return repository.findByEmail(email)
                .map(name -> ResponseEntity.ok(
                        "NOTIFIED:" + name + "|" + service.sendEmail(email, msg)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
