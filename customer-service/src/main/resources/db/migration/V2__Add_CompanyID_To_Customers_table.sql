ALTER TABLE customers ADD COLUMN company_id BIGINT NOT NULL;
CREATE INDEX idx_customers_company_id ON customers(company_id);