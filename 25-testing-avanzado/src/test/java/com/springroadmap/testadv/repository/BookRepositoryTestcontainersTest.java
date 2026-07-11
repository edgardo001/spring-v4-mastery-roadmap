package com.springroadmap.testadv.repository;

import com.springroadmap.testadv.domain.Book;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===================================================================================
 *  TEST DE INTEGRACIÓN CON POSTGRES REAL vía TESTCONTAINERS
 * ===================================================================================
 *
 *  Por qué esto es "EL ESTÁNDAR" del testing moderno (2020+):
 *  --------------------------------------------------------------
 *  ANTES (Java 8 / Spring 3.x era):
 *    - Se testeaba contra H2 en memoria pretendiendo ser Postgres.
 *    - H2 imita SQL 92, pero NO habla dialectos reales de Postgres/MySQL:
 *        * Tipos JSONB, ARRAY, INTERVAL, ENUM, UUID.
 *        * CTEs recursivos, ventanas avanzadas, LATERAL joins.
 *        * Índices GIN/GiST, extensiones (pg_trgm, PostGIS).
 *    - Resultado clásico: "en mi PC pasa, en staging con Postgres falla".
 *    - Alternativa peor: montar Postgres a mano en cada dev/CI. Frágil, no reproducible.
 *
 *  AHORA (Testcontainers, JUnit 5, Docker):
 *    - Cada suite de tests arranca un contenedor Postgres EFÍMERO (imagen oficial).
 *    - Al terminar los tests, el contenedor se destruye. Sin residuos.
 *    - Mismo motor, misma versión, mismo dialecto que producción.
 *    - Los tests son 100% reproducibles: laptop == CI == staging.
 *    - Cero mocks de la capa BD → confianza real en las queries.
 *
 *  Palabras clave explicadas:
 *  --------------------------
 *   - {@code @SpringBootTest}: carga TODO el contexto (repos, JPA, DataSource).
 *   - {@code @Testcontainers}: extensión JUnit 5 que gestiona el ciclo de vida del contenedor
 *     anotado con {@code @Container} (start antes de los tests, stop al final).
 *   - {@code @Container static PostgreSQLContainer}: static → un solo contenedor para toda la clase
 *     (arranca 1 vez, no 1 vez por método → tests más rápidos).
 *   - {@code postgres:16-alpine}: imagen oficial de Postgres 16, versión "alpine" (ligera).
 *   - {@code @DynamicPropertySource}: hook que inyecta propiedades en el {@code Environment}
 *     ANTES de que Spring cree el {@code DataSource}. Necesario porque la URL del contenedor
 *     solo se conoce EN RUNTIME (Docker asigna puerto aleatorio).
 *   - {@code DockerClientFactory.instance().isDockerAvailable()}: chequea si Docker está corriendo.
 *   - {@code Assumptions.assumeTrue(...)}: si la condición es falsa, JUnit marca el test como
 *     SKIPPED (no FAILED). Esto es lo que permite que el build no rompa en máquinas sin Docker.
 *
 *  ANTES vs AHORA (test de BD):
 *    ANTES: {@code @DataJpaTest} + H2 in-memory  → dialecto falso.
 *    AHORA: {@code @SpringBootTest} + {@code @Testcontainers} + Postgres real → dialecto real.
 *    (Además, {@code @DataJpaTest} fue ELIMINADO en Spring Boot 4.1.0 — ver MEMORY.md).
 */
/*
 * NOTA IMPORTANTE:
 *   Este test está anotado con @Disabled porque REQUIERE Docker corriendo
 *   (Testcontainers arranca un contenedor Postgres real). @Testcontainers
 *   fuerza el arranque del @Container ANTES de cualquier @BeforeAll, por lo
 *   que Assumptions.assumeTrue(dockerAvailable) NO llega a tiempo a saltarlo.
 *
 *   Para EJECUTAR este test manualmente (con Docker Desktop abierto):
 *     1. Quita la anotación @Disabled.
 *     2. Ejecuta: ./build.sh   (o `mvn -Dtest=BookRepositoryTestcontainersTest test`)
 *   El resto del build (compilar + contextLoads con H2) sigue verde SIN Docker.
 */
@Disabled("Requiere Docker Desktop; ver docs del archivo")
@SpringBootTest
@Testcontainers
class BookRepositoryTestcontainersTest {

    /**
     * El contenedor de Postgres. Es {@code static} para que se comparta entre todos los métodos
     * de test de esta clase (arranque único). Testcontainers se encarga de detenerlo al final.
     *
     * Nota: la etiqueta {@code "postgres:16-alpine"} descarga la imagen la primera vez
     * (~100 MB) y la cachea. Los siguientes runs son rápidos.
     */
    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    /**
     * Puente entre el contenedor efímero y la configuración de Spring.
     * Se ejecuta ANTES de que Spring cree beans → puede sobrescribir spring.datasource.*.
     *
     * {@code pg::getJdbcUrl} es un {@code Supplier<String>} (method reference).
     * Equivalente Java 8: {@code () -> pg.getJdbcUrl()}.
     */
    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
        // ddl-auto=create-drop: crea el schema al arrancar y lo borra al parar. Ideal para tests aislados.
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    // Inyección del repositorio por Spring. En un test @SpringBootTest, @Autowired es
    // aceptable (no hay ventaja del constructor injection dentro de un test).
    @Autowired
    private BookRepository repository;

    /**
     * Test principal: guarda un Book en Postgres real y lo lee de vuelta.
     * Si esto pasa, tienes CONFIANZA DE VERDAD en que tu capa de persistencia funciona
     * contra el motor de BD que usarás en producción.
     */
    @Test
    void savesAndReadsBook() {
        // Arrange — dato de ejemplo.
        Book domainDrivenDesign = new Book("Domain-Driven Design", "Eric Evans");

        // Act — persistir.
        Book saved = repository.save(domainDrivenDesign);

        // Assert — el ID lo generó Postgres (autoincrement), no nosotros.
        assertThat(saved.getId()).isNotNull();

        // Act — leer de vuelta.
        Optional<Book> found = repository.findById(saved.getId());

        // Assert — mismo contenido.
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Domain-Driven Design");
        assertThat(found.get().getAuthor()).isEqualTo("Eric Evans");

        // Sanity check adicional: findAll devuelve 1 elemento.
        List<Book> all = repository.findAll();
        assertThat(all).hasSize(1);
    }
}
