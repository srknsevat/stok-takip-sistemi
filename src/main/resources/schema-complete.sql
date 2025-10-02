-- Complete Database Schema for Stok Takip Sistemi

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(20),
    icon VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Products table (existing)
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(15,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    categories VARCHAR(500),
    code VARCHAR(50) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    sync_enabled BOOLEAN DEFAULT TRUE,
    last_sync_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Stock movements table (existing)
CREATE TABLE IF NOT EXISTS stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    movement_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Material Cards table
CREATE TABLE IF NOT EXISTS material_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(50) NOT NULL UNIQUE,
    material_name VARCHAR(200) NOT NULL,
    description TEXT,
    material_type VARCHAR(50) NOT NULL,
    material_category VARCHAR(50) NOT NULL,
    unit_of_measure VARCHAR(20),
    current_stock DECIMAL(15,3) DEFAULT 0,
    min_stock_level DECIMAL(15,3) DEFAULT 0,
    max_stock_level DECIMAL(15,3) DEFAULT 0,
    reorder_point DECIMAL(15,3) DEFAULT 0,
    reorder_quantity DECIMAL(15,3) DEFAULT 0,
    standard_cost DECIMAL(15,4) DEFAULT 0,
    average_cost DECIMAL(15,4) DEFAULT 0,
    last_purchase_cost DECIMAL(15,4) DEFAULT 0,
    supplier_code VARCHAR(50),
    supplier_name VARCHAR(200),
    supplier_contact VARCHAR(200),
    lead_time_days INT,
    storage_location VARCHAR(100),
    storage_conditions VARCHAR(500),
    hazardous_material BOOLEAN DEFAULT FALSE,
    batch_controlled BOOLEAN DEFAULT FALSE,
    serial_controlled BOOLEAN DEFAULT FALSE,
    quality_grade VARCHAR(50),
    certification_required BOOLEAN DEFAULT FALSE,
    inspection_required BOOLEAN DEFAULT FALSE,
    weight DECIMAL(10,3),
    dimensions VARCHAR(100),
    color VARCHAR(50),
    brand VARCHAR(100),
    model VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    is_obsolete BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Bill of Materials table
CREATE TABLE IF NOT EXISTS bill_of_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_material_id BIGINT NOT NULL,
    child_material_id BIGINT NOT NULL,
    quantity DECIMAL(15,4) NOT NULL,
    bom_type VARCHAR(50) NOT NULL,
    bom_level INT DEFAULT 1,
    operation_sequence INT DEFAULT 0,
    operation_name VARCHAR(200),
    work_center VARCHAR(100),
    unit_cost DECIMAL(15,4) DEFAULT 0,
    total_cost DECIMAL(15,4) DEFAULT 0,
    setup_time DECIMAL(10,2) DEFAULT 0,
    run_time DECIMAL(10,2) DEFAULT 0,
    total_time DECIMAL(10,2) DEFAULT 0,
    efficiency DECIMAL(5,2) DEFAULT 100.0,
    scrap_factor DECIMAL(5,2) DEFAULT 0,
    alternative_bom VARCHAR(50),
    is_primary BOOLEAN DEFAULT FALSE,
    is_phantom BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    bom_path VARCHAR(1000),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (parent_material_id) REFERENCES material_cards(id),
    FOREIGN KEY (child_material_id) REFERENCES material_cards(id)
);

-- Material Stock Movements table
CREATE TABLE IF NOT EXISTS material_stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL,
    movement_type VARCHAR(50) NOT NULL,
    quantity DECIMAL(15,4) NOT NULL,
    unit_cost DECIMAL(15,4) DEFAULT 0,
    total_cost DECIMAL(15,4) DEFAULT 0,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reference_number VARCHAR(100),
    reference_type VARCHAR(50),
    batch_number VARCHAR(100),
    serial_number VARCHAR(100),
    expiry_date TIMESTAMP,
    location VARCHAR(100),
    reason VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (material_id) REFERENCES material_cards(id)
);

-- Platforms table
CREATE TABLE IF NOT EXISTS platforms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    api_endpoint VARCHAR(500),
    webhook_url VARCHAR(500),
    api_key TEXT,
    api_secret TEXT,
    access_token TEXT,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    sync_enabled BOOLEAN DEFAULT TRUE,
    last_sync_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Platform Products table
CREATE TABLE IF NOT EXISTS platform_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    platform_product_id VARCHAR(100) NOT NULL,
    platform_sku VARCHAR(100),
    platform_title VARCHAR(200),
    platform_price DECIMAL(15,2),
    platform_stock_quantity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_synced BOOLEAN DEFAULT FALSE,
    last_sync_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (platform_id) REFERENCES platforms(id),
    UNIQUE KEY unique_platform_product (platform_id, platform_product_id)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform_id BIGINT NOT NULL,
    platform_order_id VARCHAR(100) NOT NULL,
    order_number VARCHAR(100),
    customer_name VARCHAR(200),
    customer_email VARCHAR(200),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    total_amount DECIMAL(15,2) DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'TRY',
    shipping_address TEXT,
    billing_address TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (platform_id) REFERENCES platforms(id)
);

-- Order Items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    platform_product_id VARCHAR(100),
    quantity INT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Sync Logs table
CREATE TABLE IF NOT EXISTS sync_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    platform_id BIGINT NOT NULL,
    sync_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    message TEXT,
    sync_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration_ms BIGINT,
    records_processed INT DEFAULT 0,
    records_success INT DEFAULT 0,
    records_failed INT DEFAULT 0,
    FOREIGN KEY (platform_id) REFERENCES platforms(id)
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Indexes for performance
CREATE INDEX idx_material_cards_code ON material_cards(material_code);
CREATE INDEX idx_material_cards_name ON material_cards(material_name);
CREATE INDEX idx_material_cards_type ON material_cards(material_type);
CREATE INDEX idx_material_cards_category ON material_cards(material_category);
CREATE INDEX idx_material_cards_supplier ON material_cards(supplier_code);
CREATE INDEX idx_material_cards_storage ON material_cards(storage_location);

CREATE INDEX idx_bom_parent ON bill_of_materials(parent_material_id);
CREATE INDEX idx_bom_child ON bill_of_materials(child_material_id);
CREATE INDEX idx_bom_type ON bill_of_materials(bom_type);
CREATE INDEX idx_bom_level ON bill_of_materials(bom_level);
CREATE INDEX idx_bom_active ON bill_of_materials(is_active);

CREATE INDEX idx_stock_movements_material ON material_stock_movements(material_id);
CREATE INDEX idx_stock_movements_type ON material_stock_movements(movement_type);
CREATE INDEX idx_stock_movements_date ON material_stock_movements(movement_date);
CREATE INDEX idx_stock_movements_batch ON material_stock_movements(batch_number);
CREATE INDEX idx_stock_movements_serial ON material_stock_movements(serial_number);

CREATE INDEX idx_platform_products_product ON platform_products(product_id);
CREATE INDEX idx_platform_products_platform ON platform_products(platform_id);
CREATE INDEX idx_platform_products_synced ON platform_products(is_synced);

CREATE INDEX idx_orders_platform ON orders(platform_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(status);

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

CREATE INDEX idx_sync_logs_platform ON sync_logs(platform_id);
CREATE INDEX idx_sync_logs_type ON sync_logs(sync_type);
CREATE INDEX idx_sync_logs_date ON sync_logs(sync_date);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);