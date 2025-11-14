--liquibase formatted sql
--changeset tricol-user:6-create-goods-issue-lines
CREATE TABLE IF NOT EXISTS goods_issue_lines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity DOUBLE NOT NULL,
    goods_issue_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT fk_gil_goods_issue FOREIGN KEY (goods_issue_id) REFERENCES goods_issues(id),
    CONSTRAINT fk_gil_product FOREIGN KEY (product_id) REFERENCES products(id)
    );