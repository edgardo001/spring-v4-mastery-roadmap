-- V2: crea la tabla de libros con FK a authors
CREATE TABLE books (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    author_id BIGINT       NOT NULL,
    CONSTRAINT fk_books_author FOREIGN KEY (author_id) REFERENCES authors(id)
);
