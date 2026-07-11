package com.springroadmap.jwt;

// 'import' trae clases de otros paquetes para poder usarlas en este archivo.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del modulo 14 - Autenticacion JWT.
 *
 * PROPOSITO: Levantar un servidor Spring Boot que expone dos endpoints:
 *   - POST /api/auth/login  -> emite un JWT si las credenciales son validas.
 *   - GET  /api/me          -> devuelve el nombre del usuario del token (protegido).
 *
 * ANALOGIA: El servidor es como la boleteria de un cine.
 *   - En /login, muestras tu carnet y te dan una entrada (JWT) firmada por la casa.
 *   - En /me, la entrada por si sola te deja pasar; ya no necesitan revisar tu carnet.
 *
 * PALABRAS CLAVE:
 *   - '@SpringBootApplication' — anotacion compuesta que activa auto-configuracion,
 *     escaneo de componentes y define esta clase como fuente de configuracion.
 *   - 'public static void main' — punto de entrada de toda aplicacion Java.
 */
@SpringBootApplication
public class JwtApplication {

    // PREGUNTA DE ALUMNO — "¿por que 'static' en main?"
    //   Porque main se ejecuta antes de que exista cualquier instancia. 'static'
    //   significa que pertenece a la CLASE, no a un objeto: se puede invocar sin 'new'.
    public static void main(final String[] args) {
        // SpringApplication.run arranca el contenedor Spring (crea beans, servidor web, etc.).
        SpringApplication.run(JwtApplication.class, args);
    }
}
