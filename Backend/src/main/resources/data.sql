-- USERS
INSERT INTO users (id, name, email, password_hash, role, created_at, updated_at) VALUES
(1, 'Alice', 'alice@example.com', '$2a$12$RA7CUJR8rS1lDgyE8LdXKekvp6UQUQ1bhlMlWVBZFE6VgheVs3q2i', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Bob', 'bob@example.com', '$2a$12$TjQkd4Oe0cdOIhnDuUqICO6L6UQ/YGbWff9cM3ZAeCufOyr4TiwDO', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CATEGORIES
INSERT INTO categories (id, name, description, image_url) VALUES
(1, 'Electronics', 'Electronic gadgets and devices', 'https://images.unsplash.com/photo-1498049794561-7780e7231661?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080'),
(2, 'Books', 'Printed and digital books', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080'),
(3, 'Home Appliances', 'Home and kitchen appliances', 'https://images.unsplash.com/photo-1556911220-bff31c812dba?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080'),
(4, 'Beauty Products', 'Beauty and personal care items', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080');

-- PRODUCTS
INSERT INTO products (id, name, description, price, quantity, image_url, is_active, category_id, created_at, updated_at) VALUES
(1, 'Laptop', 'High-performance laptop', 75000.00, 10, 'https://example.com/laptop.jpg', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Headphones', 'Noise-cancelling headphones', 5000.00, 25, 'https://example.com/headphones.jpg', true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Novel', 'Best-selling fiction novel', 400.00, 100, 'https://example.com/novel.jpg', true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CARTS
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CART ITEMS
INSERT INTO cart_items (id, cart_id, product_id, quantity, added_at) VALUES
(1, 1, 1, 1, CURRENT_TIMESTAMP),
(2, 1, 2, 2, CURRENT_TIMESTAMP);

-- ADDRESSES
INSERT INTO addresses (id, user_id, address_line1, address_line2, city, state, postal_code, country, phone, street, is_default, created_at, updated_at) VALUES
(1, 1, '123 Main St', '1st Apartment', 'Coimbatore', 'TN', '641001', 'India', '9876543210', 'Main Street', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '456 Market Rd', 'Peacock villa', 'Chennai', 'TN', '600001', 'India', '9123456780', 'Market Road', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PAYMENT METHODS
INSERT INTO payment_methods (id, type, provider, account_number, cardholder_name, expiry_date, is_default, created_at, updated_at, user_id) VALUES
(1, 'Card', 'Visa', '4111111111111111', 'Alice', '12/27', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(2, 'Wallet', 'PayPal', 'alice@paypal.com', 'Alice', 'N/A', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
(3, 'Card', 'MasterCard', '5555555555554444', 'Bob', '11/26', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

-- ORDERS
INSERT INTO orders (id, user_id, address_id, payment_method_id, status, total_amount, placed_at, updated_at) VALUES
(1, 1, 1, 1, 'PLACED', 80000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 2, 3, 'SHIPPED', 10000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, 1, 1, 'PENDING', 5400.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 2, 2, 3, 'PAID', 15000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 1, 1, 2, 'PROCESSING', 7500.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 2, 2, 3, 'DELIVERED', 12000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 1, 1, 1, 'CANCELLED', 3000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ORDER ITEMS
INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
(1, 1, 1, 1, 75000.00),
(2, 1, 2, 1, 5000.00),
(3, 2, 2, 2, 10000.00),
(4, 3, 3, 10, 400.00),
(5, 3, 2, 1, 5000.00),
(6, 4, 1, 1, 75000.00),
(7, 5, 2, 1, 5000.00),
(8, 6, 3, 30, 400.00),
(9, 7, 3, 5, 400.00),
(10, 7, 2, 1, 5000.00);

-- PAYMENTS
INSERT INTO payments (id, order_id, payment_method_id, amount, status, transaction_reference, created_at) VALUES
(1, 1, 1, 80000.00, 'SUCCESS', 'TXN123456', CURRENT_TIMESTAMP),
(2, 2, 3, 10000.00, 'SUCCESS', 'TXN654321', CURRENT_TIMESTAMP);

-- ANALYTICS REPORTS
INSERT INTO analytics_reports (id, generated_by, report_type, data, generated_at) VALUES
(1, 2, 'Sales Summary', 'Total sales: ₹90,000', CURRENT_TIMESTAMP);
