package com.springroadmap.springreact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 * SecurityConfig: configuracion minima de Spring Security para demo con frontend React.
 *
 * Regla:
 *   - GET  /api/messages  -> publico (cualquiera lee).
 *   - POST /api/messages  -> requiere Basic Auth (demo/demo123).
 *
 * ANALOGIA: es el "portero del edificio". Deja entrar libre a los visitantes
 * que solo quieren mirar (GET), pero pide identificacion a quienes traen
 * paquetes para dejar (POST).
 *
 * IMPORTANTE (MEMORY.md 34-oauth2): en Boot 4 hay que anotar la clase con
 * @EnableWebSecurity explicitamente para que el HttpSecurity bean funcione.
 *
 * ANTES (Java 8 + Spring Security 4) — se extendia WebSecurityConfigurerAdapter:
 *   @Override protected void configure(HttpSecurity http) throws Exception { ... }
 *
 * AHORA (Java 21 + Spring Security 7) — se declara un @Bean SecurityFilterChain
 * con lambdas encadenadas (DSL fluent).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Cadena de filtros de seguridad.
     *
     * - `csrf.disable()` porque la API es stateless y sera consumida por React via fetch
     *   con Basic Auth. En apps con formularios HTML tradicionales, NO deshabilitar CSRF.
     * - `cors.and()` (Customizer default) delega en el CorsConfigurationSource que
     *   registra WebMvcConfigurer / la anotacion @CrossOrigin del controller.
     * - `httpBasic` habilita autenticacion Basic (usuario:contraseña en base64 en el header).
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})  // habilita CORS (usa la config de CorsConfig)
            .authorizeHttpRequests(auth -> auth
                // GET publico, resto autenticado.
                .requestMatchers(HttpMethod.GET, "/api/messages/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // preflight CORS
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});  // Basic Auth para POST

        return http.build();
    }

    /**
     * Usuario en memoria "demo" con password "demo123". Solo para demostracion.
     * En produccion: BD + Spring Data + password hasheado en la BD.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails demo = User.withUsername("demo")
                .password(encoder.encode("demo123"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(demo);
    }

    /**
     * BCrypt: algoritmo de hashing de passwords resistente a fuerza bruta.
     * Nunca guardar passwords en texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
