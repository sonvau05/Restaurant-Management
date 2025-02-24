package com.restaurantmanagement.app.repository;

import com.restaurantmanagement.app.entity.RevenueReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RevenueReportRepository {
    private final Connection connection;

    public RevenueReportRepository(Connection connection) {
        this.connection = connection;
    }

    public List<RevenueReport> getLast12MonthsRevenueReports() throws SQLException {
        List<RevenueReport> reports = new ArrayList<>();
        String sql = "SELECT ReportMonth, TotalRevenue, TotalQuantity " +
                "FROM RevenueReports " +
                "WHERE ReportMonth >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 11 MONTH), '%Y-%m') " +
                "ORDER BY ReportMonth ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                RevenueReport report = new RevenueReport();
                report.setReportMonth(resultSet.getString("ReportMonth"));
                report.setTotalRevenue(resultSet.getDouble("TotalRevenue"));
                report.setTotalQuantity(resultSet.getInt("TotalQuantity"));
                reports.add(report);
            }
        }
        return reports;
    }

}