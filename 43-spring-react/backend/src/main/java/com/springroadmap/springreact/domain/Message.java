package com.springroadmap.springreact.domain;

/**
 * Message: modelo de dominio inmutable que representa un mensaje del chat/muro.
 *
 * ANALOGIA: es una "tarjeta postal" — una vez escrita, no se modifica. Si quieres
 * cambiarla, escribes una nueva.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *
 *   ANTES (Java 8) — POJO clasico con getters, equals, hashCode, toString:
 *     public final class Message {
 *         private final Long id;
 *         private final String text;
 *         public Message(Long id, String text) { this.id = id; this.text = text; }
 *         public Long getId() { return id; }
 *         public String getText() { return text; }
 *         // + equals, hashCode, toString (30 lineas mas)
 *     }
 *
 *   AHORA (Java 21) — `record`: el compilador genera automaticamente:
 *     - constructor canonico   -> new Message(1L, "hola")
 *     - accessors              -> msg.id(), msg.text()   (sin "get")
 *     - equals + hashCode      -> comparacion por valor
 *     - toString               -> "Message[id=1, text=hola]"
 *
 * PREGUNTA DE ALUMNO — "por que id() y no getId()?"
 *   Los records usan el nombre del componente como accessor, sin prefijo "get".
 *   Es sintaxis moderna. Jackson lo serializa igual como {"id":1,"text":"hola"}.
 */
public record Message(Long id, String text) {
}
