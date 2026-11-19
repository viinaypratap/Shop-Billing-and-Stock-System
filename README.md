# Shop Billing and Stock System

**Student Name:** Vinay Pratap  
**Registration Number:** PIET24CR031  
**Project Title:** Shop Billing and Stock System

## Objective

A small Java desktop application for maintaining shop products and customers, preparing bills, and updating stock when a sale is completed.

## Features

- Product add, edit, delete, search and stock listing
- Customer add, edit, delete and search
- Billing with multiple products and quantity validation against current database stock
- Transactional bill save: bill, bill items and stock deductions commit together or roll back together
- Previous bill list with item details
- Dashboard counts for products, low stock, today's bills and today's sales
- Basic database-backed login

## Technology Used

- Java (Swing/AWT)
- JDBC
- MySQL

## Software Requirements

- JDK 8 or later
- MySQL Server 8.x (InnoDB tables)
- MySQL Connector/J driver JAR
- An IDE such as IntelliJ IDEA, Eclipse or NetBeans, or a Java command line

## Database Setup

1. Start MySQL and run `database/shop_billing.sql` in MySQL Workbench or the MySQL command line.
2. The base script creates `shop_billing_db`, the required tables, an admin login and a few starter products.
3. Run `database/indian_demo_data.sql` to load the Jaipur grocery-store demo catalog, customers and seven linked previous bills. It replaces only the four original unbilled sample products, leaves the users table alone, and can be rerun without duplicating its customers or bills.
4. In `src/shopbilling/db/DatabaseConnection.java`, update `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` for your local MySQL installation. Keep local database credentials private.

## How to Configure Database Credentials

Edit the three constants in `DatabaseConnection.java`. The default URL uses MySQL on `localhost:3306` and the database name `shop_billing_db`. Keep your own password local and do not commit it.

## How to Run the Project

Add MySQL Connector/J to the project classpath. In an IDE, mark `src` as the source folder, add the connector JAR as a library, and run `shopbilling.Main`.

From a terminal, with the Connector/J JAR path substituted for `mysql-connector-j.jar`:

```text
javac -cp "mysql-connector-j.jar" -d out src/shopbilling/Main.java src/shopbilling/db/*.java src/shopbilling/model/*.java src/shopbilling/dao/*.java src/shopbilling/ui/*.java src/shopbilling/util/*.java
java -cp "out;mysql-connector-j.jar" shopbilling.Main
```

On Linux/macOS, replace the semicolon between classpath entries with a colon.

## Default Login Credentials

- Username: `admin`
- Password: `admin123`

These credentials are for the local lab database created by the supplied SQL file. This mini project stores the sample login password directly to keep the JDBC login example understandable; use password hashing in a real application.

## Project Structure

```text
ShopBillingAndStockSystem/
├── database/shop_billing.sql
├── screenshots/
├── src/shopbilling/
│   ├── Main.java
│   ├── dao/
│   ├── db/
│   ├── model/
│   ├── ui/
│   └── util/
├── .gitignore
└── README.md
```

## Important Functionality

`BillDAO.completeSale` opens a JDBC transaction, inserts the bill, then conditionally deducts each product quantity using `UPDATE ... WHERE quantity >= ?`. A changed row count other than one is treated as insufficient stock. Bill items are inserted and the transaction commits only after all operations succeed; an exception triggers rollback. The billing screen also checks stock when an item is added, including quantities already in the current bill. The database check at sale time handles stock changes made by another session. Deleting a customer leaves the bill record in history with its customer reference cleared.

## Future Scope

- Password hashing and user management
- Printable bill receipts
- Date range sales reports
- Import/export of product data

## Notes

The `screenshots` folder is reserved for application screenshots. No screenshots are included because they require a configured MySQL database and a running desktop session.
