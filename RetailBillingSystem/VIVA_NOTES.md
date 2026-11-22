# Viva Notes

## Project explanation

This project is a Java desktop retail billing and inventory management system. It demonstrates a three-layer architecture: Swing screens handle input and display, service classes validate business rules and coordinate operations, and DAO classes use prepared JDBC statements to access MySQL. It supports login, product/customer/supplier master data, purchases that increase stock, sales that reduce stock, invoices, sales history, inventory warnings, report export, and file demonstrations. Purchase and sale records are saved as transactions so stock and billing data remain consistent.

## 9-step demo script

1. Start the app and log in with `admin` / `admin123`.
2. Open Products and add a product with its purchase price, selling price and initial stock.
3. Open Suppliers and add a supplier for later purchases.
4. Open Purchases, choose the supplier and product, buy 10 units, and save the purchase.
5. Open Products or Inventory and confirm the product stock increased by exactly 10.
6. Open Customers and confirm a customer is available, or use the quick-add option in Billing.
7. Open Billing, select the customer and product, add quantity 2, verify subtotal plus 5% tax, and generate the bill.
8. Show the invoice, print or save it, confirm stock decreased by 2, and open Sales History to find the sale.
9. Explain the GUI -> Service -> DAO -> MySQL flow, BigDecimal calculations, transactions, PreparedStatements and OOP concepts.

## Likely viva questions

1. **What is OOP?**  
   Object-oriented programming organizes a program around classes and objects that combine data and behaviour.

2. **OOP vs procedural programming?**  
   Procedural code focuses on functions and shared data; OOP focuses on objects, encapsulation and relationships.

3. **Which four pillars are used?**  
   Encapsulation in model getters/setters, inheritance in `User` subclasses, abstraction in `User`/`Payment`, and polymorphism in overridden methods/interfaces.

4. **Why use an abstract class instead of an interface?**  
   An abstract class provides shared fields and partial implementation; interfaces here define capabilities such as `Billable` and `Printable`.

5. **Why are interfaces used for Sale?**  
   `Sale` can be billed and printed, and interfaces let the application use a capability without depending on the concrete class.

6. **Why PreparedStatement?**  
   It prevents parameter concatenation, reduces SQL-injection risk and safely binds values.

7. **Why BigDecimal for money?**  
   It provides exact decimal arithmetic and avoids floating-point rounding errors.

8. **Why transactions in purchase and sale saving?**  
   All parent records, line items and stock changes must succeed together. Failure rolls them all back.

9. **What does setAutoCommit(false) do?**  
   It prevents individual SQL statements from committing until the complete operation commits.

10. **What happens when insufficient stock is found?**  
    The DAO throws `InsufficientStockException`; the transaction is rolled back and no sale or stock change remains.

11. **What are the GUI, Service and DAO layers?**  
    GUI handles Swing interaction, Service validates and applies business rules, and DAO executes SQL.

12. **Why is SQL kept out of GUI event handlers?**  
    It separates display code from database code and makes validation, testing and maintenance easier.

13. **How does threading work here?**  
    `ReportTask` implements `Runnable`; a new `Thread` runs the export, while `SwingUtilities.invokeLater` updates the Swing status label safely.

14. **What is serialization used for?**  
    `Product` implements `Serializable`, allowing `ObjectOutputStream` and `ObjectInputStream` to demonstrate a product backup file.

15. **How are invoice files handled?**  
    `BufferedWriter` saves invoice text and `BufferedReader` reads saved invoice text for display.
