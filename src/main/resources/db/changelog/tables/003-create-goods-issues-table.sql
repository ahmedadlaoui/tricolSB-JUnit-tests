--liquibase formatted sql

--changeset ismailbaguni:003-create-goods-issues-table runOnChange:true
CREATE TABLE IF NOT EXISTS goods_issues (
    id BIGSERIAL PRIMARY KEY,
    issue_number VARCHAR(255) NOT NULL UNIQUE,
    issue_date DATE NOT NULL,
    destination VARCHAR(255) NOT NULL,
    motif VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    validation_date TIMESTAMP
);