package com.springroadmap.hexagonal;

// 'import' trae al ámbito de este archivo clases que viven en otros paquetes.
// SpringApplication es la clase de Spring Boot que arranca todo el contexto.
import org.springframework.boot.SpringApplication;
// @SpringBootApplication es una meta-anotación que combina:
//   - @Configuration (esta clase declara beans / configuración)
//   - @EnableAutoConfiguration (Spring Boot autoconfigura según el classpath)
//   - @ComponentScan (escanea este paquete y subpaquetes buscando @Component/@Service/@RestController/etc.)
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación (Módulo 38 - Arquitectura Hexagonal).
 *
 * Analogía del mundo real: piensa en el hexágono como el "corazón" de un banco.
 *   - Adentro están las REGLAS DEL NEGOCIO (dominio + casos de uso).
 *   - Afuera están los "enchufes" (adaptadores): sucursales, cajeros, apps.
 *   - Cada enchufe se conecta a un "puerto" (interfaz).
 *   - Puedes cambiar el enchufe (MySQL -> MongoDB, REST -> gRPC) sin tocar el corazón.
 *
 * Esta clase existe SOLO para que Spring Boot arranque el ComponentScan.
 * El dominio (paquete 'domain') NO conoce esta clase ni a Spring.
 */
@SpringBootApplication
public class HexagonalApplication {

    /**
     * Método main: la JVM lo invoca al ejecutar `java -jar hexagonal-1.0.0.jar`.
     *
     * Palabras clave explicadas:
     *   - 'public': visible desde cualquier paquete (la JVM debe poder llamarlo).
     *   - 'static': pertenece a la clase, no a una instancia (no necesitas 'new HexagonalApplication()').
     *   - 'void': no retorna nada.
     *   - 'String[] args': arreglo de argumentos que se pasan por línea de comandos.
     *
     * ANTES (Java 8) vs AHORA (Java 21):
     *   - Java 8:  public static void main(String[] args) { ... }   // exactamente igual
     *   - Java 21: idem. 'main' no cambió. Lo que sí cambió es que dentro del código usamos
     *              records, 'var', switch expressions, etc.
     */
    public static void main(String[] args) {
        // SpringApplication.run(...) construye el ApplicationContext, arranca el servidor
        // embebido (Tomcat por defecto) y devuelve el contexto ya inicializado.
        SpringApplication.run(HexagonalApplication.class, args);
    }
}
