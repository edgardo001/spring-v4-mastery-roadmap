package com.springroadmap.di.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneOffset;

/**
 * Ejemplo canónico de configuración por Java (Java Config).
 *
 * PREGUNTA DE ALUMNO — "¿Cuándo uso @Bean y cuándo @Component?"
 *   Usa @Component / @Service / @Repository cuando la clase es TUYA
 *   (tú la escribiste y puedes anotarla).
 *
 *   Usa @Bean cuando quieres registrar un objeto que NO puedes anotar:
 *     - Clases de librerías externas (Clock, ObjectMapper, PasswordEncoder).
 *     - Cuando necesitas lógica al crearlo (leer una propiedad, decidir
 *       entre dos implementaciones).
 *
 * En este ejemplo registramos un Clock (java.time.Clock). Clock es una
 * clase del JDK — no podemos ponerle @Service. Entonces creamos una clase
 * @Configuration y dentro un método @Bean que devuelve el objeto.
 *
 * ¿Para qué sirve tener el Clock como bean?
 *   Permite REEMPLAZARLO en tests por un Clock fijo (ej. siempre 2020-01-01)
 *   para verificar comportamiento dependiente de fecha/hora sin flakiness.
 *
 * =====================================================================
 * ANTES (Spring clásico con XML) vs AHORA (Java Config)
 * =====================================================================
 * ANTES — en applicationContext.xml:
 *   <bean id="systemClock" class="java.time.Clock" factory-method="systemUTC"/>
 *
 * AHORA — en Java, con autocompletado del IDE y refactor seguro:
 *   @Bean
 *   public Clock systemClock() { return Clock.systemUTC(); }
 */
@Configuration
public class AppConfig {

    /**
     * Registra un Clock UTC como bean del contenedor.
     * El nombre del bean por defecto es el nombre del método: "systemClock".
     */
    @Bean
    public Clock systemClock() {
        return Clock.system(ZoneOffset.UTC);
    }
}
