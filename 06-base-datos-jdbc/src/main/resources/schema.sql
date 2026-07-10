-- schema.sql: DDL ejecutado automáticamente por Spring Boot al arrancar
-- (gracias a spring.sql.init.mode=always). H2 es case-insensitive por defecto.
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    id    BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);
