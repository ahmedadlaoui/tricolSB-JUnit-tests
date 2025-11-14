--liquibase formatted sql

--changeset ismailbaguni:006-create-purchase-order-lines-table runOnChange:true
CREATE TABLE IF NOT EXISTS purchase_order_lines (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    unit_price DOUBLE PRECISION NOT NULL,
    sub_total DOUBLE PRECISION,
    purchase_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_purchase_order_lines_purchase_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
    CONSTRAINT fk_purchase_order_lines_product FOREIGN KEY (product_id) REFERENCES products(id)
);