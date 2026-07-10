package com.springroadmap.di.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de notificaciones.
 *
 * PREGUNTA DE ALUMNO — "¿Qué hace @Service exactamente?"
 *   Es una variante de @Component. Le dice a Spring:
 *     "Crea una instancia única de esta clase al arrancar y guárdala
 *      en el contenedor IoC. Cuando alguien pida esta clase por
 *      constructor, dale ESTA instancia."
 *   La diferencia con @Component es SEMÁNTICA (para humanos): @Service
 *   comunica intención de "capa de negocio". Técnicamente son iguales.
 *
 * Analogía: es como marcar a un empleado como "chef" en un restaurante.
 * El sistema sabe que un chef pertenece a la cocina y no al salón, aunque
 * ambos son empleados. Facilita organizar el equipo.
 *
 * =====================================================================
 * ANTES (Java 8 clásico) vs AHORA (Spring)
 * =====================================================================
 * ANTES: cada Controller creaba su propio NotificationService con `new`,
 * lo cual multiplicaba instancias inútilmente y hacía imposible cambiar
 * la implementación sin editar código.
 *
 *   public class UserController {
 *       private final NotificationService svc = new NotificationService();
 *   }
 *
 * AHORA: una sola instancia gestionada por Spring, inyectada por
 * constructor. El Controller NO sabe cómo se construye el Service.
 */
@Service
public class NotificationService {

    // @Slf4j de Lombok haría esto en una sola línea; aún no usamos Lombok
    // en el módulo 03 para que el alumno vea el patrón explícito.
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    /**
     * "Envía" un email (mock: solo registra en log y devuelve un string).
     *
     * En un sistema real este método hablaría con un servidor SMTP.
     * Aquí devolvemos un string para que los tests puedan verificar
     * el comportamiento sin depender de red.
     *
     * @param to destinatario del email
     * @param msg contenido del email
     * @return marcador determinista "EMAIL_SENT_TO:<to>"
     */
    public String sendEmail(String to, String msg) {
        // Buena práctica de seguridad (agente Seguridad): NO logueamos el
        // email ni el contenido en INFO (PII risk). Solo la longitud del
        // mensaje en DEBUG para trazabilidad sin filtrar datos personales.
        log.debug("Sending notification (msgLength={})", msg == null ? 0 : msg.length());
        return "EMAIL_SENT_TO:" + to;
    }
}
