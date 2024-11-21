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

CREATE TABLE IF NOT EXISTS products  (
    product_id SERIAL PRIMARY KEY,
    seller_id INT REFERENCES seller(seller_id) ON DELETE CASCADE,
    shopify_product_id VARCHAR(255) UNIQUE,
    amazon_mcf_sku VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    description TEXT,
    price DECIMAL(10, 2),
    inventory_level INT,
    reorder_threshold INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS orders  (
    order_id SERIAL PRIMARY KEY,
    seller_id INT REFERENCES seller(seller_id) ON DELETE CASCADE,
    shopify_order_id VARCHAR(255) UNIQUE,
    amazon_mcf_order_id VARCHAR(255),
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    total_price DECIMAL(10, 2),
    status VARCHAR(50) CHECK (status IN ('Pending', 'In Progress', 'Shipped', 'Delivered', 'Cancelled')),
    sla_met BOOLEAN,
    delivery_eta TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS returns  (
    return_id SERIAL PRIMARY KEY,
    order_id INT REFERENCES orders(order_id) ON DELETE CASCADE,
    reason TEXT,
    status VARCHAR(50) CHECK (status IN ('Pending', 'Approved', 'Rejected', 'Completed')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS inventory_logs  (
    log_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES products(product_id) ON DELETE CASCADE,
    change_type VARCHAR(50) CHECK (change_type IN ('Add', 'Subtract', 'Forecast')),
    quantity INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS forecasts  (
    forecast_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES products(product_id) ON DELETE CASCADE,
    forecasted_inventory INT,
    forecast_date TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

