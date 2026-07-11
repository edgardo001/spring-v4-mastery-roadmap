package com.springroadmap.jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuracion de seguridad para autenticacion STATELESS via JWT.
 *
 * DECISIONES:
 *   - CSRF desactivado: CSRF protege sesiones basadas en cookies. Con JWT en header
 *     el navegador NO adjunta el token automaticamente, asi que CSRF no aplica.
 *   - SessionCreationPolicy.STATELESS: Spring no crea HttpSession. Cada request
 *     debe traer su JWT; nada se recuerda entre requests.
 *   - /api/auth/** es publico (para poder loguearse). El resto de /api/** requiere
 *     autenticacion.
 *   - JwtAuthFilter se agrega ANTES de UsernamePasswordAuthenticationFilter, para
 *     que cuando llegue el request ya venga con el Authentication seteado si el
 *     token es valido.
 *
 * ANALOGIA: Es el reglamento del edificio.
 *   - "No hay lista de invitados persistente" (stateless).
 *   - "El portero (JwtAuthFilter) revisa la pulsera antes que cualquier otra puerta".
 *   - "La puerta /login siempre esta abierta, el resto solo con pulsera valida".
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(final JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * SecurityFilterChain declara la cadena de filtros de Spring Security 6+.
     *
     * ANTES (Spring Security 5, WebSecurityConfigurerAdapter):
     *   public class SecurityConfig extends WebSecurityConfigurerAdapter {
     *       @Override
     *       protected void configure(HttpSecurity http) throws Exception {
     *           http.csrf().disable()
     *               .authorizeRequests()
     *               .antMatchers("/api/auth/**").permitAll()
     *               .anyRequest().authenticated();
     *       }
     *   }
     *
     * AHORA (Spring Security 6+, estilo funcional con lambdas):
     *   @Bean SecurityFilterChain que retorna http.build().
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
            // Desactivar CSRF (no aplica en autenticacion stateless por header).
            .csrf(csrf -> csrf.disable())

            // No crear sesion HTTP; nada de JSESSIONID.
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de autorizacion por URL.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()   // login abierto
                .requestMatchers("/api/**").authenticated()    // todo lo demas /api requiere JWT
                .anyRequest().permitAll()                      // resto (errores, etc.) libre
            )

            // Deshabilitar login por formulario y HTTP Basic (no los usamos).
            .formLogin(f -> f.disable())
            .httpBasic(b -> b.disable())

            // Insertar nuestro filtro JWT ANTES del filtro clasico de usuario/contrasena.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
