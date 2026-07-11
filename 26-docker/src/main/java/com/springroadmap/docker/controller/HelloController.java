package com.springroadmap.docker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST minimo: expone un unico endpoint que sirve como
 * "prueba de vida" (health-check informal) para verificar que el
 * contenedor Docker esta corriendo y respondiendo peticiones HTTP.
 *
 * Analogia:
 *   Es como el pin de "listo" en una impresora. No hace nada util
 *   por si mismo, pero si responde con la frase esperada sabemos que
 *   la maquina esta encendida y conectada a la red.
 *
 * PREGUNTA DE ALUMNO - "Por que este modulo tiene un solo endpoint?"
 *   Porque el objetivo pedagogico del modulo 26 NO es aprender
 *   Spring MVC (eso ya se vio en el modulo 04) sino aprender a
 *   contenerizar. Un endpoint simple es suficiente para comprobar
 *   que la imagen Docker construida funciona.
 *
 * PREGUNTA DE ALUMNO - "Como llega la peticion desde mi navegador
 *   hasta este metodo cuando la app corre en Docker?"
 *   Flujo completo:
 *     1. Escribes  http://localhost:8080/api/hello  en el navegador.
 *     2. El puerto 8080 de tu PC esta mapeado al puerto 8080 del
 *        contenedor (por el flag  -p 8080:8080  de "docker run").
 *     3. Tomcat embebido dentro del contenedor recibe la peticion.
 *     4. Spring MVC busca que @GetMapping coincide con "/api/hello".
 *     5. Invoca este metodo y devuelve el String como cuerpo HTTP.
 *
 * ============================================================
 * ANTES (Java 8 / Servlets) vs AHORA (Java 21 / Spring MVC)
 * ============================================================
 * ANTES (Servlet clasico + web.xml):
 *   public class HelloServlet extends HttpServlet {
 *       protected void doGet(HttpServletRequest req, HttpServletResponse resp)
 *               throws IOException {
 *           resp.setContentType("text/plain");
 *           resp.getWriter().write("Hello from Docker container");
 *       }
 *   }
 *   // + registrar el servlet en /WEB-INF/web.xml con <servlet-mapping>.
 *
 * AHORA:
 *   Un metodo Java con @GetMapping. Spring se encarga del resto.
 */
@RestController
public class HelloController {

    /**
     * Endpoint GET /api/hello.
     *
     * @GetMapping("/api/hello") le dice a Spring MVC:
     *   "cuando llegue una peticion HTTP GET a /api/hello, invoca este
     *    metodo y usa lo que retorne como cuerpo de la respuesta".
     *
     * El String devuelto se envia como text/plain (sin conversion JSON)
     * porque Spring detecta que no es un objeto complejo.
     *
     * El texto EXACTO contiene "Docker container" para que el test
     * automatizado (HelloControllerTest) pueda validarlo con un
     * matcher "contains".
     */
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Docker container";
    }
}
