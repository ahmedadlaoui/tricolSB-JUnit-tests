--liquibase formatted sql

--changeset ismailbaguni:001-create-suppliers-table runOnChange:true
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    ice VARCHAR(255) NOT NULL UNIQUE,
    contact_person VARCHAR(255) NOT NULL
);