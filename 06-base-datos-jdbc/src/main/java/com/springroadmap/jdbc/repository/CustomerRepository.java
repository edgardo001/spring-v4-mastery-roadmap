package com.springroadmap.jdbc.repository;

import com.springroadmap.jdbc.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Repositorio CRUD para la tabla `customers` usando JdbcTemplate puro.
 *
 * Analogía: JdbcTemplate es un "traductor" que habla el idioma SQL con la
 * base de datos por nosotros; solo hay que darle la sentencia y los parámetros,
 * y él se encarga de abrir/cerrar conexiones, mapear resultados y traducir
 * las SQLException (excepciones "chequeadas" horribles de manejar) a la
 * jerarquía DataAccessException de Spring (RuntimeException).
 *
 * PREGUNTA DE ALUMNO — "¿Por qué usar @Repository y no @Component?"
 *   Semánticamente indica que es una capa de acceso a datos, y activa la
 *   traducción automática de excepciones SQL a DataAccessException.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   - RowMapper como clase anónima (Java 8) vs lambda expresiva (Java 21).
 *   - if (x != null) ... else null (Java 8) vs Optional (moderno).
 */
@Repository
public class CustomerRepository {

    private static final Logger log = LoggerFactory.getLogger(CustomerRepository.class);

    // SQL como constantes: legibilidad + reutilización + facilita revisión de queries.
    private static final String SQL_FIND_ALL   = "SELECT id, name, email FROM customers ORDER BY id";
    private static final String SQL_FIND_BY_ID = "SELECT id, name, email FROM customers WHERE id = ?";
    private static final String SQL_INSERT     = "INSERT INTO customers (name, email) VALUES (?, ?)";
    private static final String SQL_DELETE     = "DELETE FROM customers WHERE id = ?";

    // final: la referencia no cambia tras el constructor -> thread-safe y claro.
    private final JdbcTemplate jdbcTemplate;

    /**
     * RowMapper: traduce una fila del ResultSet a un objeto Customer.
     *
     * ANTES (Java 8):
     *   private final RowMapper<Customer> mapper = new RowMapper<Customer>() {
     *       public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
     *           return new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("email"));
     *       }
     *   };
     * AHORA (Java 21): una lambda.
     */
    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> new Customer(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("email")
    );

    /**
     * Constructor injection (buena práctica). @Autowired se puede omitir cuando
     * hay un único constructor, pero lo dejamos explícito para reforzar el concepto.
     */
    @Autowired
    public CustomerRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Lista todos los customers. `query` maneja 0..N resultados sin fallar. */
    public List<Customer> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, customerRowMapper);
    }

    /**
     * Busca por id. Devuelve Optional para representar el "no encontrado" sin
     * lanzar excepciones ni retornar null.
     *
     * Edge case: queryForObject lanza EmptyResultDataAccessException si NO
     * hay resultados. Lo capturamos y devolvemos Optional.empty().
     */
    public Optional<Customer> findById(final Long id) {
        try {
            final Customer c = jdbcTemplate.queryForObject(SQL_FIND_BY_ID, customerRowMapper, id);
            return Optional.ofNullable(c);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Customer id={} no encontrado", id);
            return Optional.empty();
        }
    }

    /**
     * Inserta un customer y devuelve un nuevo Customer con el id generado por la BD.
     *
     * Uso de KeyHolder + PreparedStatementCreator: es el patrón estándar de
     * Spring JDBC para recuperar claves autogeneradas por AUTO_INCREMENT.
     */
    public Customer save(final Customer customer) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            // Statement.RETURN_GENERATED_KEYS le dice al driver: "guárdame el id que generes".
            final PreparedStatement ps = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.name());
            ps.setString(2, customer.email());
            return ps;
        }, keyHolder);

        final Number generatedId = Objects.requireNonNull(keyHolder.getKey(), "No se generó id para el customer");
        final Long newId = generatedId.longValue();
        log.info("Customer insertado con id={}", newId);
        return new Customer(newId, customer.name(), customer.email());
    }

    /** Borra por id. Devuelve true si borró alguna fila. */
    public boolean deleteById(final Long id) {
        final int rows = jdbcTemplate.update(SQL_DELETE, id);
        return rows > 0;
    }
}
