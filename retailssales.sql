CREATE DATABASE IF NOT EXISTS RetailDB;
USE RetailDB;
CREATE TABLE IF NOT EXISTS Customers (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100),
    city VARCHAR(50)
);
CREATE TABLE IF NOT EXISTS Products (
    product_id INT PRIMARY KEY,
    name VARCHAR(100),
    category VARCHAR(50),
    price DECIMAL(10,2)
);
CREATE TABLE IF NOT EXISTS Orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    date DATE,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);
CREATE TABLE IF NOT EXISTS Order_Items (
    order_id INT,
    product_id INT,
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);
INSERT INTO Customers VALUES
(1, 'Sreeyah', 'Hyderabad'),
(2, 'Harshu', 'Chennai'),
(3, 'Sreeharshu', 'Bangalore');
INSERT INTO Products VALUES
(101, 'Laptop', 'Electronics', 50000),
(102, 'Phone', 'Electronics', 20000),
(103, 'Chair', 'Furniture', 3000);
INSERT INTO Orders VALUES
(1, 1, '2026-01-10'),
(2, 2, '2026-02-15'),
(3, 1, '2026-03-01');
INSERT INTO Order_Items VALUES
(1, 101, 1),
(1, 103, 2),
(2, 102, 1),
(3, 101, 1);
SELECT 'Customer Table' AS Info;
SELECT * FROM Customers; 

SELECT 'Product Table' AS Info;
SELECT * FROM Products; 
SELECT 'Orders Table' AS Info;
SELECT * FROM Orders; 

SELECT 'Order Items Table' AS Info;
SELECT * FROM Order_Items;

-- Top-Selling Products
SELECT p.name, SUM(oi.quantity) AS total_sold
FROM Order_Items oi
JOIN Products p ON oi.product_id = p.product_id
GROUP BY p.name
ORDER BY total_sold DESC;

-- Most Valuable Customers
SELECT c.name, SUM(p.price * oi.quantity) AS total_spent
FROM Customers c
JOIN Orders o ON c.customer_id = o.customer_id
JOIN Order_Items oi ON o.order_id = oi.order_id
JOIN Products p ON oi.product_id = p.product_id
GROUP BY c.name
ORDER BY total_spent DESC;

-- Monthly Revenue
SELECT MONTH(o.date) AS month,
SUM(p.price * oi.quantity) AS revenue
FROM Orders o
JOIN Order_Items oi ON o.order_id = oi.order_id
JOIN Products p ON oi.product_id = p.product_id
GROUP BY MONTH(o.date);

-- Category-wise Sales
SELECT p.category, SUM(p.price * oi.quantity) AS sales
FROM Products p
JOIN Order_Items oi ON p.product_id = oi.product_id
GROUP BY p.category;

-- Inactive Customers
SELECT c.name
FROM Customers c
LEFT JOIN Orders o ON c.customer_id = o.customer_id
WHERE o.customer_id IS NULL;
