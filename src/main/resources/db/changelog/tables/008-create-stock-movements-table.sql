--liquibase formatted sql

--changeset ismailbaguni:008-create-stock-movements-table runOnChange:true
CREATE TABLE IF NOT EXISTS stock_movements (
    id BIGSERIAL PRIMARY KEY,
    movement_date DATE NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    movement_type VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    stock_lot_id BIGINT,
    goods_issue_line_id BIGINT,
    purchase_order_line_id BIGINT,
    CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stock_movements_stock_lot FOREIGN KEY (stock_lot_id) REFERENCES stock_lots(id),
    CONSTRAINT fk_stock_movements_goods_issue_line FOREIGN KEY (goods_issue_line_id) REFERENCES goods_issue_lines(id),
    CONSTRAINT fk_stock_movements_purchase_order_line FOREIGN KEY (purchase_order_line_id) REFERENCES purchase_order_lines(id)
);