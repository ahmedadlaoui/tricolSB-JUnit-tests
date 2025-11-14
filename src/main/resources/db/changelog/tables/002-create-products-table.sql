--liquibase formatted sql

--changeset ismailbaguni:002-create-products-table runOnChange:true
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    unit_price INTEGER NOT NULL,
    category VARCHAR(255) NOT NULL,
    current_stock DOUBLE PRECISION NOT NULL,
    reorder_point DOUBLE PRECISION NOT NULL,
    unit_of_measure VARCHAR(255) NOT NULL
);