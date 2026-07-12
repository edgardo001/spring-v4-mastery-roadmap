package com.springroadmap.springangular.domain;

/**
 * Task: modelo de dominio inmutable que representa una tarea del TODO list.
 *
 * ANALOGIA: es una "tarjeta de post-it" pegada en un tablero. Una vez escrita
 * su titulo, no se edita — para "completar" una tarea se crea una nueva version.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *
 *   ANTES (Java 8) — POJO clasico con getters, setters, equals, hashCode, toString:
 *     public final class Task {
 *         private final Long id;
 *         private final String title;
 *         private final boolean done;
 *         public Task(Long id, String title, boolean done) { ... }
 *         public Long getId() { return id; }
 *         public String getTitle() { return title; }
 *         public boolean isDone() { return done; }
 *         // + equals, hashCode, toString (40+ lineas mas)
 *     }
 *
 *   AHORA (Java 21) — `record`: el compilador genera automaticamente:
 *     - constructor canonico   -> new Task(1L, "estudiar", false)
 *     - accessors              -> t.id(), t.title(), t.done()   (sin "get"/"is")
 *     - equals + hashCode      -> comparacion por valor
 *     - toString               -> "Task[id=1, title=estudiar, done=false]"
 *
 * MATCH CON ANGULAR (TypeScript):
 *   export interface TaskDto {
 *     id: number;
 *     title: string;
 *     done: boolean;
 *   }
 *
 * PREGUNTA DE ALUMNO — "por que title() y no getTitle()?"
 *   Los records usan el nombre del componente como accessor, sin prefijo "get".
 *   Es sintaxis moderna. Jackson lo serializa igual como
 *   {"id":1,"title":"estudiar","done":false}, que es EXACTAMENTE lo que espera
 *   la interface TaskDto de TypeScript.
 */
public record Task(Long id, String title, boolean done) {
}
