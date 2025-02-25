CREATE DATABASE IF NOT EXISTS Restaurant;
USE Restaurant;

CREATE TABLE IF NOT EXISTS IngredientCategories (
                                                    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
                                                    Name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Categories (
                                          CategoryID INT PRIMARY KEY AUTO_INCREMENT,
                                          Name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS TransactionTypes (
                                                TransactionTypeID INT PRIMARY KEY AUTO_INCREMENT,
                                                TypeName ENUM('Stock In', 'Stock Adjustment', 'Ingredient Cancellation') NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Users (
                                     UserID INT PRIMARY KEY AUTO_INCREMENT,
                                     Username VARCHAR(50) UNIQUE NOT NULL,
                                     PasswordHash VARCHAR(255) NOT NULL,
                                     Role ENUM('MANAGER', 'CHEF', 'CASHIER') NOT NULL,
                                     CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Employees (
                                         EmployeeID INT PRIMARY KEY AUTO_INCREMENT,
                                         FullName VARCHAR(100) NOT NULL,
                                         DateOfBirth DATE,
                                         PhoneNumber VARCHAR(15),
                                         Address TEXT,
                                         Role ENUM('MANAGER', 'CHEF', 'CASHIER', 'SERVICE') NOT NULL,
                                         HireDate DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS LeaveRecords (
                                            LeaveID INT PRIMARY KEY AUTO_INCREMENT,
                                            EmployeeID INT NOT NULL,
                                            StartDate DATE NOT NULL,
                                            EndDate DATE NOT NULL,
                                            Reason TEXT,
                                            FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS MenuItems (
                                         ItemID INT PRIMARY KEY AUTO_INCREMENT,
                                         Name VARCHAR(100) NOT NULL,
                                         Price DECIMAL(10, 2) NOT NULL,
                                         Description TEXT,
                                         CategoryID INT NOT NULL,
                                         CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Orders (
                                      OrderID INT PRIMARY KEY AUTO_INCREMENT,
                                      TotalAmount DECIMAL(10, 2) NOT NULL,
                                      OrderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      Status ENUM('PENDING', 'COMPLETED', 'CANCELLED', 'PAID') DEFAULT 'PENDING'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS OrderDetails (
                                            OrderDetailID INT PRIMARY KEY AUTO_INCREMENT,
                                            OrderID INT NOT NULL,
                                            ItemID INT NOT NULL,
                                            Quantity INT NOT NULL,
                                            Price DECIMAL(10, 2) NOT NULL,
                                            FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE,
                                            FOREIGN KEY (ItemID) REFERENCES MenuItems(ItemID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS RevenueReports (
                                              ReportID INT PRIMARY KEY AUTO_INCREMENT,
                                              ReportMonth VARCHAR(7) NOT NULL,
                                              TotalRevenue DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
                                              TotalQuantity INT NOT NULL DEFAULT 0,
                                              UNIQUE KEY unique_month (ReportMonth)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Ingredients (
                                           IngredientID INT PRIMARY KEY AUTO_INCREMENT,
                                           Name VARCHAR(255) NOT NULL,
                                           CategoryID INT,
                                           Unit VARCHAR(50) NOT NULL,
                                           Stock FLOAT NOT NULL DEFAULT 0,
                                           MinStock FLOAT NOT NULL DEFAULT 0,
                                           PricePerUnit DECIMAL(10, 2) NOT NULL,
                                           FOREIGN KEY (CategoryID) REFERENCES IngredientCategories(CategoryID) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS InventoryTransactions (
                                                     TransactionID INT PRIMARY KEY AUTO_INCREMENT,
                                                     TransactionTypeID INT NOT NULL,
                                                     SupplierName VARCHAR(255),
                                                     IngredientID INT NOT NULL,
                                                     Quantity FLOAT NOT NULL,
                                                     Unit VARCHAR(50) NOT NULL,
                                                     Price DECIMAL(10, 2),
                                                     Note TEXT,
                                                     TransactionDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID) ON DELETE CASCADE,
                                                     FOREIGN KEY (TransactionTypeID) REFERENCES TransactionTypes(TransactionTypeID) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS DailyStock (
                                          StockID INT PRIMARY KEY AUTO_INCREMENT,
                                          IngredientID INT NOT NULL,
                                          Name VARCHAR(255) NOT NULL,
                                          CategoryID INT,
                                          Unit VARCHAR(50) NOT NULL,
                                          Stock FLOAT NOT NULL,
                                          MinStock FLOAT NOT NULL,
                                          PricePerUnit DECIMAL(10, 2) NOT NULL,
                                          Date DATE NOT NULL,
                                          FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID) ON DELETE CASCADE,
                                          UNIQUE (IngredientID, Date)
) ENGINE=InnoDB;

DELIMITER //

CREATE TRIGGER UpdateRevenueAfterOrderComplete
    AFTER UPDATE ON Orders
    FOR EACH ROW
BEGIN
    DECLARE month_key VARCHAR(7);
    DECLARE total_amount DECIMAL(10, 2);
    DECLARE total_quantity INT;

    IF NEW.Status = 'COMPLETED' AND OLD.Status != 'COMPLETED' THEN
        SET month_key = DATE_FORMAT(NEW.OrderDate, '%Y-%m');
        SELECT SUM(Quantity) INTO total_quantity
        FROM OrderDetails
        WHERE OrderID = NEW.OrderID;
        SET total_amount = NEW.TotalAmount;
        IF EXISTS (SELECT 1 FROM RevenueReports WHERE ReportMonth = month_key) THEN
            UPDATE RevenueReports
            SET TotalRevenue = TotalRevenue + total_amount,
                TotalQuantity = TotalQuantity + total_quantity
            WHERE ReportMonth = month_key;
        ELSE
            INSERT INTO RevenueReports (ReportMonth, TotalRevenue, TotalQuantity)
            VALUES (month_key, total_amount, total_quantity);
        END IF;
    END IF;
END //

DELIMITER ;



