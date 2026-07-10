package com.springroadmap.mvcrest.controller;

import com.springroadmap.mvcrest.domain.Product;
import com.springroadmap.mvcrest.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST para el recurso {@code Product}.
 *
 * PROPÓSITO
 * ---------
 * Exponer un CRUD completo bajo /api/products:
 *   GET    /api/products         → listar todos.
 *   GET    /api/products/{id}    → uno por id.
 *   POST   /api/products         → crear.
 *   PUT    /api/products/{id}    → actualizar.
 *   DELETE /api/products/{id}    → borrar.
 *
 * ANALOGÍA
 * --------
 * Piensa en el controller como el MOZO de un restaurante:
 *   - Tú (cliente HTTP) le pides "tráeme el plato 42" (GET /42).
 *   - El mozo va a la cocina (Repository), busca y te trae el plato serializado.
 *   - El mozo NO cocina: solo traduce lo que pides al lenguaje interno.
 *
 * ANTES (Servlet clásico)
 * -----------------------
 *   public class ProductServlet extends HttpServlet {
 *       protected void doGet(HttpServletRequest req, HttpServletResponse resp)
 *               throws IOException {
 *           String pathInfo = req.getPathInfo();     // "/42"
 *           Long id = Long.parseLong(pathInfo.substring(1));
 *           Product p = repository.findById(id);
 *           if (p == null) { resp.setStatus(404); return; }
 *           resp.setContentType("application/json");
 *           new ObjectMapper().writeValue(resp.getWriter(), p);  // serialización a mano
 *       }
 *   }
 *   (más web.xml con <servlet-mapping>...)
 *
 * AHORA (Spring MVC 4 / Boot 4)
 * -----------------------------
 *   @GetMapping("/{id}")
 *   public ResponseEntity<Product> getById(@PathVariable Long id) {
 *       return repo.findById(id)
 *                  .map(ResponseEntity::ok)
 *                  .orElse(ResponseEntity.notFound().build());
 *   }
 *
 * PREGUNTA DE ALUMNO — "¿qué es la arroba '@' en Java?"
 *   Es una ANOTACIÓN: metadata que Spring lee por reflexión en runtime para
 *   configurar comportamiento. @GetMapping le dice a Spring "cuando entre un
 *   GET a esta URL, invoca este método".
 *
 * PREGUNTA DE ALUMNO — "¿qué es @RestController? ¿es distinto de @Controller?"
 *   Sí. @Controller devuelve NOMBRES DE VISTAS (Thymeleaf/JSP).
 *   @RestController = @Controller + @ResponseBody → cada retorno se serializa
 *   directamente a JSON en el body de la respuesta HTTP.
 */
@RestController                        // Cada método retorna datos, no vistas.
@RequestMapping("/api/products")       // Prefijo común para todos los endpoints de esta clase.
public class ProductController {

    // final + constructor injection: dependencia inmutable, testeable y thread-safe.
    private final ProductRepository repository;

    /**
     * Constructor injection: Spring 4+ ya no necesita @Autowired si hay un solo
     * constructor. Recibe el bean {@code ProductRepository} que registró
     * @Repository al arrancar el contexto.
     */
    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * GET /api/products → lista todos.
     * Status 200 OK siempre (una lista vacía sigue siendo una respuesta válida).
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> all = repository.findAll();
        return ResponseEntity.ok(all);
    }

    /**
     * GET /api/products/{id} → uno por id.
     *
     * @PathVariable extrae el segmento {id} de la URL y lo convierte a Long
     * automáticamente (si viene "abc" Spring lanza 400 antes de entrar aquí).
     *
     * Retorna 200 si existe, 404 si no.
     *
     * ANTES:  if (p == null) return 404; else return 200 con body;
     * AHORA:  Optional + method references + map/orElse.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)                  // 200 OK con el producto en el body.
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404 sin body.
    }

    /**
     * POST /api/products → crea un producto.
     *
     * @RequestBody dice "toma el JSON del body y deserialízalo a Product".
     * Devolvemos 201 Created con:
     *   - Header Location apuntando al recurso creado (buena práctica REST).
     *   - El body con el producto ya persistido (incluye el id asignado).
     */
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product incoming) {
        // Forzamos id = null: el cliente NO debe fijar el id en un POST.
        Product toSave = new Product(null, incoming.name(), incoming.price());
        Product saved = repository.save(toSave);
        URI location = URI.create("/api/products/" + saved.id());
        return ResponseEntity.created(location).body(saved);
    }

    /**
     * PUT /api/products/{id} → actualiza (idempotente).
     *
     * Regla: si el id no existe → 404.
     * Si existe → sobrescribimos con los datos del body y retornamos 200.
     *
     * Nota: PUT REEMPLAZA el recurso completo. Para modificaciones parciales
     * se usa PATCH (fuera de scope de este módulo).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product incoming) {
        if (repository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();     // 404: no había nada que actualizar.
        }
        Product toSave = new Product(id, incoming.name(), incoming.price());
        Product saved = repository.save(toSave);
        return ResponseEntity.ok(saved);                  // 200 con la versión final.
    }

    /**
     * DELETE /api/products/{id} → borra.
     *
     * 204 No Content si borró (convención REST: éxito sin body).
     * 404 Not Found si el id no existía.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = repository.deleteById(id);
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
