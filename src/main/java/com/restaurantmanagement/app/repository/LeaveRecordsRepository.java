package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.LeaveRecords;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaveRecordsRepository {

    private final Connection connection;

    public LeaveRecordsRepository(Connection connection) {
        this.connection = connection;
    }

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
            System.out.println("ERROR SQL (saveLeaveRecords): " + ex.getMessage());
            return false;
        }
    }

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
            System.out.println("ERROR SQL (getLeaveRecordsByEmployeeId): " + ex.getMessage());
        }
        return records;
    }
}
