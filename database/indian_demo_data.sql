-- The users table is intentionally untouched so the existing admin login remains configured.
USE shop_billing_db;
START TRANSACTION;

-- Replace only the four original project sample rows when they have not been
-- used by a bill. Other product records are left untouched.
DELETE p FROM products p
LEFT JOIN bill_items bi ON bi.product_id = p.product_id
WHERE bi.bill_item_id IS NULL
  AND ((p.product_code = 'GRC-001' AND p.product_name = 'Rice 5 kg')
    OR (p.product_code = 'GRC-002' AND p.product_name = 'Cooking Oil 1 L')
    OR (p.product_code = 'HOU-001' AND p.product_name = 'Bath Soap')
    OR (p.product_code = 'STA-001' AND p.product_name = 'Notebook'));

INSERT INTO products (product_code, product_name, category, price, quantity, supplier) VALUES
('GRC-001', 'Rice 5 kg', 'Grocery', 349.00, 64, 'Jaipur Wholesale Mart'),
('GRC-002', 'Aashirvaad Atta 5 kg', 'Grocery', 289.00, 52, 'Rajasthan General Suppliers'),
('GRC-003', 'Tata Salt 1 kg', 'Grocery', 24.00, 145, 'Shree Balaji Distributors'),
('GRC-004', 'Tata Sampann Chana Dal 1 kg', 'Grocery', 124.00, 38, 'Shree Ganesh Traders'),
('GRC-005', 'Moong Dal 1 kg', 'Grocery', 139.00, 31, 'Maheshwari Wholesale'),
('GRC-006', 'Toor Dal 1 kg', 'Grocery', 178.00, 27, 'Jai Ambey Enterprises'),
('GRC-007', 'Sugar 1 kg', 'Grocery', 48.00, 82, 'Krishna Distributors'),
('BEV-001', 'Tata Tea Gold 250 g', 'Beverages', 138.00, 42, 'Shree Ram Agencies'),
('BEV-002', 'Nescafe Classic 50 g', 'Beverages', 179.00, 24, 'City Wholesale Jaipur'),
('DAI-001', 'Amul Taaza Milk 1 L', 'Dairy', 68.00, 76, 'Jaipur Dairy Distributors'),
('DAI-002', 'Amul Butter 100 g', 'Dairy', 62.00, 5, 'Maa Vaishno Traders'),
('SNK-001', 'Parle-G Biscuits', 'Snacks', 10.00, 160, 'Shree Balaji Distributors'),
('SNK-002', 'Britannia Marie Gold 250 g', 'Snacks', 30.00, 58, 'Rajasthan General Suppliers'),
('SNK-003', 'Maggi 2-Minute Noodles', 'Snacks', 14.00, 132, 'Krishna Distributors'),
('SNK-004', 'Haldiram Aloo Bhujia 200 g', 'Snacks', 48.00, 34, 'City Wholesale Jaipur'),
('PER-001', 'Dettol Handwash 200 ml', 'Personal Care', 99.00, 3, 'Jai Ambey Enterprises'),
('PER-002', 'Lux Soap 100 g', 'Personal Care', 38.00, 74, 'Shree Ganesh Traders'),
('PER-003', 'Dove Shampoo 180 ml', 'Personal Care', 189.00, 22, 'Maheshwari Wholesale'),
('PER-004', 'Colgate Strong Teeth 150 g', 'Personal Care', 112.00, 41, 'Maa Vaishno Traders'),
('PER-005', 'Clinic Plus Shampoo 175 ml', 'Personal Care', 128.00, 29, 'Shree Ram Agencies'),
('CLN-001', 'Surf Excel Matic 1 kg', 'Cleaning', 249.00, 26, 'Rajasthan General Suppliers'),
('CLN-002', 'Vim Dishwash Bar', 'Cleaning', 20.00, 96, 'Jaipur Wholesale Mart'),
('CLN-003', 'Vim Liquid 500 ml', 'Cleaning', 115.00, 33, 'Shree Balaji Distributors'),
('CLN-004', 'Harpic Toilet Cleaner 500 ml', 'Cleaning', 112.00, 4, 'Krishna Distributors'),
('CLN-005', 'Lizol Floor Cleaner 500 ml', 'Cleaning', 124.00, 19, 'Jai Ambey Enterprises'),
('HOU-001', 'Cello Water Bottle 1 L', 'Household', 179.00, 28, 'City Wholesale Jaipur'),
('HOU-002', 'Good Knight Mosquito Repellent Refill', 'Household', 98.00, 5, 'Shree Ram Agencies'),
('HOU-003', 'Tissue Paper Pack', 'Household', 55.00, 47, 'Maheshwari Wholesale'),
('HOU-004', 'Matchbox Pack', 'Household', 18.00, 88, 'Shree Ganesh Traders'),
('HOU-005', 'LED Bulb 9 W', 'Household', 99.00, 36, 'Maa Vaishno Traders'),
('STA-001', 'Classmate Notebook', 'Stationery', 62.00, 54, 'City Wholesale Jaipur'),
('STA-002', 'Ball Pen', 'Stationery', 12.50, 100, 'Jaipur Wholesale Mart'),
('KIT-001', 'Aluminium Foil 9 m', 'Kitchen Essentials', 88.00, 23, 'Shree Balaji Distributors'),
('KIT-002', 'Garbage Bags Medium Pack', 'Kitchen Essentials', 79.00, 32, 'Krishna Distributors')
ON DUPLICATE KEY UPDATE
    product_name = VALUES(product_name), category = VALUES(category), price = VALUES(price),
    supplier = VALUES(supplier);

