// Traemos las clases que vamos a probar. Cada "import" es equivalente a
// decir: "vamos a usar esta clase por su nombre corto".
import optional.UsuarioServiceMock;
import records.ClienteDto;
import streams.ProcesadorStreams;

import java.util.List;
import java.util.Map;

/**
 * Punto de entrada del programa + suite de tests "hechos a mano".
 *
 * Este módulo es Java puro (sin Maven ni JUnit) para enseñar los fundamentos,
 * así que no podemos usar frameworks de testing. En su lugar, creamos
 * nuestros propios helpers (assertEqual, assertTrue, assertThrows) y
 * ejecutamos cada test desde main().
 *
 * Al terminar, imprimimos el resumen y devolvemos:
 *   - código de salida 0 si TODOS los tests pasaron
 *   - código de salida 1 si al menos uno falló
 * Esto permite integrar el resultado con scripts de CI (build.sh / build.ps1).
 */
public class Main {

    /**
     * "main" es el método que la JVM ejecuta al hacer `java Main` o
     * `java -jar app.jar` cuando la clase está marcada como Main-Class.
     */
    public static void main(String[] args) {
        int failures = 0; // contador de tests fallidos

        // Cada línea ejecuta un test y suma 1 si falla, 0 si pasa.
        // "Main::testRecordValid" es una method reference al test.
        failures += run("Record: valid instance", Main::testRecordValid);
        failures += run("Record: rejects underage", Main::testRecordRejectsUnderage);
        failures += run("Record: rejects invalid email", Main::testRecordRejectsInvalidEmail);
        failures += run("Streams: filter gmail", Main::testFilterGmail);
        failures += run("Streams: extract names sorted", Main::testExtractNames);
        failures += run("Streams: partition by age", Main::testPartitionByAge);
        failures += run("Optional: found", Main::testOptionalFound);
        failures += run("Optional: not found returns default", Main::testOptionalNotFound);
        failures += run("Pattern matching switch", Main::testPatternMatching);

        System.out.println();
        if (failures == 0) {
            System.out.println("ALL TESTS PASSED");
            // System.exit(0) termina la JVM con "éxito" para el sistema operativo.
            System.exit(0);
        } else {
            System.out.println(failures + " TEST(S) FAILED");
            System.exit(1); // exit != 0 = fracaso (útil para pipelines CI/CD)
        }
    }

    /**
     * Ejecuta un test, imprime [OK] o [FAIL], y devuelve 0 o 1.
     *
     * Runnable es una interfaz funcional (un tipo con un solo método sin
     * parámetros ni return), lo que nos permite pasar cada test como
     * un "trocito de código" ejecutable.
     */
    private static int run(String name, Runnable test) {
        try {
            test.run();
            System.out.println("[OK]   " + name);
            return 0;
        } catch (Throwable t) {
            // Throwable captura CUALQUIER error (incluidos AssertionError).
            System.out.println("[FAIL] " + name + " -> " + t.getMessage());
            return 1;
        }
    }

    // ==================== TESTS ====================

    // --- Record ---

    private static void testRecordValid() {
        ClienteDto c = new ClienteDto("Ada", "ada@gmail.com", 36);
        assertEqual("Ada", c.name());          // getter generado automáticamente
        assertEqual(36, c.age());
        assertTrue(c.hasGmail());              // método custom del record
    }

    private static void testRecordRejectsUnderage() {
        // Verificamos que el constructor compacto RECHAZA edades < 18.
        assertThrows(IllegalArgumentException.class,
                () -> new ClienteDto("Kid", "kid@x.com", 17));
    }

    private static void testRecordRejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> new ClienteDto("NoAt", "invalid-email", 25));
    }

    // --- Streams ---

    /** Datos de ejemplo compartidos por varios tests. */
    private static List<ClienteDto> sample() {
        return List.of(
                new ClienteDto("Ada", "ada@gmail.com", 36),
                new ClienteDto("Alan", "alan@bletchley.uk", 41),
                new ClienteDto("Grace", "grace@gmail.com", 45),
                new ClienteDto("Linus", "linus@kernel.org", 20)
        );
    }

    private static void testFilterGmail() {
        List<ClienteDto> gmail = ProcesadorStreams.filterGmail(sample());
        assertEqual(2, gmail.size()); // Ada y Grace
    }

    private static void testExtractNames() {
        List<String> names = ProcesadorStreams.extractNames(sample());
        assertEqual(List.of("Ada", "Alan", "Grace", "Linus"), names);
    }

    private static void testPartitionByAge() {
        Map<Boolean, List<ClienteDto>> parts = ProcesadorStreams.partitionByAge(sample(), 30);
        // true  -> edad >= 30 : Ada(36), Alan(41), Grace(45) -> 3
        // false -> edad <  30 : Linus(20) -> 1
        assertEqual(3, parts.get(true).size());
        assertEqual(1, parts.get(false).size());
    }

    // --- Optional ---

    private static void testOptionalFound() {
        UsuarioServiceMock service = new UsuarioServiceMock();
        assertEqual("Ada Lovelace", service.getNameOrDefault(1L));
    }

    private static void testOptionalNotFound() {
        UsuarioServiceMock service = new UsuarioServiceMock();
        assertEqual("Desconocido", service.getNameOrDefault(999L));
        assertThrows(IllegalStateException.class, () -> service.getOrThrow(999L));
    }

    // --- Pattern matching (switch expression con tipos, Java 21) ---

    private static void testPatternMatching() {
        Object obj = "Hello";
        // "switch expression" (Java 14+) devuelve un valor. Con "pattern
        // matching" (Java 21) también podemos matchear TIPOS, y Java hace
        // el casting automáticamente por nosotros dentro de cada rama.
        String result = switch (obj) {
            case String s  -> "STR:" + s.toLowerCase(); // s YA es String, sin cast manual
            case Integer i -> "INT:" + i;
            default        -> "OTHER";
        };
        assertEqual("STR:hello", result);
    }

    // ==================== HELPERS DE ASSERT ====================

    /**
     * Comprueba que dos valores son iguales (según equals()).
     * Si no lo son, lanza AssertionError con un mensaje descriptivo.
     */
    private static void assertEqual(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    /** Comprueba que una condición booleana es verdadera. */
    private static void assertTrue(boolean cond) {
        if (!cond) throw new AssertionError("Expected true");
    }

    /**
     * Comprueba que el bloque `r` lanza una excepción del tipo esperado.
     * - Si lanza el tipo correcto -> test pasa.
     * - Si lanza otro tipo o no lanza nada -> test falla.
     */
    private static void assertThrows(Class<? extends Throwable> type, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) return; // ¡bien! era el tipo esperado
            throw new AssertionError("Expected " + type.getSimpleName() + " but got " + t.getClass().getSimpleName());
        }
        // Si llegamos aquí, el bloque no lanzó ninguna excepción.
        throw new AssertionError("Expected " + type.getSimpleName() + " but no exception was thrown");
    }
}
