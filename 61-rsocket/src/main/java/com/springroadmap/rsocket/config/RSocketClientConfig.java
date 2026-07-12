package com.springroadmap.rsocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

/**
 * Configura un {@link RSocketRequester} apuntando al servidor RSocket local (localhost:7000).
 *
 * <p>Se usa desde {@code RestBridge} para reenviar una peticion HTTP hacia RSocket.
 * En un caso real, el cliente RSocket viviria en OTRA aplicacion; aqui es el mismo proceso
 * solo por simplicidad pedagogica.</p>
 */
@Configuration
public class RSocketClientConfig {

    private final int rsocketPort;

    // PREGUNTA DE ALUMNO - "que hace @Value?"
    //   Inyecta el valor de una propiedad de application.yml en un campo/parametro.
    public RSocketClientConfig(@Value("${spring.rsocket.server.port:7000}") final int rsocketPort) {
        this.rsocketPort = rsocketPort;
    }

    /**
     * Bean del cliente RSocket. {@link RSocketRequester.Builder} viene autoconfigurado
     * por Spring Boot cuando esta {@code spring-boot-starter-rsocket} en el classpath.
     */
    @Bean
    public RSocketRequester rsocketRequester(final RSocketRequester.Builder builder) {
        return builder.tcp("localhost", rsocketPort);
    }
}
