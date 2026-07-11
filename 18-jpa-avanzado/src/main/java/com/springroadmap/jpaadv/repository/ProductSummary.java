package com.springroadmap.jpaadv.repository;

/**
 * Interface-based Projection de Spring Data JPA.
 *
 * Qué es: una interfaz con getters que Spring Data implementa en runtime.
 * Le dice a JPA: "de la tabla `products`, solo tráeme `id` y `name`" — NO
 * cargues las demás columnas ni la relación con Category. Menos I/O, menos
 * memoria, respuestas HTTP más pequeñas.
 *
 * Analogía: en vez de pedir el EXPEDIENTE COMPLETO de un cliente, pides una
 * TARJETA de presentación con solo el nombre y el ID.
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 * <pre>
 *   // ANTES: SELECT * FROM products + mapping manual a un DTO.
 *   //   `List<ProductDto> list = ...` construido a mano con `new` en un loop.
 *   // AHORA (Spring Data): declaras una interfaz con getters y Spring
 *   //   genera la SELECT id,name FROM products + proxy que expone los
 *   //   getters. Cero boilerplate.
 * </pre>
 *
 * Casos de uso empresariales:
 * - Autocompletar en un buscador (solo id + name).
 * - Endpoints de "listar opciones" para dropdowns.
 * - Reportes ligeros donde no se necesita cargar toda la entidad.
 */
public interface ProductSummary {

    // Los nombres de los getters DEBEN coincidir con las propiedades JavaBean
    // de la entidad Product (getId → id, getName → name).
    Long getId();

    String getName();
}
