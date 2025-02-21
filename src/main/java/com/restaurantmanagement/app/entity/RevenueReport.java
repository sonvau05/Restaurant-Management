package com.restaurantmanagement.app.entity;

import javafx.beans.property.*;

public class RevenueReport {
    private final IntegerProperty reportID;
    private final StringProperty reportMonth;
    private final ObjectProperty<Double> totalRevenue;
    private final IntegerProperty totalQuantity;

    public RevenueReport(int reportID, String reportMonth, double totalRevenue, int totalQuantity) {
        this.reportID = new SimpleIntegerProperty(reportID);
        this.reportMonth = new SimpleStringProperty(reportMonth);
        this.totalRevenue = new SimpleObjectProperty<>(totalRevenue);
        this.totalQuantity = new SimpleIntegerProperty(totalQuantity);
    }

    public int getReportID() {
        return reportID.get();
    }
    public void setReportID(int reportID) {
        this.reportID.set(reportID);
    }
    public IntegerProperty reportIDProperty() {
        return reportID;
    }

    public String getReportMonth() {
        return reportMonth.get();
    }
    public void setReportMonth(String reportMonth) {
        this.reportMonth.set(reportMonth);
    }
    public StringProperty reportMonthProperty() {
        return reportMonth;
    }

    public double getTotalRevenue() {
        return totalRevenue.get();
    }
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue.set(totalRevenue);
    }
    public ObjectProperty<Double> totalRevenueProperty() {
        return totalRevenue;
    }

    public int getTotalQuantity() {
        return totalQuantity.get();
    }
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity.set(totalQuantity);
    }
    public IntegerProperty totalQuantityProperty() {
        return totalQuantity;
    }
}
