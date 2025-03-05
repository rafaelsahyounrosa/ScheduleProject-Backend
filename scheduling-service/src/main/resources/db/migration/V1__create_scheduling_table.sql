CREATE TABLE schedulings
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    start_time    datetime              NOT NULL,
    `description` VARCHAR(255)          NOT NULL,
    status        VARCHAR(255)          NOT NULL,
    customer_id   BIGINT                NOT NULL,
    company_id    BIGINT                NOT NULL,
    CONSTRAINT pk_schedulings PRIMARY KEY (id)
);