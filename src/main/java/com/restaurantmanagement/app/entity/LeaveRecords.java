package com.restaurantmanagement.app.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.sql.Date;

public class LeaveRecords {
    private final ObjectProperty<Integer> leaveID;
    private final ObjectProperty<Integer> employeeID;
    private final ObjectProperty<Date> startDate;
    private final ObjectProperty<Date> endDate;
    private final StringProperty reason;

    public LeaveRecords(int leaveID, int employeeID, Date startDate, Date endDate, String reason) {
        this.leaveID = new SimpleObjectProperty<>(leaveID);
        this.employeeID = new SimpleObjectProperty<>(employeeID);
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
        this.reason = new SimpleStringProperty(reason);
    }

    // Getters và setters thực tế
    public int getLeaveID() {
        return leaveID.get();
    }
    public void setLeaveID(int leaveID) {
        this.leaveID.set(leaveID);
    }
    public int getEmployeeID() {
        return employeeID.get();
    }
    public void setEmployeeID(int employeeID) {
        this.employeeID.set(employeeID);
    }
    public Date getStartDate() {
        return startDate.get();
    }
    public void setStartDate(Date startDate) {
        this.startDate.set(startDate);
    }
    public Date getEndDate() {
        return endDate.get();
    }
    public void setEndDate(Date endDate) {
        this.endDate.set(endDate);
    }
    public String getReason() {
        return reason.get();
    }
    public void setReason(String reason) {
        this.reason.set(reason);
    }

    // Các property getters để bind với TableView
    public ObjectProperty<Integer> leaveIDProperty() {
        return leaveID;
    }
    public ObjectProperty<Integer> employeeIDProperty() {
        return employeeID;
    }
    public ObjectProperty<Date> startDateProperty() {
        return startDate;
    }
    public ObjectProperty<Date> endDateProperty() {
        return endDate;
    }
    public StringProperty reasonProperty() {
        return reason;
    }
}
