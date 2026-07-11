package com.springroadmap.dtos.controller;

import com.springroadmap.dtos.domain.Employee;
import com.springroadmap.dtos.dto.EmployeeRequest;
import com.springroadmap.dtos.dto.EmployeeResponse;
import com.springroadmap.dtos.mapper.EmployeeMapper;
import com.springroadmap.dtos.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test MockMvc en modo standalone para EmployeeController.
 *
 * Por que standalone:
 *   Spring Boot 4.1.0 removio @WebMvcTest. Se instancia el controller a mano
 *   con dependencias reales/fake y se construye MockMvc con
 *   MockMvcBuilders.standaloneSetup(controller).
 *
 * Aqui NO usamos Mockito: implementamos un EmployeeRepository fake
 * (mapa en memoria) para que el test corra sin H2 ni Spring.
 * El EmployeeMapper si es la implementacion real generada por MapStruct.
 */
class EmployeeControllerTest {

    private MockMvc mockMvc;
    private FakeEmployeeRepository repository;

    @BeforeEach
    void setUp() {
        this.repository = new FakeEmployeeRepository();
        // Mapper real (Mappers.getMapper devuelve la implementacion generada).
        EmployeeMapper mapper = org.mapstruct.factory.Mappers.getMapper(EmployeeMapper.class);
        EmployeeController controller = new EmployeeController(repository, mapper);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void POST_creaEmpleado_devuelve201_yFullNameConcatenado() throws Exception {
        String body = """
                {
                  "firstName": "Ada",
                  "lastName": "Lovelace",
                  "salary": 1500.00,
                  "hireDate": "2024-01-15"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fullName").value("Ada Lovelace"))
                .andExpect(jsonPath("$.salary").value(1500.00))
                .andExpect(jsonPath("$.hireDate").value("2024-01-15"))
                // El campo internalNotes JAMAS aparece en el JSON de respuesta.
                .andExpect(jsonPath("$.internalNotes").doesNotExist());
    }

    @Test
    void GET_porId_conEmpleadoExistente_devuelve200() throws Exception {
        // Precargamos una entity con internalNotes.
        Employee stored = new Employee(
                null, "Grace", "Hopper",
                new BigDecimal("2500.00"), LocalDate.of(2023, 6, 1),
                "notas internas confidenciales"
        );
        Employee saved = repository.save(stored);

        mockMvc.perform(get("/api/employees/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Grace Hopper"))
                .andExpect(jsonPath("$.internalNotes").doesNotExist());
    }

    // ---- Fake repository (implementa solo lo minimo que usa el controller) ----

    private static class FakeEmployeeRepository implements EmployeeRepository {
        private final Map<Long, Employee> store = new HashMap<>();
        private final AtomicLong seq = new AtomicLong(0);

        @Override public <S extends Employee> S save(S entity) {
            if (entity.getId() == null) entity.setId(seq.incrementAndGet());
            store.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<Employee> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }
        @Override public List<Employee> findAll() {
            return new ArrayList<>(store.values());
        }

        // --- El resto de metodos de JpaRepository no se usan en los tests. ---
        @Override public List<Employee> findAll(Sort sort) { throw new UnsupportedOperationException(); }
        @Override public List<Employee> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public <S extends Employee> List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void flush() { }
        @Override public <S extends Employee> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Employee> List<S> saveAllAndFlush(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch(Iterable<Employee> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch() { throw new UnsupportedOperationException(); }
        @Override public Employee getOne(Long id) { throw new UnsupportedOperationException(); }
        @Override public Employee getById(Long id) { throw new UnsupportedOperationException(); }
        @Override public Employee getReferenceById(Long id) { throw new UnsupportedOperationException(); }
        @Override public <S extends Employee> List<S> findAll(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Employee> List<S> findAll(Example<S> example, Sort sort) { throw new UnsupportedOperationException(); }
        @Override public org.springframework.data.domain.Page<Employee> findAll(org.springframework.data.domain.Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public boolean existsById(Long id) { return store.containsKey(id); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(Long id) { store.remove(id); }
        @Override public void delete(Employee entity) { if (entity.getId()!=null) store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) { ids.forEach(store::remove); }
        @Override public void deleteAll(Iterable<? extends Employee> entities) { entities.forEach(this::delete); }
        @Override public void deleteAll() { store.clear(); }
        @Override public <S extends Employee> Optional<S> findOne(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Employee> org.springframework.data.domain.Page<S> findAll(Example<S> example, org.springframework.data.domain.Pageable pageable) { throw new UnsupportedOperationException(); }
        @Override public <S extends Employee> long count(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Employee> boolean exists(Example<S> example) { throw new UnsupportedOperationException(); }
        @Override public <S extends Employee, R> R findBy(Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { throw new UnsupportedOperationException(); }
    }
}
