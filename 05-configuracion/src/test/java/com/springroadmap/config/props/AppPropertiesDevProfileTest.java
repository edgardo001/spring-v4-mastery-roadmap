package com.springroadmap.config.props;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que al activar el perfil `dev`, los valores de
 * application-dev.yml SOBRESCRIBEN a los de application.yml.
 *
 * @ActiveProfiles("dev") le dice a Spring que arranque el contexto como si
 * hubiéramos ejecutado la app con SPRING_PROFILES_ACTIVE=dev.
 */
@SpringBootTest
@ActiveProfiles("dev")
class AppPropertiesDevProfileTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    void perfilDev_sobrescribeNombreYFeatureFlags() {
        // Sobrescrito por application-dev.yml.
        assertThat(appProperties.name()).isEqualTo("Configuracion Roadmap (DEV)");
        // NO sobrescrito por application-dev.yml -> hereda de application.yml.
        assertThat(appProperties.version()).isEqualTo("1.0.0");
        // Overrides del perfil dev.
        assertThat(appProperties.features().emailEnabled()).isTrue();
        assertThat(appProperties.features().maxUsers()).isEqualTo(10);
    }
}
