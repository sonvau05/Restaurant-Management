package com.restaurantmanagement.app.entity;

public class RevenueReport {
    private int reportID;
    private String reportMonth;
    private double totalRevenue;
    private int totalQuantity;

    public RevenueReport() {
    }

    public RevenueReport(int reportID, String reportMonth, double totalRevenue, int totalQuantity) {
        this.reportID = reportID;
        this.reportMonth = reportMonth;
        this.totalRevenue = totalRevenue;
        this.totalQuantity = totalQuantity;
    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public String getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(String reportMonth) {
        this.reportMonth = reportMonth;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}