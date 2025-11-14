--liquibase formatted sql
--changeset tricol-user:7-create-stock-lots
CREATE TABLE IF NOT EXISTS stock_lots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_number VARCHAR(255) NOT NULL UNIQUE,
    entry_date DATE NOT NULL,
    purchase_price DOUBLE NOT NULL,
    remaining_quantity DOUBLE NOT NULL,
    initial_quantity DOUBLE NOT NULL,
    product_id BIGINT NOT NULL,
    purchase_order_line_id BIGINT NOT NULL,
    CONSTRAINT fk_sl_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_sl_purchase_order_line FOREIGN KEY (purchase_order_line_id) REFERENCES purchase_order_lines(id)
    );