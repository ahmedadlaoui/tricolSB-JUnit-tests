--liquibase formatted sql
--changeset tricol-user:3-create-goods-issues
CREATE TABLE IF NOT EXISTS goods_issues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    issue_number VARCHAR(255) NOT NULL UNIQUE,
    issue_date DATE NOT NULL,
    destination VARCHAR(255) NOT NULL,
    motif VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL
    );