INSERT INTO customers (customer_name, phone, address)
SELECT 'Rohan Sharma', '9876500101', '24, Mahima Nagar, Mansarovar, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9876500101');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Priya Verma', '9829000202', '17, Shanti Vihar, Vaishali Nagar, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9829000202');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Amit Mehta', '9782400303', '8, Nandpuri Colony, Malviya Nagar, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9782400303');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Neha Singh', '9712300404', '31, Gokul Vatika, Jagatpura, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9712300404');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Rahul Gupta', '9698700505', '12, Shree Vihar, Pratap Nagar, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9698700505');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Ananya Joshi', '9587600606', '44, Surya Nagar, Vidhyadhar Nagar, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9587600606');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Vikram Yadav', '9498700707', '6, Rishi Colony, Tonk Road, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9498700707');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Pooja Sharma', '9123400808', '19, Shanti Path, Sodala, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9123400808');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Arjun Singh', '9834000909', '27, Ganesh Vihar, Shastri Nagar, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9834000909');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Kavita Jain', '9765401010', '52, Gopal Bari, Raja Park, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9765401010');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Mohit Agarwal', '9654301111', '14, Govind Nagar, Jhotwara, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9654301111');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Sneha Gupta', '9543201212', '39, Durgapura Extension, Durgapura, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9543201212');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Manish Saini', '9432101313', '11, Mahaveer Marg, C-Scheme, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9432101313');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Nisha Rathore', '9176501414', '23, Shyam Colony, Sanganer, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9176501414');
INSERT INTO customers (customer_name, phone, address)
SELECT 'Karan Choudhary', '9890101515', '5, Ajmer Road Residency, Ajmer Road, Jaipur'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE phone = '9890101515');

-- These seven numbers belong only to this seed. Remove their old detail lines
-- before rebuilding the demo bills so rerunning this file stays consistent.
DELETE FROM bills
WHERE bill_number IN ('BIL-2026-001','BIL-2026-002','BIL-2026-003','BIL-2026-004',
                      'BIL-2026-005','BIL-2026-006','BIL-2026-007');

