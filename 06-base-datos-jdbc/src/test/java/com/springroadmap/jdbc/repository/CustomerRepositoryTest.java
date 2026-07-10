package com.springroadmap.jdbc.repository;

import com.springroadmap.jdbc.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración del CustomerRepository con H2 real.
 *
 * Usamos @SpringBootTest (Spring Boot 4.1.0 NO tiene @DataJdbcTest ni
 * @JdbcTest disponibles como slice-test standalone en este stack pedagógico),
 * lo que arranca el contexto completo con:
 *   - schema.sql -> crea la tabla customers.
 *   - data.sql   -> inserta 2 registros (Ada, Alan).
 */
@SpringBootTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    void findAll_contieneLosCustomersSemilla() {
        // Nota: usamos "contains" en vez de "hasSize(2)" porque otros tests
        // de la misma suite pueden insertar registros adicionales antes de
        // que este corra (los @SpringBootTest comparten el contexto y por
        // tanto la BD H2 in-memory dentro de una misma clase de test).
        final List<Customer> all = repository.findAll();
        assertThat(all).extracting(Customer::name)
                .contains("Ada Lovelace", "Alan Turing");
    }

    @Test
    void save_asignaIdGenerado() {
        final Customer input = new Customer(null, "Grace Hopper", "grace@example.com");
        final Customer saved = repository.save(input);

        assertThat(saved.id()).isNotNull().isPositive();
        assertThat(saved.name()).isEqualTo("Grace Hopper");

        // Comprobamos que efectivamente quedó guardado.
        final Optional<Customer> reloaded = repository.findById(saved.id());
        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().email()).isEqualTo("grace@example.com");
    }

    @Test
    void findById_conIdInexistente_devuelveOptionalVacio() {
        final Optional<Customer> result = repository.findById(999_999L);
        assertThat(result).isEmpty();
    }

    @Test
    void findById_conIdExistente_devuelveCustomer() {
        // El seed inserta al menos un customer con id=1.
        final Optional<Customer> result = repository.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Ada Lovelace");
    }
}
