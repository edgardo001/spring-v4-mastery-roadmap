package com.springroadmap.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para el módulo 13.
 *
 * <p><b>Propósito:</b> declarar las reglas de acceso, los usuarios en
 * memoria y el algoritmo para encriptar contraseñas.</p>
 *
 * <p><b>Analogía:</b> imagina un edificio con dos pasillos:
 * <ul>
 *   <li>{@code /api/public/**} → pasillo de la recepción abierta al público.</li>
 *   <li>{@code /api/private/**} → pasillo con torniquete que exige credenciales.</li>
 * </ul>
 * El {@link SecurityFilterChain} es el guardia que revisa el gafete
 * (Basic Auth) antes de dejar pasar.</p>
 *
 * <p><b>Palabras clave:</b>
 * <ul>
 *   <li>{@code @Configuration} — marca esta clase como fuente de beans.</li>
 *   <li>{@code @EnableWebSecurity} — activa la cadena de filtros de seguridad web.</li>
 *   <li>{@code @Bean} — el método devuelve un objeto que Spring administrará.</li>
 *   <li>Lambdas ({@code auth -> auth.requestMatchers(...)}) — funciones anónimas
 *       que configuran opciones sin subclases anónimas verbosas.</li>
 * </ul>
 * </p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES (Spring Security 4/5, Java 8):
 * //   public class SecurityConfig extends WebSecurityConfigurerAdapter {
 * //       @Override
 * //       protected void configure(HttpSecurity http) throws Exception {
 * //           http.csrf().disable()
 * //               .authorizeRequests()
 * //               .antMatchers("/api/public/**").permitAll()
 * //               .anyRequest().authenticated()
 * //               .and().httpBasic();
 * //       }
 * //   }
 * //   // WebSecurityConfigurerAdapter fue DEPRECADO en Spring Security 5.7
 * //   // y ELIMINADO en Spring Security 6/7.
 *
 * // AHORA (Spring Security 6/7, Java 21):
 * //   Se declara un @Bean SecurityFilterChain con lambda DSL,
 * //   sin extender ninguna clase base.
 * </pre>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad para toda la aplicación.
     *
     * <p>Reglas:
     * <ol>
     *   <li>CSRF deshabilitado (API stateless: no hay formularios/cookies).</li>
     *   <li>{@code /api/public/**} → acceso libre.</li>
     *   <li>{@code /api/private/**} → requiere autenticación.</li>
     *   <li>Cualquier otro endpoint → requiere autenticación.</li>
     *   <li>Autenticación por HTTP Basic (cabecera {@code Authorization: Basic ...}).</li>
     * </ol>
     * </p>
     *
     * @param http el DSL fluido que Spring Security inyecta
     * @return la cadena de filtros construida
     * @throws Exception si la construcción falla
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // PREGUNTA DE ALUMNO — "¿qué es CSRF y por qué lo deshabilito?"
                //   CSRF (Cross-Site Request Forgery) protege formularios
                //   basados en sesión con cookies. En APIs REST stateless
                //   con Basic Auth o JWT no aplica, por eso lo desactivamos.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // requestMatchers reemplaza al antiguo antMatchers.
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/private/**").authenticated()
                        .anyRequest().authenticated()
                )
                // Basic Auth con configuración por defecto.
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * Fuente de usuarios en memoria (sin base de datos).
     *
     * <p>Contiene un único usuario {@code admin} con contraseña {@code admin123}
     * y rol {@code ADMIN}. La contraseña se guarda hasheada con BCrypt.</p>
     *
     * <p><b>Palabras clave:</b>
     * <ul>
     *   <li>{@code UserDetails} — contrato de Spring Security que describe
     *       a un usuario autenticable.</li>
     *   <li>{@code InMemoryUserDetailsManager} — implementación in-memory
     *       útil para demos y tests.</li>
     * </ul>
     * </p>
     *
     * @param passwordEncoder el encoder para hashear la contraseña
     * @return el manager con el usuario admin cargado
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                // Hasheamos la contraseña en caliente para que jamás quede
                // en texto plano ni siquiera en memoria del proceso.
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Algoritmo de hashing para contraseñas.
     *
     * <p>BCrypt aplica un salt aleatorio y un factor de trabajo (cost) que
     * hace lento el cracking por fuerza bruta. Nunca se debe guardar una
     * contraseña en texto plano.</p>
     *
     * @return una instancia reutilizable de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
