-- E-ticaret Entegrasyonu için Yeni Tablolar

-- Platform tablosu
DROP TABLE IF EXISTS platforms CASCADE;
CREATE TABLE platforms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    api_endpoint VARCHAR(500),
    webhook_url VARCHAR(500),
    api_key VARCHAR(500),
    api_secret VARCHAR(500),
    access_token TEXT,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    sync_enabled BOOLEAN NOT NULL DEFAULT true,
    last_sync_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Platform ürünleri tablosu
DROP TABLE IF EXISTS platform_products CASCADE;
CREATE TABLE platform_products (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    platform_product_id VARCHAR(100) NOT NULL,
    platform_sku VARCHAR(100),
    platform_title VARCHAR(500),
    platform_price DECIMAL(10,2),
    platform_stock_quantity INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_synced BOOLEAN NOT NULL DEFAULT false,
    last_sync_at TIMESTAMP,
    sync_error_message TEXT,
    retry_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, platform_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (platform_id) REFERENCES platforms(id) ON DELETE CASCADE
);

-- Siparişler tablosu
DROP TABLE IF EXISTS orders CASCADE;
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    platform_id BIGINT NOT NULL,
    customer_name VARCHAR(200),
    customer_email VARCHAR(200),
    customer_phone VARCHAR(50),
    shipping_address TEXT,
    billing_address TEXT,
    total_amount DECIMAL(10,2),
    currency VARCHAR(3) NOT NULL DEFAULT 'TRY',
    order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    platform_order_id VARCHAR(100),
    platform_order_url VARCHAR(500),
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    shipping_date TIMESTAMP,
    delivery_date TIMESTAMP,
    cancellation_date TIMESTAMP,
    cancellation_reason TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (platform_id) REFERENCES platforms(id) ON DELETE CASCADE
);

-- Sipariş kalemleri tablosu
DROP TABLE IF EXISTS order_items CASCADE;
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    platform_product_id BIGINT,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    discount_amount DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    platform_item_id VARCHAR(100),
    platform_sku VARCHAR(100),
    platform_title VARCHAR(500),
    is_processed BOOLEAN NOT NULL DEFAULT false,
    processed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (platform_product_id) REFERENCES platform_products(id) ON DELETE SET NULL
);

-- Senkronizasyon logları tablosu
DROP TABLE IF EXISTS sync_logs CASCADE;
CREATE TABLE sync_logs (
    id BIGSERIAL PRIMARY KEY,
    platform_id BIGINT NOT NULL,
    product_id BIGINT,
    sync_type VARCHAR(30) NOT NULL,
    sync_status VARCHAR(20) NOT NULL,
    sync_message TEXT,
    error_message TEXT,
    request_data TEXT,
    response_data TEXT,
    execution_time_ms BIGINT,
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retry_count INTEGER NOT NULL DEFAULT 3,
    next_retry_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (platform_id) REFERENCES platforms(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Mevcut products tablosunu güncelle
ALTER TABLE products ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE products ADD COLUMN IF NOT EXISTS sync_enabled BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE products ADD COLUMN IF NOT EXISTS last_sync_at TIMESTAMP;
ALTER TABLE products ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE products ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- İndeksler
CREATE INDEX idx_platforms_active ON platforms(is_active);
CREATE INDEX idx_platforms_sync_enabled ON platforms(sync_enabled);
CREATE INDEX idx_platform_products_platform ON platform_products(platform_id);
CREATE INDEX idx_platform_products_product ON platform_products(product_id);
CREATE INDEX idx_platform_products_synced ON platform_products(is_synced);
CREATE INDEX idx_orders_platform ON orders(platform_id);
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
CREATE INDEX idx_order_items_processed ON order_items(is_processed);
CREATE INDEX idx_sync_logs_platform ON sync_logs(platform_id);
CREATE INDEX idx_sync_logs_status ON sync_logs(sync_status);
CREATE INDEX idx_sync_logs_retry ON sync_logs(next_retry_at);

-- Trigger'lar
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_platforms_updated_at BEFORE UPDATE ON platforms
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_platform_products_updated_at BEFORE UPDATE ON platform_products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_order_items_updated_at BEFORE UPDATE ON order_items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_sync_logs_updated_at BEFORE UPDATE ON sync_logs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
