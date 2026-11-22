# OOP Concept Map

| Syllabus unit | Viva demonstration | Exact class and method |
|---|---|---|
| Unit I - Object-oriented introduction | Encapsulation, model objects, constructors | `com.retail.model.Product` / fields plus constructors and getters/setters |
| Unit II - Classes, objects and inheritance | Abstract `User` hierarchy, method overriding, dynamic dispatch | `com.retail.model.User`, `Admin`, `Employee`; `User.getRole()` and subclasses |
| Unit III - Arrays and Strings | Product array/low-stock sorting, string methods and StringBuffer | `com.retail.gui.ProductFrame.showTopLowStock()`; `com.retail.DemoConcepts.main()` |
| Unit IV - Interfaces, polymorphism and abstract classes | Interfaces and abstract payment hierarchy | `com.retail.interfaces.Billable`, `Printable`; `com.retail.model.Payment`, `CashPayment`, `CardPayment` |
| Unit V - Exception handling | Checked/unchecked exceptions, try/catch/finally and rollback rethrow | `com.retail.exceptions.InsufficientStockException`; `BillingDAO.saveSale()`; `ProductFrame.readForm()` |
| Unit VI - Files and I/O | Invoice text files and product object serialization | `InvoiceDialog.saveInvoice()`; `SalesHistoryFrame.viewSavedInvoice()`; `ProductFrame.backupProducts()`/`viewBackup()` |
| Unit VII - AWT and Swing | Login, dashboard, forms, tables and dialogs | `com.retail.gui.LoginFrame`, `Dashboard`, `ProductFrame`, `CustomerFrame`, `SupplierFrame` |
| Unit VIII - Applets | No Applet used in the desktop Swing application | Separate, only if required |
| Unit IX - Threads | Background report export with status updates and synchronization | `com.retail.threads.ReportTask.run()`; `SalesHistoryFrame.exportSalesReport()` |
