package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.Employee;
import com.restaurantmanagement.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    private final Connection connection;

    // Constructor nhận đối tượng Connection
    public EmployeeRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Lưu thông tin nhân viên vào bảng Employees.
     * Các trường: FullName, DateOfBirth, PhoneNumber, Address, Role, HireDate.
     */
    public boolean saveEmployee(String fullName, Date dateOfBirth, String phoneNumber, String address, String role, Date hireDate) {
        String query = "INSERT INTO Employees (FullName, DateOfBirth, PhoneNumber, Address, Role, HireDate) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            if (connection == null) return false;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, fullName);
                pstmt.setDate(2, dateOfBirth);
                pstmt.setString(3, phoneNumber);
                pstmt.setString(4, address);
                pstmt.setString(5, role);
                pstmt.setDate(6, hireDate);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (saveEmployee): " + ex.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách tất cả nhân viên từ bảng Employees.
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees";
        try {
            if (connection == null) return employees;
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee(
                            rs.getInt("EmployeeID"),
                            rs.getString("FullName"),
                            rs.getDate("DateOfBirth"),
                            rs.getString("PhoneNumber"),
                            rs.getString("Address"),
                            rs.getString("Role"),
                            rs.getDate("HireDate")
                    );
                    employees.add(emp);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (getAllEmployees): " + ex.getMessage());
        }
        return employees;
    }

    /**
     * Cập nhật thông tin nhân viên.
     */
    public boolean updateEmployee(Employee employee) {
        String query = "UPDATE Employees SET FullName = ?, DateOfBirth = ?, PhoneNumber = ?, Address = ?, Role = ?, HireDate = ? WHERE EmployeeID = ?";
        try {
            if (connection == null) return false;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, employee.getFullName());
                pstmt.setDate(2, employee.getDateOfBirth());
                pstmt.setString(3, employee.getPhoneNumber());
                pstmt.setString(4, employee.getAddress());
                pstmt.setString(5, employee.getRole());
                pstmt.setDate(6, employee.getHireDate());
                pstmt.setInt(7, employee.getEmployeeID());
                int rowsUpdated = pstmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (updateEmployee): " + ex.getMessage());
            return false;
        }
    }

    /**
     * Xóa nhân viên theo EmployeeID.
     */
    public boolean deleteEmployee(int employeeID) {
        String query = "DELETE FROM Employees WHERE EmployeeID = ?";
        try {
            if (connection == null) return false;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, employeeID);
                int rowsDeleted = pstmt.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (deleteEmployee): " + ex.getMessage());
            return false;
        }
    }

    /**
     * Tìm kiếm nhân viên theo tên (theo kiểu tìm kiếm chứa).
     */
    public List<Employee> getEmployeesByName(String name) {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employees WHERE FullName LIKE ?";
        try {
            if (connection == null) return employees;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, "%" + name + "%");
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Employee emp = new Employee(
                                rs.getInt("EmployeeID"),
                                rs.getString("FullName"),
                                rs.getDate("DateOfBirth"),
                                rs.getString("PhoneNumber"),
                                rs.getString("Address"),
                                rs.getString("Role"),
                                rs.getDate("HireDate")
                        );
                        employees.add(emp);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (getEmployeesByName): " + ex.getMessage());
        }
        return employees;
    }
}
