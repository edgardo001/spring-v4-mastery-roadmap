package com.springroadmap.hexagonal.application.service;

import com.springroadmap.hexagonal.domain.model.Customer;
import com.springroadmap.hexagonal.domain.port.in.RegisterCustomerUseCase;
import com.springroadmap.hexagonal.domain.port.out.CustomerRepositoryPort;
// @Service es una especialización de @Component. Le dice a Spring:
// "esta clase es un bean de la capa de aplicación/servicios de negocio".
import org.springframework.stereotype.Service;

/**
 * Implementación del CASO DE USO 'Registrar Cliente'.
 *
 * Analogía del mundo real: es el "operador telefónico" del banco. Recibe la
 * orden ("registrar a Juan con juan@x.com"), aplica las reglas de negocio
 * (validaciones, orquestación) y llama al archivero (repositorio) para guardar.
 *
 * ¿Por qué esta clase vive en 'application' y NO en 'domain'?
 *   - Puristas hexagonales: podría ir en 'domain.usecase' sin @Service, y un
 *     @Configuration en infraestructura la crearía como @Bean manual.
 *   - Pragmatismo (adoptado aquí): la ponemos en 'application' y le dejamos
 *     @Service para aprovechar el escaneo de Spring. El 'domain' sigue 100% puro
 *     (modelo + puertos sin Spring).
 *
 * ¿Por qué IMPLEMENTA la interfaz RegisterCustomerUseCase?
 *   - El controlador dependerá de la INTERFAZ, no de esta clase concreta.
 *   - Así podemos mockearla fácilmente en tests y sustituirla en el futuro.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   La estructura de una clase con constructor injection NO cambió. En Java 21
 *   podríamos declarar 'private final' de igual forma. La única mejora estilística
 *   moderna es que las interfaces del dominio podrían ser 'sealed' si quisiéramos
 *   restringir quién las implementa.
 */
@Service
public class CustomerService implements RegisterCustomerUseCase {

    // 'private final': el campo se asigna una sola vez (en el constructor) y no
    // cambia jamás. Esto se llama "constructor injection" y es la forma preferida
    // en Spring moderno: hace la clase inmutable y trivial de testear (basta pasar
    // un mock del port por constructor).
    private final CustomerRepositoryPort customerRepositoryPort;

    /**
     * Constructor. Spring detecta un solo constructor y le pasa automáticamente
     * el bean que implementa CustomerRepositoryPort (nuestro InMemoryCustomerRepository).
     * No hace falta @Autowired desde Spring 4.3+.
     */
    public CustomerService(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    /**
     * Aplica el caso de uso 'Registrar Cliente'.
     *
     * Flujo:
     *   1. Valida entrada (regla de negocio mínima).
     *   2. Construye el objeto de dominio.
     *   3. Delega la persistencia al puerto de salida.
     *   4. Retorna el resultado.
     *
     * '@Override' avisa al compilador: "este método viene de una interfaz o clase padre".
     * Si nos equivocamos en la firma, el compilador falla en vez de crear un método nuevo.
     */
    @Override
    public Customer register(String name, String email) {
        // Regla de negocio simple: nombre y email obligatorios.
        // PREGUNTA DE ALUMNO — "¿Y por qué no uso @Valid?" Porque @Valid es una
        // anotación de infraestructura (Jakarta Validation). Aquí, dentro del use case,
        // la validación es lógica pura Java, no depende de ningún framework.
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name requerido");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email requerido");
        }

        // Construimos el Customer sin id (null). El adaptador de persistencia
        // asignará el id (autoincrement). Al llamar 'new Customer(null, ...)' se
        // ejecuta el constructor canónico del record.
        Customer nuevo = new Customer(null, name, email);

        // Delegamos al puerto de salida. No sabemos ni nos importa si guarda en
        // memoria, MySQL o un CSV: la interfaz nos lo oculta.
        return customerRepositoryPort.save(nuevo);
    }
}
