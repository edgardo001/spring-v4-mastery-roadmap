package com.springroadmap.microservices.registry;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ServiceDiscoveryController: endpoints HTTP para operar el registro.
 *
 * ENDPOINTS:
 *   POST /registry?name=pagos&url=http://host:8081  -> registra una instancia.
 *   GET  /registry                                  -> lista todos los servicios.
 *
 * ANALOGIA:
 *   Es la ventanilla del centro comercial donde las tiendas nuevas llegan a
 *   registrarse ("hola, soy pagos y estoy en el local 8081") y donde los
 *   clientes preguntan "que tiendas hay?".
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES:  Map<String, Object> body = new HashMap<>(); body.put("ok", true);
 *   AHORA:  Map<String, Object> body = Map.of("ok", true);
 *           // 'Map.of' crea un mapa inmutable en una linea (Java 9+).
 */
@RestController                    // = @Controller + @ResponseBody (devuelve JSON).
@RequestMapping("/registry")       // Todos los endpoints cuelgan de /registry.
public class ServiceDiscoveryController {

    private final ServiceRegistry registry; // Constructor injection (buenas practicas).

    // Un solo constructor -> Spring inyecta el bean automaticamente (sin @Autowired).
    public ServiceDiscoveryController(ServiceRegistry registry) {
        this.registry = registry;
    }

    /**
     * Registra una URL bajo un nombre de servicio.
     * @RequestParam extrae los query params (?name=X&url=Y) de la URL.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestParam String name,
                                                        @RequestParam String url) {
        registry.register(name, url);
        // ResponseEntity permite controlar el status code y headers.
        return ResponseEntity.ok(Map.of(
            "registered", true,
            "service", name,
            "url", url
        ));
    }

    /**
     * Lista todos los servicios registrados y sus URLs.
     */
    @GetMapping
    public Map<String, List<String>> list() {
        return registry.listAll(); // Spring lo serializa a JSON via Jackson.
    }
}
