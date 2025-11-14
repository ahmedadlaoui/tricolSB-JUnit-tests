--liquibase formatted sql

--changeset ismailbaguni:004-create-goods-issue-lines-table runOnChange:true
CREATE TABLE IF NOT EXISTS goods_issue_lines (
    id BIGSERIAL PRIMARY KEY,
    quantity DOUBLE PRECISION NOT NULL,
    goods_issue_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_goods_issue_lines_goods_issue FOREIGN KEY (goods_issue_id) REFERENCES goods_issues(id),
    CONSTRAINT fk_goods_issue_lines_product FOREIGN KEY (product_id) REFERENCES products(id)
);