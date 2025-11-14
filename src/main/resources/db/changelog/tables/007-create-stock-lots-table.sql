--liquibase formatted sql

--changeset ismailbaguni:007-create-stock-lots-table runOnChange:true
CREATE TABLE IF NOT EXISTS stock_lots (
    id BIGSERIAL PRIMARY KEY,
    lot_number VARCHAR(255) NOT NULL UNIQUE,
    entry_date DATE NOT NULL,
    purchase_price DOUBLE PRECISION NOT NULL,
    remaining_quantity DOUBLE PRECISION NOT NULL,
    initial_quantity DOUBLE PRECISION NOT NULL,
    product_id BIGINT NOT NULL,
    purchase_order_line_id BIGINT NOT NULL,
    CONSTRAINT fk_stock_lots_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stock_lots_purchase_order_line FOREIGN KEY (purchase_order_line_id) REFERENCES purchase_order_lines(id)
);