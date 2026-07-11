// Package raiz del modulo 29. Debe coincidir con la carpeta fisica.
// PREGUNTA DE ALUMNO — "¿por que el package empieza en 'com.springroadmap'?"
//   Convencion de la industria: dominio invertido para evitar choques de
//   nombres cuando varias librerias declaran una clase con el mismo nombre.
package com.springroadmap.cloudconfig;

// 'import' trae clases desde otras librerias.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada del modulo 29 — Configuracion externalizada.
 *
 * <p><b>Contexto:</b> El modulo 29 del roadmap propone montar un
 * <i>Spring Cloud Config Server</i> con repositorio Git. Sin embargo,
 * a la fecha del modulo (Spring Boot 4.1.0), Spring Cloud <b>aun no tiene
 * un release compatible</b> con Boot 4 (Spring Cloud 2024.x/2025.x se
 * mantienen sobre Boot 3.3-3.4).
 *
 * <p><b>Variante simplificada:</b> Este modulo demuestra el patron
 * <i>Externalized Configuration</i> usando solo Spring Boot puro:
 * <ul>
 *   <li>{@link FeatureFlags} con {@code @ConfigurationProperties} lee
 *       valores desde {@code application.yml}.</li>
 *   <li>{@link FeatureController} expone {@code GET /api/features} para
 *       ver la configuracion vigente.</li>
 *   <li>Actuator expone (documentalmente) {@code POST /actuator/refresh}
 *       — que en un Config Server real recargaria en caliente los beans
 *       anotados con {@code @RefreshScope}.</li>
 * </ul>
 *
 * <p>Analogia: si tu aplicacion es un edificio, la configuracion es
 * el manual del administrador. En vez de tener el manual pegado a la
 * pared (hardcoded), lo pones en una carpeta compartida
 * ({@code application.yml}) para poder actualizarlo sin demoler.
 *
 * <hr>
 * <b>ANTES (Java 8) vs AHORA (Java 21)</b>
 * <pre>
 * // ANTES: cada valor se leia con Properties + FileInputStream a mano.
 * Properties p = new Properties();
 * p.load(new FileInputStream("app.properties"));
 * String pwd = p.getProperty("db.password");
 *
 * // AHORA: @ConfigurationProperties enlaza el YAML a un objeto tipado.
 * &#64;ConfigurationProperties(prefix = "app.features")
 * public class FeatureFlags { ... }
 * </pre>
 */
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
// @ConfigurationPropertiesScan detecta clases anotadas con
// @ConfigurationProperties sin necesidad de registrarlas una por una.
@ConfigurationPropertiesScan
public class CloudConfigApplication {

    // main: la JVM invoca este metodo al lanzar 'java -jar spring-cloud-config-1.0.0.jar'.
    //   - 'public'  -> visible desde afuera.
    //   - 'static'  -> no requiere instancia.
    //   - 'void'    -> no devuelve nada.
    //   - String[]  -> argumentos de linea de comando.
    public static void main(String[] args) {
        // Arranca el contenedor Spring: crea beans, aplica autoconfiguracion,
        // levanta Tomcat en 8080, y queda escuchando peticiones HTTP.
        SpringApplication.run(CloudConfigApplication.class, args);
    }
}
