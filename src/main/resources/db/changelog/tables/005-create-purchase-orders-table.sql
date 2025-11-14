--liquibase formatted sql

--changeset ismailbaguni:005-create-purchase-orders-table runOnChange:true
CREATE TABLE IF NOT EXISTS purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    order_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DOUBLE PRECISION,
    supplier_id BIGINT NOT NULL,
    reception_date TIMESTAMP,
    CONSTRAINT fk_purchase_orders_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);