package com.springroadmap.validation.controller;

import com.springroadmap.validation.dto.UserRequest;
// @Valid es de Jakarta Validation. Es LA anotación que activa la validación
// automática sobre el argumento anotado (@RequestBody en este caso).
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST del módulo 10.
 *
 * Endpoint único:
 *   POST /api/users
 *   Body: JSON con name/email/age/pin.
 *   Respuesta:
 *     - 201 Created (sin body) si los datos son válidos.
 *     - 400 Bad Request con Map<String,String> (field -> mensaje)
 *       si @Valid encuentra errores. Esa respuesta la construye
 *       ValidationExceptionHandler, NO este método.
 *
 * ============================================================
 * FAQ ALUMNO — "¿Por qué mi @Valid no valida nada?"
 *   Causas típicas (todas ocurren en la vida real):
 *     1. Falta la dependencia spring-boot-starter-validation en el pom.
 *     2. Olvidaste el @Valid ANTES de @RequestBody.
 *     3. Importaste `javax.validation.Valid` (viejo) en vez de
 *        `jakarta.validation.Valid` (Spring Boot 3+ / 4.x).
 *
 * FAQ ALUMNO — "¿Puedo devolver el objeto creado en el 201?"
 *   Sí. En este módulo, para mantenernos enfocados en VALIDACIÓN,
 *   devolvemos solo el status 201 sin body. En un caso real,
 *   devolverías un UserResponse (id, name, email) con el location.
 * ============================================================
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Crea un usuario si los datos son válidos.
     *
     * FLUJO INTERNO CUANDO LLEGA UN POST:
     *   1. Jackson deserializa el JSON en un UserRequest.
     *   2. Spring, por el @Valid, invoca al Validator (Hibernate).
     *   3. Si hay errores -> lanza MethodArgumentNotValidException
     *      -> ValidationExceptionHandler devuelve 400 + Map.
     *      -> ESTE MÉTODO NUNCA SE EJECUTA.
     *   4. Si no hay errores -> se ejecuta este método -> 201.
     *
     * @param request DTO validado por Spring.
     * @return 201 Created sin body.
     */
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserRequest request) {
        // En un módulo posterior (JPA) aquí llamaríamos a un @Service que
        // guarde el usuario. Ahora nos basta con confirmar el 201.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
