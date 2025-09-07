
CREATE TABLE IF NOT EXISTS "order" (
    id             UUID NOT NULL PRIMARY KEY,
    customer_id    UUID NOT NULL REFERENCES customer(id),
    payment_status PAYMENT_STATUS NOT NULL
);
