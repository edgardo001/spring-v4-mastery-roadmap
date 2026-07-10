-- V3: datos iniciales - 2 autores y 3 libros
INSERT INTO authors (id, name) VALUES
    (1, 'Gabriel Garcia Marquez'),
    (2, 'Isabel Allende');

INSERT INTO books (id, title, author_id) VALUES
    (1, 'Cien Anios de Soledad', 1),
    (2, 'El Amor en los Tiempos del Colera', 1),
    (3, 'La Casa de los Espiritus', 2);
