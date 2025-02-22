CREATE TABLE customers
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    first_name    VARCHAR(20)           NOT NULL,
    last_name     VARCHAR(50)           NOT NULL,
    email         VARCHAR(50)           NULL,
    phone         VARCHAR(20)           NULL,
    address       VARCHAR(255)          NULL,
    date_of_birth datetime              NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id)
);

ALTER TABLE customers
    ADD CONSTRAINT uc_customers_email UNIQUE (email);

ALTER TABLE customers
    ADD CONSTRAINT uc_customers_phone UNIQUE (phone);