INSERT INTO bills (bill_number, customer_id, bill_date, total_amount)
SELECT 'BIL-2026-001', customer_id, '2026-09-30 11:24:00', 429.00 FROM customers WHERE phone='9876500101';
INSERT INTO bills (bill_number, customer_id, bill_date, total_amount)
SELECT 'BIL-2026-002', customer_id, '2026-10-01 13:05:00', 560.00 FROM customers WHERE phone='9829000202';
INSERT INTO bills (bill_number, customer_id, bill_date, total_amount)
SELECT 'BIL-2026-003', customer_id, '2026-10-02 10:42:00', 639.00 FROM customers WHERE phone='9782400303';
INSERT INTO bills (bill_number, customer_id, bill_date, total_amount)
SELECT 'BIL-2026-004', customer_id, '2026-10-03 17:18:00', 484.00 FROM customers WHERE phone='9712300404';
INSERT INTO bills (bill_number, customer_id, bill_date, total_amount)
SELECT 'BIL-2026-005', customer_id, '2026-10-04 12:36:00', 407.00 FROM customers WHERE phone='9765401010';
INSERT INTO bills (bill_number, customer_id, bill_date, total_amount)
SELECT 'BIL-2026-006', customer_id, '2026-10-05 15:51:00', 776.00 FROM customers WHERE phone='9698700505';
INSERT INTO bills (bill_number, customer_id, bill_date, total_amount)
SELECT 'BIL-2026-007', customer_id, '2026-10-06 11:20:00', 406.00 FROM customers WHERE phone='9587600606';

INSERT INTO bill_items (bill_id, product_id, quantity, price, subtotal)
SELECT b.bill_id, p.product_id, v.quantity, p.price, p.price * v.quantity
FROM (SELECT 'BIL-2026-001' bill_number, 'GRC-002' product_code, 1 quantity UNION ALL
      SELECT 'BIL-2026-001','GRC-003',2 UNION ALL SELECT 'BIL-2026-001','SNK-001',3 UNION ALL SELECT 'BIL-2026-001','DAI-002',1 UNION ALL
      SELECT 'BIL-2026-002','CLN-001',1 UNION ALL SELECT 'BIL-2026-002','PER-001',1 UNION ALL SELECT 'BIL-2026-002','PER-002',2 UNION ALL SELECT 'BIL-2026-002','DAI-001',2 UNION ALL
      SELECT 'BIL-2026-003','GRC-001',1 UNION ALL SELECT 'BIL-2026-003','GRC-007',2 UNION ALL SELECT 'BIL-2026-003','BEV-001',1 UNION ALL SELECT 'BIL-2026-003','SNK-003',4 UNION ALL
      SELECT 'BIL-2026-004','GRC-006',1 UNION ALL SELECT 'BIL-2026-004','GRC-005',1 UNION ALL SELECT 'BIL-2026-004','HOU-003',1 UNION ALL SELECT 'BIL-2026-004','PER-004',1 UNION ALL
      SELECT 'BIL-2026-005','HOU-001',1 UNION ALL SELECT 'BIL-2026-005','HOU-002',1 UNION ALL SELECT 'BIL-2026-005','CLN-004',1 UNION ALL SELECT 'BIL-2026-005','HOU-004',1 UNION ALL
      SELECT 'BIL-2026-006','GRC-002',1 UNION ALL SELECT 'BIL-2026-006','GRC-004',1 UNION ALL SELECT 'BIL-2026-006','DAI-001',3 UNION ALL SELECT 'BIL-2026-006','SNK-002',2 UNION ALL SELECT 'BIL-2026-006','HOU-005',1 UNION ALL
      SELECT 'BIL-2026-007','CLN-003',1 UNION ALL SELECT 'BIL-2026-007','CLN-002',2 UNION ALL SELECT 'BIL-2026-007','PER-003',1 UNION ALL SELECT 'BIL-2026-007','STA-001',1) v
JOIN bills b ON b.bill_number = v.bill_number
JOIN products p ON p.product_code = v.product_code;

COMMIT;
