# Retail Billing & Inventory Management System

Java 21 desktop application for retail purchase, inventory, billing and sales management.

## Requirements

- JDK 21
- Maven
- MySQL 8

## Database setup

1. Start MySQL.
2. Create and populate the database:
   ```bash
   mysql -u root -p < database.sql
   ```
3. If MySQL prompts for a password and the password is non-empty, enter it.
4. Open `src/main/java/com/retail/database/DBConnection.java`.
5. Change these constants when needed:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/retail_billing";
   private static final String USER = "root";
   private static final String PASSWORD = "";
   ```

The sample login is:

- Username: `admin`
- Password: `admin123`

## Build and run

```bash
# Run these commands from the RetailBillingSystem project folder.
mvn test
mvn exec:java
```

Run the syllabus concepts demo:

```bash
mvn package
java -cp "target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=classpath.txt && cat classpath.txt)" com.retail.DemoConcepts
```

Generated report and invoice files are stored under `reports/` and `invoices/`. Product backups are stored under `backup/products.ser`.
