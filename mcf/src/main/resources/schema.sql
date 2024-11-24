
CREATE TABLE IF NOT EXISTS seller  (
    seller_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    shopify_store_url VARCHAR(255),
    amazon_mcf_account_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS products (
    product_id BIGINT PRIMARY KEY, -- Changed from SERIAL to match Long productId
    title VARCHAR(255),
    product_type VARCHAR(255),
    vendor VARCHAR(255),
    description TEXT,
    price DECIMAL(10, 2),
    inventory_level INT,
    amazon_mcf_sku VARCHAR(255) UNIQUE,
    published_at TIMESTAMP,
    updated_at TIMESTAMP
);


-- CREATE TYPE order_status AS ENUM ('PENDING', 'IN_PROGRESS', 'SHIPPED', 'DELIVERED', 'CANCELLED');
-- 🔴 `IF NOT EXISTS` is not supported for enum types in PostgreSQL, so we use below approach 🔴

-- Create enum type safely
-- DO 
-- $$ 
-- BEGIN
--     IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_status') THEN
--         CREATE TYPE order_status AS ENUM (
--             'PENDING',
--             'IN_PROGRESS', 
--             'SHIPPED',
--             'DELIVERED',
--             'CANCELLED'
--         );
--     END IF;
-- END
-- $$;

CREATE TABLE IF NOT EXISTS orders  (
    order_id BIGINT PRIMARY KEY,
    seller_id INT REFERENCES seller(seller_id),
    order_name VARCHAR(255),
    amazon_mcf_order_id VARCHAR(255),
    customer_name VARCHAR(255),
    email VARCHAR(255),
    current_total_price DECIMAL(10, 2),
    fulfillment_status order_status DEFAULT 'PENDING',
    sla_met BOOLEAN,
    delivery_eta TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    processed_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS returns  (
    return_id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(order_id),
    note TEXT,
    status VARCHAR(50) CHECK (status IN ('Pending', 'Approved', 'Rejected', 'Completed')),
    processed_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS inventory_logs  (
    log_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES products(product_id),
    change_type VARCHAR(50) CHECK (change_type IN ('Add', 'Subtract', 'Forecast')),
    quantity INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS forecasts  (
    forecast_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES products(product_id),
    forecasted_inventory INT,
    forecast_date TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);


