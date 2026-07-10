// Paquete raíz del módulo 05.
package com.springroadmap.config;

// PREGUNTA DE ALUMNO — "¿qué es `import`?"
//   Sirve para traer clases desde otros paquetes y usarlas por su nombre
//   corto (SpringApplication en vez de org.springframework.boot.SpringApplication).
import com.springroadmap.config.props.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Punto de entrada de la aplicación del módulo 05 (Configuración).
 *
 * ¿Qué hace esta clase? Arranca el contenedor de Spring (ApplicationContext),
 * lee los archivos application*.yml y "cablea" todos los @Component/@Service
 * detectados por escaneo de componentes.
 *
 * Analogía: es el "botón de encendido" de una radio. Al pulsarlo (main),
 * la radio (Spring) se enciende, sintoniza la frecuencia (perfil activo),
 * y conecta los altavoces (beans) sin que tengas que soldar cables.
 *
 * =====================================================================
 * ANTES (Java 8 + Spring "clásico" XML)
 * =====================================================================
 * // beans.xml:
 * // <beans>
 * //   <bean id="appProps" class="AppProperties">
 * //     <property name="name" value="..."/>
 * //   </bean>
 * // </beans>
 * // Main:
 * // ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
 * // No había perfiles nativos ni type-safety.
 *
 * =====================================================================
 * AHORA (Java 21 + Spring Boot 4.1)
 * =====================================================================
 * Una sola clase con @SpringBootApplication + main. El YAML reemplaza al
 * XML y @ConfigurationProperties da type-safety.
 *
 * PREGUNTA DE ALUMNO — "¿por qué @EnableConfigurationProperties?"
 *   Registra el record AppProperties como bean gestionado por Spring.
 *   Alternativa equivalente: anotar esta clase con @ConfigurationPropertiesScan
 *   (el escáner descubre todas las clases con @ConfigurationProperties).
 *   Elegimos la forma EXPLÍCITA para que el alumno vea el enlace uno-a-uno.
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ConfiguracionApplication {

    /**
     * Método `main` estándar de Java. La palabra `static` significa que el
     * método pertenece a la CLASE (no a una instancia), por eso la JVM
     * puede llamarlo sin haber construido un objeto.
     *
     * @param args argumentos de línea de comandos (por ejemplo
     *             --spring.profiles.active=dev).
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfiguracionApplication.class, args);
    }
}
