package com.springroadmap.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de endpoints públicos.
 *
 * <p><b>Propósito:</b> exponer una URL a la que cualquiera pueda acceder
 * sin autenticarse. Es el equivalente al mostrador de información de
 * una empresa: abierto al público.</p>
 *
 * <p><b>Palabras clave:</b>
 * <ul>
 *   <li>{@code @RestController} — combina {@code @Controller} + {@code @ResponseBody};
 *       cada método devuelve el cuerpo HTTP directamente.</li>
 *   <li>{@code @RequestMapping} — prefijo común de rutas.</li>
 *   <li>{@code @GetMapping} — atajo para {@code @RequestMapping(method = GET)}.</li>
 * </ul>
 * </p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES (Spring MVC clásico + web.xml):
 * //   Se declaraba un DispatcherServlet en web.xml y las restricciones
 * //   públicas iban en &lt;security-constraint&gt; con &lt;url-pattern&gt;.
 * // AHORA:
 * //   El endpoint no sabe de seguridad; SecurityFilterChain decide.
 * </pre>
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    /**
     * Retorna un saludo público simple.
     *
     * @return la cadena {@code "public"}
     */
    @GetMapping("/hello")
    public String hello() {
        return "public";
    }
}
