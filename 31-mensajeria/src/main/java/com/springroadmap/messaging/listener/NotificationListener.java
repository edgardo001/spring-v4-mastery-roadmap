package com.springroadmap.messaging.listener;

import com.springroadmap.messaging.event.OrderCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * NotificationListener - SUSCRIPTOR que reacciona a OrderCreatedEvent.
 *
 * Analogia del mundo real:
 *   Es el mensajero que ESTA sentado en el pasillo de la notaria (del ejemplo
 *   de OrderService). Cuando escucha "¡Contrato 42 registrado!", saca su libreta
 *   y anota una linea. Le da igual quien lo grito y por que; solo reacciona.
 *
 * @Component : bean Spring generico (no es servicio, ni controller, ni repo).
 * @EventListener : marca el metodo como oyente del tipo de evento que
 *                  recibe como parametro (aqui, OrderCreatedEvent).
 *
 * PREGUNTA DE ALUMNO - "¿El @EventListener corre en la misma hebra que el publisher?"
 *   R: Por defecto SI (sincrono). Si quieres asincronia agregas @Async al metodo
 *      y @EnableAsync en la clase de configuracion. En este demo lo dejamos
 *      sincrono para que los tests sean deterministicos.
 */
@Component
public class NotificationListener {

    // AtomicInteger = contador entero SEGURO entre hilos.
    // Lo usamos para que los tests puedan comprobar cuantos eventos llegaron.
    // ANTES (Java 8): un 'int' con 'synchronized' o 'volatile' + get/set manuales.
    // AHORA:          AtomicInteger + incrementAndGet() atomico.
    private final AtomicInteger receivedCount = new AtomicInteger(0);

    /**
     * Se dispara automaticamente cada vez que OrderService publica un
     * OrderCreatedEvent. Spring hace el routing por el tipo del parametro.
     */
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        // Incremento atomico del contador.
        receivedCount.incrementAndGet();

        // En un caso real aqui enviarias un email/SMS/push notification.
        // Para el demo solo dejamos un log en stdout.
        System.out.println("[NotificationListener] Recibido pedido #"
                + event.orderId() + " para cliente " + event.customer());
    }

    /**
     * Getter usado por los tests para verificar cuantos eventos se recibieron.
     */
    public int getReceivedCount() {
        return receivedCount.get();
    }

    /**
     * Reinicia el contador. Util entre tests para partir de cero.
     */
    public void reset() {
        receivedCount.set(0);
    }
}
