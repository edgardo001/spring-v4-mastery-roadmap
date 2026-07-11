package com.springroadmap.i18n;

// SpringApplication es la clase utilitaria que arranca el contenedor de Spring
// (crea beans, configura el servidor web embebido Tomcat, etc.).
import org.springframework.boot.SpringApplication;
// @SpringBootApplication es una meta-anotación que combina:
//   - @Configuration (declara la clase como fuente de beans)
//   - @EnableAutoConfiguration (activa la autoconfiguración de Spring Boot)
//   - @ComponentScan (escanea el paquete actual y subpaquetes en busca de beans)
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del módulo 37 - Internacionalización (i18n).
 *
 * Analogía del mundo real:
 *   Imagina un menú de un restaurante turístico. La comida es la misma, pero
 *   el mesero (Spring) te trae el menú en español, inglés o francés según el
 *   idioma que hables. El "menú" son los archivos messages_*.properties, el
 *   mesero es MessageSource, y "qué idioma hablas" lo decide LocaleResolver
 *   leyendo el header HTTP Accept-Language.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - ANTES: public static void main(String[] args) { ... } (idéntico)
 *   - AHORA: idéntico. No hay diferencia en el arranque.
 */
@SpringBootApplication
public class I18nApplication {

    /**
     * Punto de entrada estándar de Java.
     * "static" = pertenece a la clase, no a una instancia (no necesitas new).
     * "void"   = no devuelve nada.
     * "String[] args" = argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        // Arranca todo el contenedor Spring + Tomcat embebido en el puerto 8080.
        SpringApplication.run(I18nApplication.class, args);
    }
}
