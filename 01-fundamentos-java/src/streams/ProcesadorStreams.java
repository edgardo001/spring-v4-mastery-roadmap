package streams;

// "import" trae clases de otros paquetes para poder usarlas por su nombre corto.
import records.ClienteDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Clase de utilidades para procesar listas de ClienteDto usando la Streams API.
 *
 * Streams API (Java 8+) permite escribir transformaciones sobre colecciones
 * de forma "declarativa": describes QUÉ quieres hacer (filtrar, mapear,
 * agrupar) en vez de CÓMO recorrer la lista con bucles for.
 *
 * Analogía: es como una línea de ensamblaje. Los datos entran, pasan por
 * varias estaciones (filter, map, collect) y salen transformados.
 *
 * "final" en la clase significa que NADIE puede heredar de ella (extender).
 * Esto tiene sentido porque es una clase de utilidades, no un tipo base.
 *
 * =====================================================================
 * ANTES (Java 8 / imperativo) vs AHORA (Java 21 / declarativo)
 * =====================================================================
 * ANTES — filtrar Gmail con un bucle for clásico (Java 8 estilo 1.4):
 *
 *   public static List<ClienteDto> filterGmail(List<ClienteDto> clients) {
 *       List<ClienteDto> result = new ArrayList<>();
 *       for (ClienteDto c : clients) {
 *           if (c.getEmail().endsWith("@gmail.com")) {
 *               result.add(c);
 *           }
 *       }
 *       return result;
 *   }
 *
 * AHORA — con Streams API:
 *
 *   public static List<ClienteDto> filterGmail(List<ClienteDto> clients) {
 *       return clients.stream()
 *               .filter(ClienteDto::hasGmail)
 *               .toList();
 *   }
 *
 * Menos código, más legible, y el compilador puede optimizarlo mejor.
 */
public final class ProcesadorStreams {

    // Constructor privado: impide que alguien haga `new ProcesadorStreams()`.
    // Todos los métodos son static (se llaman como ProcesadorStreams.filterGmail(...)),
    // así que no tiene sentido crear instancias.
    private ProcesadorStreams() {}

    /**
     * Filtra la lista y devuelve solo los clientes cuyo email termina en @gmail.com.
     *
     * "static" significa que el método pertenece a la clase, no a una instancia.
     * Se invoca así: ProcesadorStreams.filterGmail(lista).
     *
     * PREGUNTA DE ALUMNO — "¿Qué es 'ClienteDto::hasGmail'?"
     *   Se llama "method reference" y es equivalente a la lambda:
     *     c -> c.hasGmail()
     *   Java sabe que hasGmail() no recibe parámetros y devuelve boolean,
     *   así que puede reutilizarlo como criterio de filtrado.
     */
    public static List<ClienteDto> filterGmail(List<ClienteDto> clients) {
        return clients.stream()                       // 1) convertimos la lista en un "stream" (flujo procesable)
                .filter(ClienteDto::hasGmail)         // 2) dejamos pasar solo los que cumplan la condición
                .toList();                            // 3) volvemos a colectar los elementos en una List inmutable
        // ANTES (Java 8): .collect(Collectors.toList()) → devolvía una List MUTABLE.
        // AHORA (Java 16+): .toList() → devuelve una List INMUTABLE, más segura.
    }

    /**
     * Devuelve los nombres de todos los clientes ordenados alfabéticamente.
     * Ejemplo de "map": transformar cada elemento en otro (ClienteDto -> String).
     *
     * ANTES (Java 8):
     *   List<String> names = new ArrayList<>();
     *   for (ClienteDto c : clients) names.add(c.getName());
     *   Collections.sort(names);
     *   return names;
     *
     * AHORA (Java 21):
     *   return clients.stream().map(ClienteDto::name).sorted().toList();
     */
    public static List<String> extractNames(List<ClienteDto> clients) {
        return clients.stream()
                .map(ClienteDto::name)   // ClienteDto -> String (su nombre)
                .sorted()                // orden natural alfabético para Strings
                .toList();
    }

    /**
     * Divide los clientes en dos grupos según su edad:
     *   clave true  -> clientes con edad >= threshold
     *   clave false -> clientes con edad <  threshold
     *
     * "partitioningBy" es un caso especial de "groupingBy" cuando el criterio
     * es binario (dos cubetas: verdadero / falso).
     *
     * ANTES (Java 8) — dos bucles / dos listas:
     *   List<ClienteDto> mayores = new ArrayList<>();
     *   List<ClienteDto> menores = new ArrayList<>();
     *   for (ClienteDto c : clients) {
     *       if (c.getAge() >= threshold) mayores.add(c); else menores.add(c);
     *   }
     *
     * AHORA (Java 21) — un solo stream con partitioningBy:
     *   return clients.stream().collect(Collectors.partitioningBy(c -> c.age() >= threshold));
     */
    public static Map<Boolean, List<ClienteDto>> partitionByAge(List<ClienteDto> clients, int threshold) {
        return clients.stream()
                .collect(Collectors.partitioningBy(c -> c.age() >= threshold));
        // "c -> c.age() >= threshold" es una "lambda": una función anónima corta
        // que recibe un ClienteDto y devuelve un boolean.
        //
        // PREGUNTA DE ALUMNO — "¿Qué es una lambda?"
        //   Es una forma abreviada de escribir una función anónima. Antes tendrías:
        //     new Predicate<ClienteDto>() { public boolean test(ClienteDto c) { return c.getAge() >= threshold; } }
        //   Ahora: c -> c.age() >= threshold
    }
}
