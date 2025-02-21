-- 📌 Tạo cơ sở dữ liệu
CREATE DATABASE IF NOT EXISTS Restaurant;
USE Restaurant;

-- 1. Bảng danh mục nguyên liệu (IngredientCategories)
CREATE TABLE IF NOT EXISTS IngredientCategories (
                                                    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
                                                    Name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2. Bảng danh mục món ăn (Categories)
CREATE TABLE IF NOT EXISTS Categories (
                                          CategoryID INT PRIMARY KEY AUTO_INCREMENT,
                                          Name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 3. Bảng loại giao dịch kho (TransactionTypes)
CREATE TABLE IF NOT EXISTS TransactionTypes (
                                                TransactionTypeID INT PRIMARY KEY AUTO_INCREMENT,
                                                TypeName ENUM('Nhập kho', 'Điều chỉnh tồn kho', 'Hủy nguyên liệu') NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 4. Bảng người dùng (Users)
CREATE TABLE IF NOT EXISTS Users (
                                     UserID INT PRIMARY KEY AUTO_INCREMENT,
                                     Username VARCHAR(50) UNIQUE NOT NULL,
                                     PasswordHash VARCHAR(255) NOT NULL,
                                     Role ENUM('MANAGER', 'CHEF', 'CASHIER') NOT NULL,
                                     CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 5. Bảng nhân viên (Employees)
CREATE TABLE IF NOT EXISTS Employees (
                                         EmployeeID INT PRIMARY KEY AUTO_INCREMENT,
                                         FullName VARCHAR(100) NOT NULL,
                                         DateOfBirth DATE,
                                         PhoneNumber VARCHAR(15),
                                         Address TEXT,
                                         Role ENUM('MANAGER', 'CHEF', 'CASHIER', 'SERVICE') NOT NULL,
                                         HireDate DATE NOT NULL
) ENGINE=InnoDB;

-- 6. Bảng lí do nghỉ phép (LeaveRecords)
CREATE TABLE IF NOT EXISTS LeaveRecords (
                                            LeaveID INT PRIMARY KEY AUTO_INCREMENT,
                                            EmployeeID INT NOT NULL,
                                            StartDate DATE NOT NULL,
                                            EndDate DATE NOT NULL,
                                            Reason TEXT,
                                            FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
) ENGINE=InnoDB;

-- 7. Bảng thực đơn (MenuItems) - cần tham chiếu đến Categories
CREATE TABLE IF NOT EXISTS MenuItems (
                                         ItemID INT PRIMARY KEY AUTO_INCREMENT,
                                         Name VARCHAR(100) NOT NULL,
                                         Price DECIMAL(10, 2) NOT NULL,
                                         Description TEXT,
                                         CategoryID INT NOT NULL,
                                         CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Bảng đơn hàng (Orders)
CREATE TABLE IF NOT EXISTS Orders (
                                      OrderID INT PRIMARY KEY AUTO_INCREMENT,
                                      TotalAmount DECIMAL(10, 2) NOT NULL,
                                      OrderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      Status ENUM('PENDING', 'COMPLETED', 'CANCELLED', 'PAID') DEFAULT 'PENDING'
) ENGINE=InnoDB;

-- 9. Bảng chi tiết đơn hàng (OrderDetails) - tham chiếu đến Orders và MenuItems
CREATE TABLE IF NOT EXISTS OrderDetails (
                                            OrderDetailID INT PRIMARY KEY AUTO_INCREMENT,
                                            OrderID INT NOT NULL,
                                            ItemID INT NOT NULL,
                                            Quantity INT NOT NULL,
                                            Price DECIMAL(10, 2) NOT NULL,
                                            FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE,
                                            FOREIGN KEY (ItemID) REFERENCES MenuItems(ItemID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. Bảng báo cáo doanh thu (RevenueReports)
CREATE TABLE IF NOT EXISTS RevenueReports (
                                              ReportID INT PRIMARY KEY AUTO_INCREMENT,
                                              ReportMonth VARCHAR(7) NOT NULL,  -- Ví dụ: '2025-02'
                                              TotalRevenue DECIMAL(10, 2) NOT NULL,
                                              TotalQuantity INT NOT NULL
) ENGINE=InnoDB;

-- 11. Bảng nguyên liệu (Ingredients) - tham chiếu đến IngredientCategories
CREATE TABLE IF NOT EXISTS Ingredients (
                                           IngredientID INT PRIMARY KEY AUTO_INCREMENT,
                                           Name VARCHAR(255) NOT NULL,
                                           CategoryID INT,
                                           Unit VARCHAR(50) NOT NULL,
                                           Stock FLOAT NOT NULL DEFAULT 0,
                                           MinStock FLOAT NOT NULL DEFAULT 0,
                                           PricePerUnit DECIMAL(10,2) NOT NULL,
                                           FOREIGN KEY (CategoryID) REFERENCES IngredientCategories(CategoryID) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 12. Bảng giao dịch kho (InventoryTransactions) - tham chiếu đến Ingredients và TransactionTypes
CREATE TABLE IF NOT EXISTS InventoryTransactions (
                                                     TransactionID INT PRIMARY KEY AUTO_INCREMENT,
                                                     TransactionTypeID INT NOT NULL,
                                                     SupplierName VARCHAR(255),
                                                     IngredientID INT NOT NULL,
                                                     Quantity FLOAT NOT NULL,
                                                     Unit VARCHAR(50) NOT NULL,
                                                     Price DECIMAL(10,2),
                                                     Note TEXT,
                                                     TransactionDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID) ON DELETE CASCADE,
                                                     FOREIGN KEY (TransactionTypeID) REFERENCES TransactionTypes(TransactionTypeID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 13. Bảng lưu trữ tồn kho hàng ngày (DailyStock) - tham chiếu đến Ingredients
CREATE TABLE IF NOT EXISTS DailyStock (
                                          StockID INT PRIMARY KEY AUTO_INCREMENT,
                                          IngredientID INT NOT NULL,
                                          Name VARCHAR(255) NOT NULL,
                                          CategoryID INT,
                                          Unit VARCHAR(50) NOT NULL,
                                          Stock FLOAT NOT NULL,
                                          MinStock FLOAT NOT NULL,
                                          PricePerUnit DECIMAL(10,2) NOT NULL,
                                          Date DATE NOT NULL,
                                          FOREIGN KEY (IngredientID) REFERENCES Ingredients(IngredientID) ON DELETE CASCADE,
                                          UNIQUE (IngredientID, Date)
) ENGINE=InnoDB;

