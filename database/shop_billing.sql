CREATE DATABASE IF NOT EXISTS shop_billing_db;
USE shop_billing_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(40) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(30) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(60),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    supplier VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (price > 0),
    CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(25),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id INT NULL,
    bill_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_bill_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bill_items (
    bill_item_id INT PRIMARY KEY AUTO_INCREMENT,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_item_bill FOREIGN KEY (bill_id) REFERENCES bills(bill_id) ON DELETE CASCADE,
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(product_id),
    CHECK (quantity > 0)
);

INSERT INTO users(username,password)
SELECT 'admin','admin123' WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

INSERT INTO products(product_code,product_name,category,price,quantity,supplier)
SELECT 'GRC-001','Rice 5 kg','Grocery',320.00,20,'Local Distributor'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code='GRC-001');
INSERT INTO products(product_code,product_name,category,price,quantity,supplier)
SELECT 'GRC-002','Cooking Oil 1 L','Grocery',145.00,18,'Local Distributor'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code='GRC-002');
INSERT INTO products(product_code,product_name,category,price,quantity,supplier)
SELECT 'STA-001','Notebook','Stationery',55.00,35,'City Book House'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code='STA-001');
INSERT INTO products(product_code,product_name,category,price,quantity,supplier)
SELECT 'HOU-001','Bath Soap','Household',38.00,24,'Local Distributor'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE product_code='HOU-001');
