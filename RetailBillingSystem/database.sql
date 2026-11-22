CREATE DATABASE IF NOT EXISTS retail_billing;
USE retail_billing;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL CHECK (purchase_price >= 0),
    selling_price DECIMAL(10,2) NOT NULL CHECK (selling_price >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

CREATE TABLE IF NOT EXISTS purchases (
    purchase_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    purchase_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    CONSTRAINT fk_purchase_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

CREATE TABLE IF NOT EXISTS purchase_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT fk_purchase_item_purchase
        FOREIGN KEY (purchase_id) REFERENCES purchases(purchase_id),
    CONSTRAINT fk_purchase_item_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE IF NOT EXISTS sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    sale_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    tax DECIMAL(10,2) NOT NULL CHECK (tax >= 0),
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    CONSTRAINT fk_sale_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE IF NOT EXISTS sale_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    CONSTRAINT fk_sale_item_sale
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
    CONSTRAINT fk_sale_item_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
);

INSERT INTO users (username, password, role)
SELECT 'admin', 'admin123', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO products (name, category, purchase_price, selling_price, stock)
SELECT 'Notebook', 'Stationery', 20.00, 30.00, 50
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Notebook');
INSERT INTO products (name, category, purchase_price, selling_price, stock)
SELECT 'Ball Pen', 'Stationery', 8.00, 12.00, 100
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Ball Pen');
INSERT INTO products (name, category, purchase_price, selling_price, stock)
SELECT 'Water Bottle', 'Accessories', 150.00, 220.00, 25
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Water Bottle');

INSERT INTO customers (name, phone, address)
SELECT 'Rahul Sharma', '9876543210', 'Pune'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE name = 'Rahul Sharma');
INSERT INTO customers (name, phone, address)
SELECT 'Priya Patel', '9123456780', 'Mumbai'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE name = 'Priya Patel');

INSERT INTO suppliers (name, phone, address)
SELECT 'Apex Stationery', '9988776655', 'Pune'
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name = 'Apex Stationery');
INSERT INTO suppliers (name, phone, address)
SELECT 'General Supplies', '8877665544', 'Mumbai'
WHERE NOT EXISTS (SELECT 1 FROM suppliers WHERE name = 'General Supplies');
