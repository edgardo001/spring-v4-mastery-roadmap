-- Seed inicial para demo del módulo 18-jpa-avanzado.
-- Se ejecuta después de que Hibernate crea el esquema (ver
-- `spring.jpa.defer-datasource-initialization: true`).

INSERT INTO categories (id, name) VALUES (1, 'Electronica');
INSERT INTO categories (id, name) VALUES (2, 'Libros');

INSERT INTO products (id, name, price, category_id) VALUES (1, 'Laptop Pro',   1500.00, 1);
INSERT INTO products (id, name, price, category_id) VALUES (2, 'Mouse Basic',    25.00, 1);
INSERT INTO products (id, name, price, category_id) VALUES (3, 'Spring in Action', 45.00, 2);
INSERT INTO products (id, name, price, category_id) VALUES (4, 'Clean Code',     120.00, 2);
