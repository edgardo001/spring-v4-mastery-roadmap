package com.springroadmap.springreact.controller;

import com.springroadmap.springreact.domain.Message;
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
 * MessageController: expone la API REST `/api/messages` que consume el frontend React.
 *
 * ANALOGIA: es la "ventanilla de atencion" del backend. React (el cliente) se acerca
 * a la ventanilla y pide o entrega un mensaje. La ventanilla no sabe ni le importa
 * quien esta al otro lado (React, Angular, curl, Postman...).
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   ANTES: List<Message> lista = new ArrayList<>();
 *          lista.add(new Message(1L, "hola"));
 *          return lista;
 *   AHORA: return List.copyOf(store);  // inmutable, thread-safe
 *
 * NOTA CORS: @CrossOrigin habilita orígenes específicos del entorno de desarrollo
 * de React (Vite=5173, CRA=3000). En produccion se recomienda mover CORS al
 * API Gateway / NGINX (ver docker-compose.yml). Aqui esta activo tanto por la
 * anotacion como por WebMvcConfigurer (CorsConfig) — la anotacion documenta la
 * intencion en el codigo.
 *
 * PREGUNTA DE ALUMNO — "por que el POST devuelve 201?"
 *   Convencion REST: 200 = OK / lectura correcta, 201 = Created cuando se crea un
 *   recurso nuevo. Usamos ResponseEntity para controlar el status HTTP.
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class MessageController {

    // Almacen en memoria (demo). En produccion seria un @Repository con BD.
    // CopyOnWriteArrayList es thread-safe: seguro para lecturas concurrentes.
    private final List<Message> store = new CopyOnWriteArrayList<>();

    // AtomicLong genera IDs unicos incluso con multiples hilos escribiendo a la vez.
    // ANTES (Java 8): long id = 0; id++; (NO thread-safe).
    // AHORA (Java 21): AtomicLong.incrementAndGet() (atomico).
    private final AtomicLong idSeq = new AtomicLong(0);

    public MessageController() {
        // Semilla: dos mensajes de ejemplo para que el frontend tenga algo que mostrar.
        store.add(new Message(idSeq.incrementAndGet(), "Bienvenido al modulo 43"));
        store.add(new Message(idSeq.incrementAndGet(), "Backend Spring + Frontend React"));
    }

    /**
     * GET /api/messages — Publico. Devuelve todos los mensajes.
     * Devuelve una copia inmutable para que el cliente no pueda mutar el store interno.
     */
    @GetMapping
    public List<Message> list() {
        return List.copyOf(store);
    }

    /**
     * POST /api/messages — Requiere Basic Auth (definido en SecurityConfig).
     * Recibe un Message (sin id), asigna id y lo guarda.
     */
    @PostMapping
    public ResponseEntity<Message> create(@RequestBody Message payload) {
        // Ignoramos el id que envie el cliente (lo asigna el servidor).
        Message created = new Message(idSeq.incrementAndGet(), payload.text());
        store.add(created);
        // ResponseEntity.status(201).body(created) => HTTP 201 Created.
        return ResponseEntity.status(201).body(created);
    }
}
