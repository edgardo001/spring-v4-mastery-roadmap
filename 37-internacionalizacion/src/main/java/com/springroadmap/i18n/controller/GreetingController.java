package com.springroadmap.i18n.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST que devuelve un saludo internacionalizado.
 *
 * Endpoint:
 *   GET /api/greet?name=Ada
 *   Header opcional: Accept-Language: en | es | fr
 *
 * Analogía del mundo real:
 *   Es como llamar a un call center global: dices "hola" en tu idioma y el
 *   operador te responde en ese mismo idioma.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - ANTES: `new Object[]{name}` para varargs no tipados.
 *   - AHORA: puedes seguir usando ese literal — no cambia. Pero si armas mensajes
 *            más complejos, `List.of(name)` + `toArray()` es equivalente moderno.
 */
@RestController
@RequestMapping("/api")
public class GreetingController {

    // Constructor injection (recomendado por Spring 4+):
    // el campo es 'final' — no puede reasignarse tras construir el objeto.
    private final MessageSource messageSource;

    // Spring detecta este constructor y pasa el bean MessageSource automáticamente.
    public GreetingController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Devuelve el saludo en el idioma del request.
     *
     * @param name   nombre a saludar (query param, default "amigo").
     * @param locale Spring lo resuelve vía LocaleResolver leyendo Accept-Language.
     *               PREGUNTA DE ALUMNO — "¿de dónde sale el Locale?"
     *                 Cuando declaras un parámetro `Locale` en un método de
     *                 controller, Spring lo inyecta automáticamente usando el
     *                 LocaleResolver que registramos en I18nConfig.
     */
    @GetMapping("/greet")
    public String greet(
            @RequestParam(name = "name", defaultValue = "amigo") String name,
            Locale locale) {
        // getMessage(clave, args, locale):
        //   - "greeting" es la clave que existe en messages_*.properties.
        //   - new Object[]{name} son los argumentos que rellenan el {0}.
        //   - locale decide qué archivo leer.
        return messageSource.getMessage("greeting", new Object[]{name}, locale);
    }
}
