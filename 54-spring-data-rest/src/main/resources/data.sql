-- Datos iniciales cargados por Spring Boot al arrancar (spring.sql.init.mode=always).
INSERT INTO authors (id, name) VALUES (1, 'Martin Fowler');
INSERT INTO authors (id, name) VALUES (2, 'Robert C. Martin');

INSERT INTO books (id, title, author_id) VALUES (1, 'Refactoring', 1);
INSERT INTO books (id, title, author_id) VALUES (2, 'Clean Code', 2);
