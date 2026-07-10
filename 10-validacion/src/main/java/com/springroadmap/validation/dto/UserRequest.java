package com.springroadmap.validation.dto;

// Anotaciones Jakarta Validation (JSR-380 / JSR-303).
// OJO: importar de `jakarta.validation.constraints`, NO de `javax.validation.constraints`
// (Spring Boot 3+ y Boot 4.x están migrados a Jakarta EE 10+).
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de entrada para crear un usuario.
 *
 * Se usa un `record` de Java 21 porque:
 *   - Es inmutable por diseño (todos los campos son final).
 *   - Genera constructor, getters (accessors), equals/hashCode y toString.
 *   - Jackson (deserializador JSON de Spring) sabe crear records desde JSON
 *     usando el constructor canónico.
 *
 * Reglas de validación declaradas EN EL PROPIO DTO
 * (centralización = mantenibilidad):
 *   - name  : @NotBlank  -> no puede ser null, "", ni solo espacios.
 *   - email : @Email     -> debe tener formato user@host (regex de Hibernate).
 *   - age   : @Min(18)   -> el usuario debe ser mayor de edad (int primitivo).
 *   - pin   : @Pattern   -> exactamente 4 dígitos (0-9). Ej: "1234".
 *
 * ============================================================
 * ANTES (Java 8, validación manual con ifs)
 * ============================================================
 * public class UserRequest {
 *     private String name;
 *     private String email;
 *     private int age;
 *     private String pin;
 *     // ... getters/setters ...
 *
 *     public void validate() {
 *         if (name == null || name.trim().isEmpty()) {
 *             throw new IllegalArgumentException("name obligatorio");
 *         }
 *         if (email == null || !email.matches(".+@.+\\..+")) {
 *             throw new IllegalArgumentException("email inválido");
 *         }
 *         if (age < 18) {
 *             throw new IllegalArgumentException("edad debe ser >= 18");
 *         }
 *         if (pin == null || !pin.matches("\\d{4}")) {
 *             throw new IllegalArgumentException("pin debe ser 4 dígitos");
 *         }
 *     }
 * }
 * // Y luego en el Controller: request.validate();  // fácil de OLVIDAR.
 *
 * ============================================================
 * AHORA (Spring Boot 4 + Jakarta Validation)
 * ============================================================
 *   1. Declaras las reglas EN EL DTO (una anotación por regla).
 *   2. Pones @Valid antes del @RequestBody en el Controller.
 *   3. Spring valida SIEMPRE, no se puede olvidar.
 *   4. Los errores se acumulan (no falla al primero) -> el usuario ve TODOS
 *      los campos malos de una sola vez.
 */
public record UserRequest(

        // @NotBlank: rechaza null, "" y "   ". Solo aplica a CharSequence.
        // (Para tipos numéricos usaríamos @NotNull; @NotBlank sobre int no compila.)
        @NotBlank(message = "name obligatorio")
        String name,

        // @Email: valida formato con la regex de Hibernate Validator.
        // Ejemplos válidos: "a@b.co"; Inválidos: "pepito", "a@", "@b.co".
        @Email(message = "email inválido")
        String email,

        // @Min(18): el valor numérico debe ser >= 18.
        // Se usa `int` (primitivo) por simplicidad. Si fuera Integer, además
        // convendría @NotNull para exigir que el campo venga.
        @Min(value = 18, message = "age debe ser >= 18")
        int age,

        // @Pattern: expresión regular libre. `\d{4}` = exactamente 4 dígitos.
        // Se escribe "\\d{4}" en Java por el doble escape del backslash.
        @Pattern(regexp = "\\d{4}", message = "pin debe ser 4 dígitos")
        String pin

) { }
