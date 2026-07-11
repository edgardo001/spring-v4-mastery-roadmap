package com.springroadmap.hexagonal.application.service;

import com.springroadmap.hexagonal.domain.model.Customer;
import com.springroadmap.hexagonal.domain.port.out.CustomerRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test UNITARIO PURO del caso de uso. NO usa @SpringBootTest, NO carga contexto,
 * NO usa Mockito. Es POJO 100%.
 *
 * Analogía del mundo real: probamos al "operador telefónico" (CustomerService)
 * dándole un "archivero de mentira" (mock manual del port). Todo ocurre en RAM,
 * en 1 milisegundo, sin arrancar Spring ni tocar red o disco.
 *
 * Este test demuestra el mayor beneficio de la arquitectura hexagonal:
 *   - El dominio + application son testeables SIN framework.
 *   - No hace falta Mockito: implementamos el port a mano en el mismo archivo.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   Antes: la clase mock manual usaría una clase interna con getters/setters.
 *   Ahora: usamos 'AtomicReference' para capturar el argumento del save() de forma
 *          concisa. Igualmente funcionaba en Java 8.
 */
class CustomerServiceTest {

    @Test
    void register_debe_llamar_save_del_repository_y_devolver_el_customer_persistido() {
        // 1. ARRANGE: mock manual del port de salida. Capturamos lo que se pasa a save().
        AtomicReference<Customer> capturado = new AtomicReference<>();
        CustomerRepositoryPort mockRepo = new CustomerRepositoryPort() {
            @Override
            public Customer save(Customer customer) {
                capturado.set(customer);
                // Simulamos que la BD asigna id = 42.
                return new Customer(42L, customer.name(), customer.email());
            }
            @Override
            public Optional<Customer> findById(Long id) {
                return Optional.empty();
            }
        };
        // Instanciamos el service como un POJO cualquiera. Sin Spring.
        CustomerService service = new CustomerService(mockRepo);

        // 2. ACT
        Customer result = service.register("Juan", "juan@x.com");

        // 3. ASSERT
        assertNotNull(capturado.get(), "save() debió ser invocado");
        assertEquals("Juan", capturado.get().name());
        assertEquals("juan@x.com", capturado.get().email());
        // El result es lo que devolvió el mock: con id 42 asignado.
        assertEquals(42L, result.id());
        assertEquals("Juan", result.name());
    }

    @Test
    void register_debe_lanzar_IllegalArgumentException_si_name_es_null() {
        CustomerRepositoryPort mockRepo = new CustomerRepositoryPort() {
            @Override public Customer save(Customer c) { return c; }
            @Override public Optional<Customer> findById(Long id) { return Optional.empty(); }
        };
        CustomerService service = new CustomerService(mockRepo);

        // assertThrows: verifica que el bloque lanza la excepción esperada.
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.register(null, "x@x.com")
        );
        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    void register_debe_lanzar_IllegalArgumentException_si_email_es_blank() {
        CustomerRepositoryPort mockRepo = new CustomerRepositoryPort() {
            @Override public Customer save(Customer c) { return c; }
            @Override public Optional<Customer> findById(Long id) { return Optional.empty(); }
        };
        CustomerService service = new CustomerService(mockRepo);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.register("Juan", "   ")
        );
    }
}
