--liquibase formatted sql
--changeset tricol-user:2-create-products
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    unit_price INT NOT NULL,
    category VARCHAR(255) NOT NULL,
    current_stock DOUBLE NOT NULL DEFAULT 0.0,
    reorder_point DOUBLE NOT NULL,
    unit_of_measure VARCHAR(255) NOT NULL
    );