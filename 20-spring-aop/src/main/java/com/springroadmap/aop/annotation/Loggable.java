package com.springroadmap.aop.annotation;

// Meta-anotaciones: anotaciones que se aplican SOBRE otras anotaciones para
// configurar cómo se comportan (dónde se pueden usar, cuánto tiempo viven).
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Loggable — Anotación custom para marcar métodos que queremos medir y loguear.
 *
 * <p><b>Analogía:</b> es como pegar una etiqueta amarilla "REVISAR" sobre una carpeta.
 * La carpeta (el método) no cambia por dentro; sólo tiene una etiqueta que otro (el
 * aspecto) usará para decidir si le pone atención.
 *
 * <p><b>Palabras clave explicadas:</b>
 * <ul>
 *   <li>{@code @interface} — palabra reservada de Java para declarar una anotación
 *       (no confundir con {@code interface} normal).</li>
 *   <li>{@code @Target(ElementType.METHOD)} — restringe el uso: sólo se puede pegar
 *       sobre métodos, no sobre clases ni campos.</li>
 *   <li>{@code @Retention(RetentionPolicy.RUNTIME)} — la anotación debe sobrevivir a
 *       la compilación y estar disponible en tiempo de ejecución. Sin esto, Spring
 *       AOP NO podría verla vía reflexión.</li>
 * </ul>
 *
 * <p><b>ANTES (Java 8) vs AHORA (Java 21):</b> la sintaxis de anotaciones custom no
 * cambió. Lo que sí cambió: en Java 8+ ya existía; en Java 21 sigue idéntica. Es uno
 * de los pocos rincones estables del lenguaje.
 *
 * <p><b>Uso:</b>
 * <pre>
 *   {@literal @}Loggable
 *   public int add(int a, int b) { return a + b; }
 * </pre>
 */
// PREGUNTA DE ALUMNO — "¿por qué necesito una anotación custom si @Around ya existe?"
//   Porque permite escribir el pointcut como @annotation(...Loggable) y aplicar el
//   aspecto SÓLO donde tú lo marques explícitamente, sin depender de nombres de
//   paquetes o patrones frágiles.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    // Sin atributos por ahora. Si quisieras, podrías añadir:
    //   String value() default "";   // etiqueta libre
    //   boolean logArgs() default true;
}
