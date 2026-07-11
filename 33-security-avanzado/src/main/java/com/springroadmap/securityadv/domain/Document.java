package com.springroadmap.securityadv.domain;

/**
 * Documento del dominio.
 *
 * <p><b>Analogia:</b> un archivo en una carpeta compartida: tiene id,
 * contenido y un dueño (owner). Solo el dueño (o un ADMIN) deberia
 * poder verlo o borrarlo.</p>
 *
 * <h3>ANTES (Java 8) vs AHORA (Java 21)</h3>
 * <pre>
 * // ANTES (Java 8): clase POJO con getters/setters/equals/hashCode/toString
 * //   public final class Document {
 * //       private final Long id;
 * //       private final String content;
 * //       private final String owner;
 * //       public Document(Long id, String content, String owner) { ... }
 * //       public Long getId() { return id; }
 * //       public String getContent() { return content; }
 * //       public String getOwner() { return owner; }
 * //       // + equals, hashCode, toString (30+ lineas)
 * //   }
 *
 * // AHORA (Java 21): un record en UNA linea.
 * //   Genera automaticamente: constructor canonico, accessors
 * //   (id(), content(), owner()), equals, hashCode y toString.
 * </pre>
 *
 * <p>PREGUNTA DE ALUMNO — "¿por que el accessor se llama {@code owner()}
 * y no {@code getOwner()}?"</p>
 * <p>Los records de Java 14+ usan la convencion sin el prefijo {@code get}.
 * Es el nombre del componente. Spring EL en {@code @PostAuthorize} lo
 * reconoce igual: {@code returnObject.owner} funciona porque Spring EL
 * resuelve tanto {@code getOwner()} como el accessor {@code owner()}.</p>
 */
public record Document(Long id, String content, String owner) {
}
