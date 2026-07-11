package com.springroadmap.websocket.dto;

// Instant = punto exacto en el tiempo en UTC (nanosegundos desde 1970-01-01T00:00:00Z).
// Elegido a propósito frente a java.util.Date para evitar bugs de zona horaria en despliegues en la nube.
import java.time.Instant;

/**
 * Mensaje de chat que viaja entre cliente y servidor por WebSocket/STOMP.
 *
 * ANALOGÍA:
 *   Es como un post-it que se pega en un tablón (el /topic/messages).
 *   Trae: quién lo escribió (from), qué dice (content), y a qué hora (timestamp).
 *
 * PREGUNTA DE ALUMNO — "¿qué es un 'record'?"
 *   Un record (Java 14+, estable desde 16) es una clase INMUTABLE de datos:
 *     - Genera automáticamente el constructor, getters (con el nombre del campo, no getFrom()),
 *       equals(), hashCode() y toString().
 *     - No permite herencia ni setters.
 *     - Sirve perfectamente como DTO (Data Transfer Object).
 *
 * ANTES (Java 8):
 *   public class ChatMessage {
 *       private final String from;
 *       private final String content;
 *       private final Instant timestamp;
 *       public ChatMessage(String from, String content, Instant timestamp) {
 *           this.from = from; this.content = content; this.timestamp = timestamp;
 *       }
 *       public String getFrom() { return from; }
 *       public String getContent() { return content; }
 *       public Instant getTimestamp() { return timestamp; }
 *       // + equals, hashCode, toString (Lombok @Value o generarlos a mano)
 *   }
 *
 * AHORA (Java 21):
 *   public record ChatMessage(String from, String content, Instant timestamp) {}
 *   Una sola línea. El compilador genera lo demás.
 *
 * NOTA sobre Jackson:
 *   Spring Boot 4 + Jackson 2.15+ deserializa records nativamente sin configuración extra,
 *   siempre que los nombres de los componentes coincidan con las claves JSON.
 */
public record ChatMessage(String from, String content, Instant timestamp) {
}
