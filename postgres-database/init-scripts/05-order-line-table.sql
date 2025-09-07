
CREATE TABLE IF NOT EXISTS order_line (
    id         UUID NOT NULL PRIMARY KEY,
    order_id   UUID NOT NULL REFERENCES "order"(id),
    product_id UUID NOT NULL REFERENCES product(id)
);
