package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.RevenueReport;
import com.restaurantmanagement.database.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueReportRepository {

    public List<RevenueReport> getRevenueReports() {
        List<RevenueReport> reports = new ArrayList<>();
        String query = "SELECT ReportID, ReportMonth, TotalRevenue, TotalQuantity FROM RevenueReports";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int reportID = resultSet.getInt("ReportID");
                String reportMonth = resultSet.getString("ReportMonth");
                BigDecimal totalRevenue = resultSet.getBigDecimal("TotalRevenue");
                int totalQuantity = resultSet.getInt("TotalQuantity");

                RevenueReport report = new RevenueReport(reportID, reportMonth, totalRevenue.doubleValue(), totalQuantity);
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }
}
