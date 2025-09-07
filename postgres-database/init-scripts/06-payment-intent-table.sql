
CREATE TABLE IF NOT EXISTS payment_intent (
    id         UUID NOT NULL PRIMARY KEY,
    order_id   UUID NOT NULL REFERENCES "order"(id),
    stripe_id  TEXT NOT NULL,
    amount_gbx BIGINT NOT NULL
);
