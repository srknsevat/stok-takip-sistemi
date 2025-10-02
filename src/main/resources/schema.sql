DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    color VARCHAR(20) NOT NULL,
    icon VARCHAR(50) NOT NULL
);

DROP TABLE IF EXISTS products;
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL,
    creation_date DATE NOT NULL,
    categories VARCHAR(255)
);

DROP TABLE IF EXISTS stock_movements;
CREATE TABLE stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    movement_date TIMESTAMP NOT NULL,
    movement_type VARCHAR(10) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
); 