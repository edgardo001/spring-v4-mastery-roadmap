package com.springroadmap.modulith.orders;

import com.springroadmap.modulith.notifications.NotificationListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion entre modulos.
 * Verifica que la comunicacion por eventos funciona: orders publica,
 * notifications reacciona (sin dependencia directa en el codigo).
 */
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private NotificationListener notificationListener;

    @Test
    void createOrder_disparaEventoYNotifica() {
        int previo = notificationListener.getNotified();

        Order order = orderService.createOrder("Ana");

        assertThat(order.id()).isNotNull();
        assertThat(order.customer()).isEqualTo("Ana");
        assertThat(notificationListener.getNotified()).isGreaterThanOrEqualTo(previo + 1);
    }
}
