CREATE TABLE schedulings
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    start_time    datetime              NOT NULL,
    `description` VARCHAR(255)          NULL,
    status        VARCHAR(255)          NULL,
    customer_id   BIGINT                NULL,
    CONSTRAINT pk_schedulings PRIMARY KEY (id)
);