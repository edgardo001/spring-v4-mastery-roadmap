package com.springroadmap.docs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la spec OpenAPI 3.1 generada por springdoc.
 *
 * springdoc buscará un @Bean OpenAPI en el contexto y lo usará como base
 * para el documento resultante en /v3/api-docs. Aquí seteamos title,
 * version y description, que son los campos que se muestran en el
 * encabezado de Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Books API")
                        .version("1.0")
                        .description("API de ejemplo del módulo 15 del Spring Roadmap. "
                                + "Documentada automáticamente con springdoc-openapi (OpenAPI 3.1)."));
    }
}
