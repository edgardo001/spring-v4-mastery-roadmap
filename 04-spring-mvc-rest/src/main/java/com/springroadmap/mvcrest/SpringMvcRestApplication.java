package com.springroadmap.mvcrest;

// PREGUNTA DE ALUMNO — "¿qué es 'package'?"
//   Es la carpeta lógica donde vive esta clase. En disco vive en
//   src/main/java/com/springroadmap/mvcrest/. El nombre del paquete debe
//   coincidir con la ruta de carpetas.

import org.springframework.boot.SpringApplication;                    // Utilidad que arranca la app.
import org.springframework.boot.autoconfigure.SpringBootApplication;  // Meta-anotación mágica de Spring Boot.

/**
 * Clase principal del módulo 04.
 *
 * PROPÓSITO
 * ---------
 * Arrancar el contenedor Spring, encender Tomcat embebido en el puerto 8080
 * y publicar el CRUD REST de {@code Product} bajo /api/products.
 *
 * ANALOGÍA
 * --------
 * Piensa en esta clase como la LLAVE DE CONTACTO de un auto. No conduce, no
 * frena, no acelera: solo enciende el motor (el contexto Spring). Una vez
 * arrancado, quienes conducen son los @RestController, @Service, @Repository.
 *
 * ANTES (Java 8 / Spring 3.x clásico)
 * -----------------------------------
 *   public class Main {
 *       public static void main(String[] args) {
 *           ApplicationContext ctx =
 *               new ClassPathXmlApplicationContext("beans.xml");   // XML gigante
 *           Server tomcat = new Tomcat();                          // configurar Tomcat a mano
 *           tomcat.setPort(8080);
 *           tomcat.addWebapp("/", new File("webapp").getAbsolutePath());
 *           tomcat.start();
 *           tomcat.getServer().await();
 *       }
 *   }
 *
 * AHORA (Spring Boot 4 / Java 21)
 * -------------------------------
 *   @SpringBootApplication
 *   public class SpringMvcRestApplication {
 *       public static void main(String[] args) {
 *           SpringApplication.run(SpringMvcRestApplication.class, args);
 *       }
 *   }
 *
 * Dos líneas en vez de veinte: Spring Boot lee el classpath, ve
 * spring-boot-starter-web, y auto-configura Tomcat + DispatcherServlet +
 * Jackson por convención.
 */
// @SpringBootApplication combina 3 anotaciones:
//   - @Configuration      → esta clase puede definir @Bean.
//   - @EnableAutoConfiguration → activa las auto-configuraciones (Tomcat, Jackson...).
//   - @ComponentScan      → escanea este paquete y subpaquetes buscando @Component,
//                            @Service, @Repository, @RestController.
@SpringBootApplication
public class SpringMvcRestApplication {

    /**
     * Punto de entrada estándar de la JVM.
     *
     * Palabras clave:
     *   - public  → visible fuera del paquete (la JVM la busca por reflexión).
     *   - static  → pertenece a la clase, no a una instancia (la JVM no crea objetos).
     *   - void    → no retorna nada.
     *   - String[] args → argumentos de línea de comandos (ej: --server.port=9090).
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) hace 3 cosas:
        //   1) Crea el ApplicationContext (contenedor de beans).
        //   2) Registra los beans encontrados por @ComponentScan.
        //   3) Arranca Tomcat embebido y bloquea el hilo main.
        SpringApplication.run(SpringMvcRestApplication.class, args);
    }
}
