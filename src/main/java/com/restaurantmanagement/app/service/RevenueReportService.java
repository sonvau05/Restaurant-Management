package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.RevenueReport;
import com.restaurantmanagement.app.repository.RevenueReportRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RevenueReportService {
    private final RevenueReportRepository revenueReportRepository;

    public RevenueReportService(Connection connection) {
        this.revenueReportRepository = new RevenueReportRepository();
    }

    public List<RevenueReport> getRevenueReports() throws SQLException {
        return revenueReportRepository.getRevenueReports();
    }
}
