package com.springroadmap.vslice;

// Importamos SpringApplication: la clase que arranca el contenedor Spring
// (equivalente al "motor" que enciende toda la maquinaria).
import org.springframework.boot.SpringApplication;
// @SpringBootApplication combina 3 anotaciones: @Configuration + @EnableAutoConfiguration + @ComponentScan.
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion del modulo 62 - Vertical Slice Architecture.
 *
 * <p><b>Analogia del mundo real:</b> imagina un supermercado.
 * En arquitectura HORIZONTAL (por capas) tienes: bodega + reposicion + cajeros
 * organizados como departamentos separados por edificio; para hacer una venta
 * cruzas los 3 edificios.
 * En arquitectura VERTICAL (slice) tienes "estaciones autonomas": cada estacion
 * ya trae su cajero, su stock y su registro; todo lo que necesitas para vender
 * "una manzana" vive en la estacion "vender-manzana". Cambiar la estacion no
 * afecta a las demas.</p>
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b>
 * <pre>
 *   // ANTES (Java 8):
 *   public static void main(String[] args) {
 *       SpringApplication.run(VerticalSliceApplication.class, args);
 *   }
 *   // AHORA (Java 21): sintaxis identica; los cambios modernos estan en
 *   // los records y las expresiones switch dentro de los features/.
 * </pre></p>
 */
// PREGUNTA DE ALUMNO — "¿que es la arroba '@' antes de una palabra?"
//   Es una "anotacion": una etiqueta que Spring lee para decidir que hacer con
//   la clase (crear un bean, exponer un endpoint, escanear paquetes, etc.).
@SpringBootApplication
public class VerticalSliceApplication {

    /**
     * Metodo main: puerta de entrada de cualquier programa Java.
     * <ul>
     *   <li><code>public</code>: visible desde fuera de la clase.</li>
     *   <li><code>static</code>: no necesita instanciar la clase para llamarlo.</li>
     *   <li><code>void</code>: no devuelve nada.</li>
     *   <li><code>String[] args</code>: argumentos de linea de comandos.</li>
     * </ul>
     */
    public static void main(String[] args) {
        // SpringApplication.run inicializa el contexto: escanea @Component,
        // arma los beans, arranca Tomcat embebido en el puerto 8080.
        SpringApplication.run(VerticalSliceApplication.class, args);
    }
}
