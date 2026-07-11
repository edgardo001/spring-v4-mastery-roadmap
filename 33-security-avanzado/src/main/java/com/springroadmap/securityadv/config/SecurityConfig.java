package com.springroadmap.securityadv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Configuracion de Spring Security para el modulo 33 — Security Avanzado.
 *
 * <p><b>Proposito:</b> habilitar Method Security ({@code @PreAuthorize} /
 * {@code @PostAuthorize}) sobre servicios, ademas de la clasica proteccion
 * por URL con Basic Auth.</p>
 *
 * <p><b>Analogia:</b> {@code @EnableMethodSecurity} agrega guardias
 * dentro de cada oficina; el {@link SecurityFilterChain} agrega el
 * guardia en la puerta del edificio.</p>
 *
 * <p><b>Palabras clave:</b>
 * <ul>
 *   <li>{@code @Configuration} — clase que aporta beans al contenedor.</li>
 *   <li>{@code @EnableWebSecurity} — activa la cadena de filtros web.</li>
 *   <li>{@code @EnableMethodSecurity} — activa proxies AOP para
 *       {@code @PreAuthorize}/{@code @PostAuthorize} en beans Spring.</li>
 *   <li>{@code @Bean} — el metodo devuelve un objeto gestionado por Spring.</li>
 *   <li>Lambda ({@code auth -> auth...}) — funcion anonima que reemplaza
 *       las viejas clases anonimas heredadas de {@code WebSecurityConfigurerAdapter}.</li>
 * </ul>
 * </p>
 *
 * <h3>ANTES (Java 8 / Spring Security 5) vs AHORA (Java 21 / Spring Security 7)</h3>
 * <pre>
 * // ANTES:
 * //   @EnableGlobalMethodSecurity(prePostEnabled = true)  // nombre viejo
 * //   public class SecurityConfig extends WebSecurityConfigurerAdapter { ... }
 *
 * // AHORA:
 * //   @EnableMethodSecurity  // por defecto prePostEnabled=true
 * //   public class SecurityConfig { ... }  // sin herencia
 * </pre>
 */
@Configuration
@EnableWebSecurity
// @EnableMethodSecurity habilita @PreAuthorize/@PostAuthorize.
// Por defecto ya viene con prePostEnabled=true (a diferencia del
// viejo @EnableGlobalMethodSecurity, que exigia declararlo explicito).
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Cadena de filtros HTTP.
     *
     * <ol>
     *   <li>CSRF deshabilitado (API stateless).</li>
     *   <li>Todo endpoint requiere autenticacion (la autorizacion fina
     *       la hace {@code @PreAuthorize} en el servicio).</li>
     *   <li>Basic Auth para la autenticacion.</li>
     *   <li>{@link AccessDeniedHandler} personalizado para devolver 403
     *       cuando el usuario esta autenticado pero no autorizado.</li>
     * </ol>
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Todo endpoint requiere estar autenticado.
                        // El role check lo hace @PreAuthorize en el servicio.
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()))
                .build();
    }

    /**
     * Handler para {@link AccessDeniedException} que devuelve 403.
     *
     * <p>PREGUNTA DE ALUMNO — "¿por que necesito esto si Spring ya
     * devuelve 403 por defecto?"</p>
     * <p>Porque cuando la excepcion la lanza un proxy de Method Security
     * (por {@code @PreAuthorize}/{@code @PostAuthorize}), sube por el
     * controller y Spring MVC podria convertirla en 500 si no la mapeamos.
     * Este handler garantiza el 403 correcto.</p>
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> response.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden");
    }

    /**
     * Fuente de usuarios en memoria.
     *
     * <p>Se cargan dos usuarios de demo:
     * <ul>
     *   <li>{@code admin} / {@code admin123} — rol {@code ADMIN}.</li>
     *   <li>{@code user}  / {@code user123}  — rol {@code USER}.</li>
     * </ul>
     * </p>
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * Encoder BCrypt para contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
