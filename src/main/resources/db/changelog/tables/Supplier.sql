--liquibase formatted sql
--changeset tricol-user:1-create-suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    ice VARCHAR(255) NOT NULL UNIQUE,
    contact_person VARCHAR(255) NOT NULL
    );