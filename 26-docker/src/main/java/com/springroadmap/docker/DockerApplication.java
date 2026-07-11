package com.springroadmap.docker;

// "import" hace disponible una clase de otro paquete por su nombre corto.
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del modulo 26 (Docker).
 *
 * Analogia del mundo real:
 *   Piensa en esta clase como el "boton rojo de encendido" de una
 *   maquina expendedora. La maquina (Spring Boot) tiene monedero,
 *   catalogo y motor, pero no arranca hasta que alguien pulsa ese
 *   boton. En Java, ese boton es el metodo main(...).
 *
 * PREGUNTA DE ALUMNO - "Si esto es una app web, por que no veo
 *   nada de Tomcat aqui?"
 *   Porque @SpringBootApplication activa la AUTOCONFIGURACION: Spring
 *   revisa el classpath, ve el JAR de Tomcat embebido (viene con
 *   spring-boot-starter-web) y arranca un servidor en el puerto 8080
 *   sin que tu escribas una sola linea de configuracion.
 *
 * PREGUNTA DE ALUMNO - "Como se relaciona esto con Docker?"
 *   No se relaciona directamente. El codigo Java NO sabe que corre
 *   dentro de un contenedor. Docker es una CAJA que envuelve al JAR
 *   producido por Maven. Dentro de esa caja se ejecuta exactamente
 *   el mismo comando que ejecutarias en tu PC:
 *     java -jar docker-1.0.0.jar
 *
 * ============================================================
 * ANTES (Java 8 / Spring 4 clasico) vs AHORA (Java 21 / Spring Boot 4)
 * ============================================================
 * ANTES:
 *   - Escribias un web.xml describiendo servlets.
 *   - Empaquetabas un WAR.
 *   - Desplegabas ese WAR dentro de un Tomcat INSTALADO en el servidor.
 *   - Copiabas el WAR por SCP/FTP y reiniciabas Tomcat con systemd.
 *
 * AHORA:
 *   - Una unica clase con @SpringBootApplication.
 *   - Se empaqueta como fat-JAR (Tomcat viaja DENTRO del JAR).
 *   - Se ejecuta con "java -jar" — el servidor arranca solo.
 *   - Se distribuye como IMAGEN de Docker (auto-contenida, incluye
 *     JRE + JAR) que corre en cualquier maquina con Docker instalado.
 */
@SpringBootApplication
public class DockerApplication {

    /**
     * Metodo main: punto de entrada estandar de cualquier programa Java.
     *
     * Palabras clave explicadas:
     *   public       -> se puede invocar desde fuera de la clase.
     *   static       -> pertenece a la CLASE, no a una instancia. La JVM
     *                   puede llamarlo sin hacer "new DockerApplication()".
     *   void         -> no devuelve nada.
     *   String[] args -> arreglo de argumentos por linea de comandos.
     *                    Ejemplo: java -jar app.jar --server.port=9090
     *                    -> args = ["--server.port=9090"]
     *
     * SpringApplication.run(...) hace todo el trabajo de arranque:
     *   1. Crea el ApplicationContext (contenedor de beans).
     *   2. Escanea @Component / @RestController en este paquete y
     *      subpaquetes.
     *   3. Aplica autoconfiguracion segun el classpath (aqui: Tomcat).
     *   4. Escucha peticiones HTTP hasta que se detiene con Ctrl+C
     *      (o hasta que Docker envia SIGTERM al contenedor).
     */
    public static void main(String[] args) {
        SpringApplication.run(DockerApplication.class, args);
    }
}
