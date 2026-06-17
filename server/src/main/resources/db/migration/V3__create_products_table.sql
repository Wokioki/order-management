CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          description VARCHAR(1000),
                          price NUMERIC(10, 2) NOT NULL,
                          stock_quantity INTEGER NOT NULL,
                          image_url VARCHAR(1000),
                          category_id BIGINT NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_products_category
                              FOREIGN KEY (category_id)
                                  REFERENCES categories(id),

                          CONSTRAINT chk_products_price_positive
                              CHECK (price > 0),

                          CONSTRAINT chk_products_stock_quantity_non_negative
                              CHECK (stock_quantity >= 0)
);