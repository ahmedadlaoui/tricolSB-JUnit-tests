--liquibase formatted sql
--changeset tricol-user:4-create-purchase-orders
CREATE TABLE IF NOT EXISTS purchase_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    total_amount DOUBLE,
    supplier_id BIGINT NOT NULL,
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
    );