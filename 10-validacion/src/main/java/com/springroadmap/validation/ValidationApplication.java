// Package raíz del módulo 10. Debe coincidir con la ruta física en disco.
package com.springroadmap.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo 10 - Validación con Bean Validation.
 *
 * ¿Qué es NUEVO respecto a módulos anteriores?
 *   El "main" es el mismo de siempre. Lo NUEVO vive en:
 *     - UserRequest             -> record DTO con anotaciones Jakarta.
 *     - UserController          -> usa @Valid en @RequestBody.
 *     - ValidationExceptionHandler -> @RestControllerAdvice que captura
 *                                     MethodArgumentNotValidException y
 *                                     devuelve un 400 con Map field -> message.
 *
 * PREGUNTA DE ALUMNO — "¿Quién EJECUTA las validaciones?"
 *   Cuando arranca SpringApplication.run(...):
 *     1. Detecta spring-boot-starter-validation en el classpath.
 *     2. Auto-configura un `LocalValidatorFactoryBean` (Hibernate Validator).
 *     3. Registra un `MethodValidationPostProcessor` para @Valid en Controllers.
 *     4. Cuando llega un POST con @Valid @RequestBody, el HandlerMethodArgument
 *        Resolver valida el objeto ANTES de invocar tu método. Si falla, lanza
 *        MethodArgumentNotValidException y tu método NUNCA se ejecuta.
 */
@SpringBootApplication
public class ValidationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValidationApplication.class, args);
    }
}
