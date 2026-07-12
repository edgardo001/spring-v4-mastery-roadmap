package com.springroadmap.integration.gateway;

import com.springroadmap.integration.domain.Order;
// @MessagingGateway convierte esta INTERFAZ en un proxy que envía Message al canal
// indicado en `defaultRequestChannel`. NO tienes que implementarla tú.
import org.springframework.integration.annotation.MessagingGateway;

/**
 * `OrderGateway` - Puerta de entrada al flujo de integración.
 *
 * <h2>Propósito</h2>
 * Oculta la mensajería. El resto de la aplicación (Controllers, Services) invoca
 * `gateway.process(order)` como si fuera un método normal, sin saber que por debajo:
 *   1. Se crea un `Message<Order>`.
 *   2. Se envía al canal `"orderInput"`.
 *   3. Recorre el `IntegrationFlow` (transform + handle).
 *   4. La respuesta viaja de vuelta y se retorna al llamador.
 *
 * <h2>Analogía</h2>
 * Es el <b>botón "enviar"</b> de una máquina de correos automatizada. Tú aprietas
 * el botón; no ves las cintas transportadoras internas.
 *
 * <h2>ANTES (Java 8 + Spring 3) vs AHORA</h2>
 * <pre>
 * // ANTES: XML gigante en integration.xml declarando gateways, channels y beans
 * &lt;int:gateway id="orderGateway"
 *              service-interface="com.demo.OrderGateway"
 *              default-request-channel="orderInput"/&gt;
 *
 * // AHORA: una sola anotación en la interfaz Java. Todo tipado, sin XML.
 * &#64;MessagingGateway(defaultRequestChannel = "orderInput")
 * public interface OrderGateway { String process(Order order); }
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿Cómo hace Spring para implementar una interfaz sola?"
 * R: Usa un <b>proxy dinámico</b> (java.lang.reflect.Proxy). En tiempo de arranque,
 * escanea las interfaces con @MessagingGateway y genera en memoria una clase que
 * implementa cada método enviando un Message al canal declarado. Nunca ves esa
 * clase, pero es un bean de Spring como cualquier otro.</p>
 */
@MessagingGateway(defaultRequestChannel = "orderInput")
public interface OrderGateway {

    /**
     * Procesa una orden a través del flujo de integración.
     *
     * @param order la orden a procesar (se convierte en `Message<Order>`).
     * @return el resultado final del flujo (payload del `Message` de respuesta).
     */
    String process(Order order);
}
