package com.springroadmap.dtos.controller;

import com.springroadmap.dtos.domain.Employee;
import com.springroadmap.dtos.dto.EmployeeRequest;
import com.springroadmap.dtos.dto.EmployeeResponse;
import com.springroadmap.dtos.mapper.EmployeeMapper;
import com.springroadmap.dtos.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST que expone Employee usando SIEMPRE DTOs, nunca la Entity.
 *
 * Reglas del modulo:
 *   - Nada de Employee en la firma publica de los endpoints.
 *   - Entrada: EmployeeRequest.
 *   - Salida:  EmployeeResponse.
 *   - EmployeeMapper hace la traduccion (inyectado por constructor).
 *
 * Analogia del mundo real:
 *   El controller es la "recepcion del hotel". El cliente entrega la ficha
 *   de registro (EmployeeRequest), el traductor (mapper) la convierte a la
 *   ficha interna del sistema (Employee), el archivador (repository) la
 *   guarda, y al cliente se le entrega una tarjeta-llave (EmployeeResponse)
 *   con solo lo que necesita saber.
 *
 * ANTES (Java 8 + Spring 4.x):
 *   @Autowired en el campo (peor testabilidad) y mapeo manual.
 *
 * AHORA (Java 21 + Spring 4.x moderno):
 *   Constructor injection (dependencias 'final'), records como DTO,
 *   MapStruct para el mapeo.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    // 'final' garantiza que la dependencia se asigne una sola vez (en el constructor)
    // y nadie la sobrescriba. Facilita razonar sobre inmutabilidad y concurrencia.
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    // PREGUNTA DE ALUMNO - "por que no uso @Autowired?"
    //   Spring 4+ recomienda constructor injection sin @Autowired. Cuando una
    //   clase tiene UN SOLO constructor, Spring lo detecta y lo usa
    //   automaticamente. Ventajas: tests unitarios sin necesidad de mocks
    //   del framework y campos 'final'.
    public EmployeeController(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * POST /api/employees
     * Crea un nuevo empleado. Devuelve 201 Created + EmployeeResponse.
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeRequest request) {
        // 1) DTO de entrada -> Entity
        Employee toSave = mapper.toEntity(request);
        // 2) Persistir (Spring Data JPA asigna el id).
        Employee saved = repository.save(toSave);
        // 3) Entity -> DTO de salida (concatena fullName, oculta internalNotes).
        EmployeeResponse body = mapper.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * GET /api/employees
     * Devuelve todos los empleados como lista de EmployeeResponse.
     *
     * ANTES (Java 8):
     *   List&lt;EmployeeResponse&gt; out = new ArrayList&lt;&gt;();
     *   for (Employee e : repository.findAll()) {
     *       out.add(mapper.toResponse(e));
     *   }
     *   return out;
     *
     * AHORA (Java 21 con streams):
     *   return repository.findAll().stream().map(mapper::toResponse).toList();
     *
     * 'mapper::toResponse' es un "method reference": una forma corta de la
     * lambda 'e -> mapper.toResponse(e)'. .toList() (Java 16+) devuelve
     * una lista inmutable, mas segura que Collectors.toList().
     */
    @GetMapping
    public List<EmployeeResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * GET /api/employees/{id}
     * Devuelve un empleado por id, o 404 si no existe.
     *
     * Optional.map + orElseGet:
     *   Si el Optional trae valor, se ejecuta la lambda y se devuelve 200 OK.
     *   Si viene vacio, se ejecuta el 'orElseGet' -> 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
