package com.springroadmap.messaging.service;

import com.springroadmap.messaging.listener.NotificationListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion del flujo publish/subscribe.
 *
 * Usa @SpringBootTest para levantar el contexto REAL: cuando OrderService
 * publica el evento, Spring lo entrega a NotificationListener (mismo contexto)
 * y podemos verificar el contador.
 *
 * IMPORTANTE (regla del proyecto - Boot 4.1.0):
 *   NO usamos @DataJpaTest, @WebMvcTest ni TestRestTemplate (fueron ELIMINADOS).
 *   Aqui @SpringBootTest es el patron correcto.
 */
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private NotificationListener notificationListener;

    @BeforeEach
    void resetCounter() {
        // Reiniciamos el contador para que cada test sea independiente.
        notificationListener.reset();
    }

    @Test
    void createOrderPublishesEventAndListenerIncrementsCounter() {
        // Estado inicial: el listener no ha recibido nada.
        assertThat(notificationListener.getReceivedCount()).isZero();

        // Cuando creamos un pedido...
        Long orderId = orderService.createOrder("Juan Perez");

        // ...el service devuelve un ID valido...
        assertThat(orderId).isNotNull();
        assertThat(orderId).isPositive();

        // ...y el listener fue notificado exactamente UNA vez.
        // (Sincrono por defecto: cuando publishEvent() retorna, el listener ya corrio).
        assertThat(notificationListener.getReceivedCount()).isEqualTo(1);
    }

    @Test
    void multipleOrdersProduceMultipleNotifications() {
        orderService.createOrder("Cliente A");
        orderService.createOrder("Cliente B");
        orderService.createOrder("Cliente C");

        assertThat(notificationListener.getReceivedCount()).isEqualTo(3);
    }
}
