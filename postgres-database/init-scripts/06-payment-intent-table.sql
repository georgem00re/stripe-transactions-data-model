
CREATE TABLE IF NOT EXISTS payment_intent (
    id         UUID NOT NULL PRIMARY KEY,
    order_id   UUID NOT NULL REFERENCES "order"(id),
    amount_gbx BIGINT NOT NULL
);
