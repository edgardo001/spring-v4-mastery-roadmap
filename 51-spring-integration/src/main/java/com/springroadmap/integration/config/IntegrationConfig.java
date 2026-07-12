package com.springroadmap.integration.config;

import com.springroadmap.integration.domain.Order;
// @Configuration marca la clase como fuente de beans para el contenedor de Spring.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// DirectChannel = canal síncrono punto-a-punto (el hilo del sender ejecuta el receiver).
import org.springframework.integration.channel.DirectChannel;
// IntegrationFlow y su DSL: forma declarativa de conectar canales, transformers y handlers.
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

/**
 * `IntegrationConfig` - Configuración central del flujo de integración.
 *
 * <h2>¿Qué construye?</h2>
 * <ul>
 *   <li><b>orderInput</b>: canal DirectChannel donde el @MessagingGateway deposita los mensajes.</li>
 *   <li><b>orderProcessingFlow</b>: el pipeline que:
 *     <ol>
 *       <li>Escucha `orderInput`.</li>
 *       <li>Transforma el `Order` a un `String` descriptivo (transformer).</li>
 *       <li>Lo procesa con un handler que devuelve la respuesta final.</li>
 *     </ol>
 *   </li>
 * </ul>
 *
 * <h2>Analogía</h2>
 * Es el <b>plano arquitectónico</b> de la fábrica de mensajes. Aquí decides qué
 * cinta va con qué máquina.
 *
 * <h2>ANTES (Java 8 + Spring 3) vs AHORA (Java 21 + DSL)</h2>
 * <pre>
 * // ANTES: XML de 40 líneas con &lt;int:channel/&gt;, &lt;int:transformer/&gt;, &lt;int:service-activator/&gt;.
 * &lt;int:channel id="orderInput"/&gt;
 * &lt;int:transformer input-channel="orderInput" output-channel="afterTransform"
 *                  ref="orderTransformer" method="toDescription"/&gt;
 * &lt;int:service-activator input-channel="afterTransform" ref="orderHandler" method="handle"/&gt;
 *
 * // AHORA: DSL Java tipado con lambdas.
 * IntegrationFlow.from("orderInput")
 *     .transform((Order o) -&gt; "Procesando: " + o.product() + " x" + o.quantity())
 *     .handle((payload, headers) -&gt; "OK - " + payload)
 *     .get();
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿Por qué DirectChannel y no QueueChannel?"
 * R: DirectChannel es <b>síncrono</b>: el mismo hilo que envía ejecuta el flujo
 * completo y devuelve la respuesta. Perfecto para request/response desde un
 * controller REST. QueueChannel es asíncrono con buffer (fire-and-forget),
 * pero entonces el gateway no puede retornar un resultado inmediato.</p>
 */
@Configuration
public class IntegrationConfig {

    /**
     * Canal de entrada donde el @MessagingGateway `OrderGateway` deposita mensajes.
     * El nombre del bean ("orderInput") debe coincidir EXACTAMENTE con el
     * `defaultRequestChannel` declarado en la interfaz OrderGateway.
     */
    @Bean
    public MessageChannel orderInput() {
        // `new DirectChannel()` = canal síncrono; el envío bloquea hasta que el handler responde.
        return new DirectChannel();
    }

    /**
     * Flujo principal: orderInput -> transformer -> handler -> respuesta al gateway.
     *
     * <p>Nota sobre lambdas y method references:</p>
     * <ul>
     *   <li><code>(Order o) -> "..."</code> es una lambda: función anónima corta.</li>
     *   <li>Equivale (ANTES, Java 8) a: <code>new GenericTransformer&lt;Order,String&gt;() { public String transform(Order o) { return "..."; } }</code></li>
     * </ul>
     */
    @Bean
    public IntegrationFlow orderProcessingFlow() {
        // IntegrationFlow.from(...) = origen del flujo (nombre del canal).
        return IntegrationFlow.from("orderInput")
                // .transform() = aplica una función al payload. Aquí convertimos Order en String descriptivo.
                .transform((Order order) ->
                        "Procesando orden " + order.id()
                        + " - producto=" + order.product()
                        + " cantidad=" + order.quantity())
                // .handle() = endpoint terminal. Recibe el payload (String) y headers, retorna la respuesta.
                // Como es DirectChannel síncrono, lo retornado aquí vuelve al llamador del gateway.
                .handle((payload, headers) -> "OK - " + payload)
                // .get() finaliza la construcción del IntegrationFlow y lo entrega al contenedor.
                .get();
    }
}
