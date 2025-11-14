--liquibase formatted sql
--changeset tricol-user:5-create-purchase-order-lines
CREATE TABLE IF NOT EXISTS purchase_order_lines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    purchase_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_pol_purchase_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
    CONSTRAINT fk_pol_product FOREIGN KEY (product_id) REFERENCES products(id)
    );