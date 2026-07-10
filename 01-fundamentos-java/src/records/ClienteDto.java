// "package" indica en qué "carpeta lógica" vive esta clase.
// Aquí decimos que ClienteDto pertenece al paquete "records", lo que permite
// que otras clases la importen con "import records.ClienteDto;".
package records;

/**
 * Un "record" es un tipo especial de clase en Java 16+ pensado para
 * transportar datos (como una ficha de cliente). Con una sola línea Java
 * genera automáticamente por ti:
 *   - el constructor (new ClienteDto(...))
 *   - los "getters" (name(), email(), age())
 *   - equals(), hashCode() y toString()
 * Además, un record es INMUTABLE: una vez creado, sus valores no cambian.
 *
 * Analogía: es como una tarjeta de presentación impresa. No puedes borrarle
 * el nombre y escribir otro; si necesitas otro nombre, imprimes otra tarjeta.
 *
 * =====================================================================
 * ANTES (Java 8 / clásico) vs AHORA (Java 21)
 * =====================================================================
 * ANTES (Java 8) — 50+ líneas de "boilerplate":
 *
 *   public final class ClienteDto {
 *       private final String name;
 *       private final String email;
 *       private final int age;
 *
 *       public ClienteDto(String name, String email, int age) {
 *           if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name is required");
 *           if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
 *           if (age < 18) throw new IllegalArgumentException("Age must be 18 or older");
 *           this.name = name;
 *           this.email = email;
 *           this.age = age;
 *       }
 *
 *       public String getName()  { return name; }
 *       public String getEmail() { return email; }
 *       public int getAge()      { return age; }
 *
 *       @Override public boolean equals(Object o) { ... 15 líneas ... }
 *       @Override public int hashCode() { return Objects.hash(name, email, age); }
 *       @Override public String toString() { return "ClienteDto[...]"; }
 *   }
 *
 * AHORA (Java 21) — 1 línea + validación:
 *
 *   public record ClienteDto(String name, String email, int age) {
 *       public ClienteDto { validar... }
 *   }
 *
 * Lo que se hace SOLO: constructor, getters, equals, hashCode, toString,
 * inmutabilidad. Todo automático.
 */
public record ClienteDto(String name, String email, int age) {

    // Este bloque se llama "constructor compacto" (compact constructor).
    // Se ejecuta CADA vez que alguien hace `new ClienteDto(...)`, ANTES de
    // guardar los campos. Sirve para validar los datos de entrada.
    //
    // PREGUNTA DE ALUMNO — "¿No hay 'this.name = name;' etc.?"
    //   No hace falta: el compilador de Java lo agrega automáticamente al
    //   final del constructor compacto de un record. Tú solo pones las
    //   validaciones (la asignación de campos la hace la máquina).
    public ClienteDto {
        // isBlank() devuelve true si el texto es null-like o solo tiene espacios.
        // ANTES (Java 8): `name == null || name.trim().length() == 0`
        // AHORA (Java 21): `name == null || name.isBlank()`
        if (name == null || name.isBlank()) {
            // Lanzamos una excepción para rechazar datos inválidos.
            // IllegalArgumentException es la excepción estándar para "parámetro incorrecto".
            throw new IllegalArgumentException("Name is required");
        }
        // Verificación muy simple de email: solo pedimos que contenga "@".
        // En un caso real usaríamos @Email de Bean Validation (módulo 10).
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        // Regla de negocio: solo clientes mayores de edad.
        if (age < 18) {
            throw new IllegalArgumentException("Age must be 18 or older");
        }
    }

    /**
     * Método de "conveniencia" para saber si el email es de Gmail.
     * Un record puede tener métodos propios además de los generados.
     * Aquí encapsulamos la lógica "termina en @gmail.com" en un único lugar,
     * así el resto del código no repite esa comparación.
     *
     * ANTES (Java 8):
     *   public boolean hasGmail() {
     *       return this.getEmail().endsWith("@gmail.com");
     *   }
     * AHORA (Java 21):
     *   public boolean hasGmail() {
     *       return email.endsWith("@gmail.com");  // "email" es el accessor del record
     *   }
     */
    public boolean hasGmail() {
        // endsWith devuelve true si el String termina con el texto indicado.
        return email.endsWith("@gmail.com");
    }
}
