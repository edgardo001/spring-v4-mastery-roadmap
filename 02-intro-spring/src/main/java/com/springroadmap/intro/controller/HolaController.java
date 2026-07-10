package com.springroadmap.intro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador REST (punto de entrada HTTP de la aplicación).
 *
 * PREGUNTA DE ALUMNO — "¿Qué es un 'endpoint'?"
 *   Un endpoint es la combinación de un MÉTODO HTTP (GET, POST, ...) más
 *   una URL a la que un cliente (navegador, app, curl) puede llamar.
 *   Ejemplo: GET http://localhost:8080/api/hola  ← ese es un endpoint.
 *
 * PREGUNTA DE ALUMNO — "¿Qué hace @RestController?"
 *   Es una anotación que combina dos:
 *     1. @Controller     -> esta clase maneja peticiones HTTP (Spring la registra
 *                           como bean web).
 *     2. @ResponseBody   -> lo que devuelvan sus métodos se envía DIRECTAMENTE
 *                           como cuerpo de la respuesta HTTP (texto o JSON),
 *                           en lugar de intentar renderizar una vista HTML.
 *
 * PREGUNTA DE ALUMNO — "¿Por qué no hay 'new HolaController(...)' en ningún lado?"
 *   Spring lo crea por ti automáticamente. Como la clase está anotada con
 *   @RestController, el @ComponentScan la detecta al arrancar, la instancia
 *   una sola vez y la guarda como bean. Cuando llega una petición HTTP,
 *   Spring busca qué controller la atiende y usa esa instancia compartida.
 *   Este mecanismo se llama "Inversión de Control" (IoC) y lo estudiaremos
 *   en detalle en el módulo 03.
 *
 * Analogía: este controlador es el "recepcionista" del hotel. Se sienta en
 * la puerta esperando llamadas HTTP y, según a qué habitación pregunten
 * (/api/hola, /api/hora), da la respuesta adecuada.
 */
@RestController
public class HolaController {

    /**
     * Endpoint principal: GET /api/hola.
     *
     * @GetMapping("/ruta") le dice a Spring:
     *   "Cuando llegue una petición HTTP GET a la ruta indicada, invoca
     *    este método y usa lo que retorne como cuerpo de la respuesta".
     *
     * El String que devolvemos se envía tal cual al cliente con
     * Content-Type: text/plain (Spring detecta que no es un objeto complejo).
     */
    @GetMapping("/api/hola")
    public String saludar() {
        return "¡Hola Mundo desde Spring Boot 4!";
    }

    /**
     * Segundo endpoint (refuerzo pedagógico): GET /api/hora.
     *
     * Demuestra que un mismo @RestController puede exponer VARIOS métodos
     * HTTP con distintas rutas. Cada método es un endpoint independiente.
     *
     * LocalDateTime.now() obtiene la fecha y hora actual del servidor.
     * Al convertirla a String, Spring la envía como texto plano.
     */
    @GetMapping("/api/hora")
    public String horaActual() {
        return "Hora del servidor: " + LocalDateTime.now();
    }
}
