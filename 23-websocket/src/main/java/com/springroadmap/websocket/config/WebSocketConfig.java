package com.springroadmap.websocket.config;

// @Configuration marca esta clase como "fuente de beans/configuración" para el contenedor Spring.
import org.springframework.context.annotation.Configuration;

// Activa TODO el subsistema de mensajería por WebSocket + STOMP.
// Sin esta anotación, los @MessageMapping de los controllers son ignorados.
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

// Registro donde declaramos las URLs (endpoints) por las que los clientes abren el WebSocket.
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

// Interfaz que implementamos para "personalizar" el broker (destinos, endpoints, interceptores).
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// Registro donde configuramos el broker de mensajes (destinos /topic, /queue, prefijos /app, etc).
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

/**
 * Configuración del subsistema WebSocket + STOMP.
 *
 * ANALOGÍA:
 *   Piensa en una radio comunitaria.
 *   - registerStompEndpoints("/ws") = la FRECUENCIA por donde sintonizas la radio.
 *   - enableSimpleBroker("/topic")  = las EMISORAS (canales) a las que el público puede suscribirse.
 *   - setApplicationDestinationPrefixes("/app") = el buzón donde los oyentes DEJAN mensajes
 *                                                 para que la radio los procese y reemita.
 *
 * FLUJO COMPLETO:
 *   Cliente ---(envía a /app/chat.send)---> ChatController.sendMessage()
 *   ChatController ---(devuelve, @SendTo("/topic/messages"))---> Broker
 *   Broker ---(broadcast)---> Todos los clientes suscritos a /topic/messages
 *
 * ANTES (servlet 2.x, año ~2010): había que usar Comet / long polling — el navegador
 *   preguntaba cada X segundos "¿hay algo nuevo?". Ineficiente y con latencia.
 * AHORA: WebSocket = conexión bidireccional persistente. El server EMPUJA (push) al cliente.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registra los endpoints STOMP — las URLs por donde los clientes abren la conexión WebSocket.
     *
     * PREGUNTA DE ALUMNO — "¿qué es SockJS?"
     *   SockJS es un "plan B" para navegadores/proxies antiguos que bloquean WebSocket nativo.
     *   Si el WebSocket falla, SockJS cae automáticamente a long-polling. Transparente para el código.
     *
     * @param registry receptor donde declaramos los endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // El cliente hará: new SockJS("http://localhost:8080/ws")
        //                   new Stomp.over(sock)
        registry.addEndpoint("/ws")   // URL de handshake
                .withSockJS();         // Habilita fallback SockJS
    }

    /**
     * Configura el broker de mensajes in-memory (simple, sin RabbitMQ ni ActiveMQ).
     *
     * PREGUNTA DE ALUMNO — "¿qué diferencia hay entre /app y /topic?"
     *   - /app     = destinos "entrantes". Los mensajes que el CLIENTE envía al servidor
     *                pasan por acá y los procesa un @MessageMapping del controller.
     *   - /topic   = destinos "salientes" (broadcast). Todo cliente suscrito recibe copia.
     *
     * @param registry receptor donde declaramos los prefijos y el broker simple.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Broker en memoria: cualquier @SendTo("/topic/...") o convertAndSend("/topic/...")
        // se propaga a todos los suscritos. Perfecto para demos y baja escala.
        // Para producción con miles de conexiones se usaría un broker externo (RabbitMQ STOMP relay).
        registry.enableSimpleBroker("/topic");

        // Los mensajes que llegan del cliente con destino "/app/..." se ruteán a los @MessageMapping.
        registry.setApplicationDestinationPrefixes("/app");
    }
}
