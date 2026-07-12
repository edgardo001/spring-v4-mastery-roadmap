package com.springroadmap.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.springroadmap.ai.config.LlmProperties;

/**
 * Punto de entrada de la aplicacion "AI Integration".
 *
 * Analogia: es el "arranque de la caja" — Spring Boot escanea las
 * anotaciones, prepara el contenedor de beans y expone el servidor HTTP.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *  - Antes: public static void main(String[] args) {
 *              new SpringApplicationBuilder(App.class).run(args);
 *           }
 *  - Ahora: exactamente igual — main(String[]) sigue siendo la firma
 *           estandar. La modernidad esta en el resto del stack (records,
 *           virtual threads, RestClient), no en el arranque.
 */
@SpringBootApplication
// `@EnableConfigurationProperties` registra LlmProperties como bean para
// que Spring rellene sus campos desde `application.yml` (prefix "llm").
@EnableConfigurationProperties(LlmProperties.class)
public class AiIntegrationApplication {

    // `main` = metodo estatico que la JVM ejecuta primero.
    // `String[] args` = argumentos que llegan desde la linea de comandos.
    public static void main(String[] args) {
        SpringApplication.run(AiIntegrationApplication.class, args);
    }
}
