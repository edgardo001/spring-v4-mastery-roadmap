package com.springroadmap.springangular.controller;

import com.springroadmap.springangular.domain.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TaskController: expone la API REST `/api/tasks` que consume el frontend Angular.
 *
 * ANALOGIA: es la "ventanilla de atencion" del backend. Angular (el cliente,
 * corriendo en localhost:4200 durante desarrollo) se acerca a la ventanilla y
 * pide o entrega una tarea. La ventanilla no sabe ni le importa quien esta al
 * otro lado (Angular, React, curl, Postman...).
 *
 * ANTES (Java 8 + AngularJS 1.x):
 *   - Backend devolvia JSP con datos embebidos.
 *   - AngularJS hacia $http.get('/tasks') a la MISMA app (mismo origen).
 *   - No habia CORS porque backend y front vivian en el mismo puerto/dominio.
 *
 * AHORA (Java 21 + Angular v22):
 *   - Backend REST puro devolviendo JSON.
 *   - Angular vive en localhost:4200, backend en localhost:8080 -> hay CORS.
 *   - Se resuelve con proxy.conf.json en dev y @CrossOrigin/CorsConfig en prod.
 *
 * ANTES vs AHORA — inmutabilidad:
 *   ANTES: List<Task> lista = new ArrayList<>();
 *          lista.add(new Task(1L, "estudiar", false));
 *          return lista;
 *   AHORA: return List.copyOf(store);  // copia inmutable, thread-safe
 *
 * NOTA CORS: @CrossOrigin habilita el origen de Angular CLI (4200). En produccion
 * se recomienda mover CORS al API Gateway / NGINX (ver docker-compose.yml). Aqui
 * esta activo tanto por la anotacion como por WebMvcConfigurer (CorsConfig) — la
 * anotacion documenta la intencion en el codigo.
 *
 * PREGUNTA DE ALUMNO — "por que el POST devuelve 201?"
 *   Convencion REST: 200 = OK / lectura correcta, 201 = Created cuando se crea
 *   un recurso nuevo. Usamos ResponseEntity para controlar el status HTTP.
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    // Almacen en memoria (demo). En produccion seria un @Repository con BD.
    // CopyOnWriteArrayList es thread-safe: seguro para lecturas concurrentes.
    private final List<Task> store = new CopyOnWriteArrayList<>();

    // AtomicLong genera IDs unicos incluso con multiples hilos escribiendo a la vez.
    // ANTES (Java 8): long id = 0; id++; (NO thread-safe).
    // AHORA (Java 21): AtomicLong.incrementAndGet() (atomico).
    private final AtomicLong idSeq = new AtomicLong(0);

    public TaskController() {
        // Semilla: dos tareas de ejemplo para que Angular tenga algo que renderizar.
        store.add(new Task(idSeq.incrementAndGet(), "Aprender Angular v22", false));
        store.add(new Task(idSeq.incrementAndGet(), "Integrar con Spring Boot 4", true));
    }

    /**
     * GET /api/tasks — Publico. Devuelve todas las tareas.
     * Angular las consumira con this.http.get<Task[]>('/api/tasks') y las
     * mostrara con `*ngFor="let t of tasks$ | async"`.
     */
    @GetMapping
    public List<Task> list() {
        // Copia inmutable: el cliente no puede mutar el store interno del server.
        return List.copyOf(store);
    }

    /**
     * POST /api/tasks — Requiere Basic Auth (definido en SecurityConfig).
     * Recibe un Task (sin id), asigna id y lo guarda.
     * Angular debe inyectar el header Authorization via HttpInterceptor.
     */
    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task payload) {
        // Ignoramos el id que envie el cliente (lo asigna siempre el servidor).
        Task created = new Task(idSeq.incrementAndGet(), payload.title(), payload.done());
        store.add(created);
        // ResponseEntity.status(201).body(created) => HTTP 201 Created.
        return ResponseEntity.status(201).body(created);
    }
}
