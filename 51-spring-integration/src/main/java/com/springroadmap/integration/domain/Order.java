package com.springroadmap.integration.domain;

/**
 * `Order` - Orden de compra que viaja como payload por el flujo de integración.
 *
 * <h2>Analogía</h2>
 * Es la <b>carta</b> que metes en el sobre (`Message`). Los headers del sobre
 * (id, timestamp) son metadatos; el `Order` es el contenido.
 *
 * <h2>ANTES (Java 8) vs AHORA (Java 21)</h2>
 * <pre>
 * // ANTES: clase POJO con constructor, getters, equals, hashCode, toString (30+ líneas)
 * public final class Order {
 *     private final String id;
 *     private final String product;
 *     private final int quantity;
 *     public Order(String id, String product, int quantity) {
 *         this.id = id; this.product = product; this.quantity = quantity;
 *     }
 *     public String getId() { return id; }
 *     public String getProduct() { return product; }
 *     public int getQuantity() { return quantity; }
 *     // + equals, hashCode, toString...
 * }
 *
 * // AHORA (Java 14+): un record de una línea genera todo lo anterior automáticamente.
 * public record Order(String id, String product, int quantity) {}
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿Un record es inmutable?" R: Sí. Sus campos son `final`
 * implícitamente. No hay setters. Si quieres modificar uno, creas otro record con
 * el método `withXxx()` (que tú mismo escribes) o pasas los valores nuevos al
 * constructor.</p>
 *
 * @param id       identificador único de la orden (ej. "ORD-001").
 * @param product  nombre del producto (ej. "Notebook").
 * @param quantity cantidad de unidades (ej. 3).
 */
public record Order(String id, String product, int quantity) {
    // El cuerpo del record puede estar vacío. Java genera:
    //   - Constructor canónico Order(String, String, int)
    //   - Accessors id(), product(), quantity()
    //   - equals(), hashCode() basados en los 3 campos
    //   - toString() del tipo "Order[id=ORD-001, product=Notebook, quantity=3]"
}
