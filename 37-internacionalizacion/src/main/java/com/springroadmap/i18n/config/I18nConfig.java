package com.springroadmap.i18n.config;

import java.util.Locale;

// @Bean marca un método cuyo objeto devuelto será gestionado por Spring.
import org.springframework.context.annotation.Bean;
// @Configuration indica que esta clase contiene definiciones de beans.
import org.springframework.context.annotation.Configuration;
// MessageSource es la interfaz que Spring usa para resolver textos por clave + Locale.
import org.springframework.context.MessageSource;
// ReloadableResourceBundleMessageSource permite recargar los .properties sin reiniciar.
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
// LocaleResolver decide qué idioma usar en cada request HTTP.
import org.springframework.web.servlet.LocaleResolver;
// AcceptHeaderLocaleResolver lee el header HTTP "Accept-Language" del cliente.
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Configuración de internacionalización (i18n) del módulo.
 *
 * Analogía del mundo real:
 *   Este archivo es el "diccionario multi-idioma" del restaurante.
 *   - MessageSource   = el diccionario físico (mapea "greeting" a "Hola" o "Hello").
 *   - LocaleResolver  = el mesero que te pregunta (o adivina) qué idioma hablas.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - ANTES: `Locale defaultLocale = new Locale("es")` — constructor deprecated hoy.
 *   - AHORA: `Locale.forLanguageTag("es")` o `Locale.of("es")` (Java 19+).
 *   - ANTES: los .properties se cargaban con ResourceBundle a mano.
 *   - AHORA: Spring lo hace vía `@Bean MessageSource`.
 */
@Configuration
public class I18nConfig {

    /**
     * Bean MessageSource:
     *   - Apunta a "classpath:messages/messages", es decir, Spring buscará:
     *       messages/messages.properties        (fallback / default)
     *       messages/messages_es.properties     (español)
     *       messages/messages_en.properties     (inglés)
     *       messages/messages_fr.properties     (francés)
     *   - defaultEncoding UTF-8 evita que caracteres acentuados salgan como "??".
     *
     * PREGUNTA DE ALUMNO — "¿por qué 'Reloadable' y no el normal?"
     *   Porque en desarrollo puedes cambiar un .properties y ver el nuevo texto
     *   sin reiniciar el server. En producción funciona igual pero cachea.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        // "classpath:" = busca dentro del jar, en src/main/resources/.
        ms.setBasename("classpath:messages/messages");
        ms.setDefaultEncoding("UTF-8");
        // Si una clave no se encuentra en el Locale pedido, cae al default (messages.properties).
        ms.setFallbackToSystemLocale(false);
        // Cache: -1 = para siempre; 0 = recargar cada vez. 10 seg es un balance razonable.
        ms.setCacheSeconds(10);
        return ms;
    }

    /**
     * Bean LocaleResolver:
     *   AcceptHeaderLocaleResolver mira el header HTTP "Accept-Language" que
     *   envía el navegador y lo convierte en un java.util.Locale.
     *
     *   - Si el cliente manda "Accept-Language: fr" → Locale.FRENCH.
     *   - Si no manda header alguno → usa el defaultLocale que le fijemos aquí.
     *
     * PREGUNTA DE ALUMNO — "¿y si el cliente manda un idioma que no tengo?"
     *   Spring cae al defaultLocale (español en nuestro caso), y si tampoco
     *   existe la clave allí, cae al messages.properties genérico.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        // Locale.of(...) es el reemplazo moderno de "new Locale(...)".
        resolver.setDefaultLocale(Locale.of("es"));
        return resolver;
    }
}
