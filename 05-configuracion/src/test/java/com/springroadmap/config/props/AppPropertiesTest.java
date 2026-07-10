package com.springroadmap.config.props;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que Spring Boot BINDEA correctamente el bloque `app.*` del YAML
 * a la instancia de {@link AppProperties}.
 *
 * Usamos @SpringBootTest con la opción `properties = { ... }` para inyectar
 * valores de prueba en el Environment ANTES de que arranque el contexto.
 * Esto sobrescribe application.yml sin necesidad de perfiles.
 */
@SpringBootTest(properties = {
        "app.name=TestApp",
        "app.version=9.9.9",
        "app.features.email-enabled=true",
        "app.features.max-users=42"
})
class AppPropertiesTest {

    // Autowired en campo SOLO en tests (aceptable porque los tests no se
    // instancian con `new`). En código de producción usar constructor injection.
    @Autowired
    private AppProperties appProperties;

    @Test
    void bindeaLasClavesAppDelYamlEnElRecord() {
        assertThat(appProperties.name()).isEqualTo("TestApp");
        assertThat(appProperties.version()).isEqualTo("9.9.9");
        assertThat(appProperties.features().emailEnabled()).isTrue();
        assertThat(appProperties.features().maxUsers()).isEqualTo(42);
    }
}
