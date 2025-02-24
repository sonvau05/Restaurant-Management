package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.RevenueReport;
import com.restaurantmanagement.app.repository.RevenueReportRepository;
import com.restaurantmanagement.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RevenueReportService {

    private final RevenueReportRepository revenueReportRepository;

    public RevenueReportService() {
        Connection connection = DatabaseConnection.getConnection();
        this.revenueReportRepository = new RevenueReportRepository(connection);
    }
    
    public List<RevenueReport> getLast12MonthsRevenueReports() {
        try {
            return revenueReportRepository.getLast12MonthsRevenueReports();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

}