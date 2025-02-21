package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.LeaveRecords;
import com.restaurantmanagement.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaveRecordsRepository {

    private final Connection connection;

    // Constructor nhận đối tượng Connection
    public LeaveRecordsRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Lưu thông tin nghỉ phép vào bảng LeaveRecords.
     * Các trường: EmployeeID, StartDate, EndDate, Reason.
     */
    public boolean saveLeaveRecords(int employeeID, Date startDate, Date endDate, String reason) {
        String query = "INSERT INTO LeaveRecords (EmployeeID, StartDate, EndDate, Reason) VALUES (?, ?, ?, ?)";
        try {
            if (connection == null) return false;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, employeeID);
                pstmt.setDate(2, startDate);
                pstmt.setDate(3, endDate);
                pstmt.setString(4, reason);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (saveLeaveRecords): " + ex.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách tất cả LeaveRecords từ bảng LeaveRecords.
     */
    public List<LeaveRecords> getAllLeaveRecords() {
        List<LeaveRecords> records = new ArrayList<>();
        String query = "SELECT * FROM LeaveRecords";
        try {
            if (connection == null) return records;
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LeaveRecords record = new LeaveRecords(
                            rs.getInt("LeaveID"),
                            rs.getInt("EmployeeID"),
                            rs.getDate("StartDate"),
                            rs.getDate("EndDate"),
                            rs.getString("Reason")
                    );
                    records.add(record);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (getAllLeaveRecords): " + ex.getMessage());
        }
        return records;
    }

    /**
     * Lấy danh sách LeaveRecords theo EmployeeID.
     */
    public List<LeaveRecords> getLeaveRecordsByEmployeeId(int employeeID) {
        List<LeaveRecords> records = new ArrayList<>();
        String query = "SELECT * FROM LeaveRecords WHERE EmployeeID = ?";
        try {
            if (connection == null) return records;
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, employeeID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        LeaveRecords record = new LeaveRecords(
                                rs.getInt("LeaveID"),
                                rs.getInt("EmployeeID"),
                                rs.getDate("StartDate"),
                                rs.getDate("EndDate"),
                                rs.getString("Reason")
                        );
                        records.add(record);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Lỗi SQL (getLeaveRecordsByEmployeeId): " + ex.getMessage());
        }
        return records;
    }
}
