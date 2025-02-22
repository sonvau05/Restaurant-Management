package com.restaurantmanagement.app.entity;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.sql.Date;

public class Employee {
    private final ObjectProperty<Integer> employeeID;
    private final StringProperty fullName;
    private final ObjectProperty<Date> dateOfBirth;
    private final StringProperty phoneNumber;
    private final StringProperty address;
    private final StringProperty role;
    private final ObjectProperty<Date> hireDate;

    public Employee(int employeeID, String fullName, Date dateOfBirth, String phoneNumber, String address, String role, Date hireDate) {
        this.employeeID = new SimpleObjectProperty<>(employeeID);
        this.fullName = new SimpleStringProperty(fullName);
        this.dateOfBirth = new SimpleObjectProperty<>(dateOfBirth);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
        this.role = new SimpleStringProperty(role);
        this.hireDate = new SimpleObjectProperty<>(hireDate);
    }

    public int getEmployeeID() {
        return employeeID.get();
    }
    public void setEmployeeID(int employeeID) {
        this.employeeID.set(employeeID);
    }
    public String getFullName() {
        return fullName.get();
    }
    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }
    public Date getDateOfBirth() {
        return dateOfBirth.get();
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }
    public String getPhoneNumber() {
        return phoneNumber.get();
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }
    public String getAddress() {
        return address.get();
    }
    public void setAddress(String address) {
        this.address.set(address);
    }
    public String getRole() {
        return role.get();
    }
    public void setRole(String role) {
        this.role.set(role);
    }
    public Date getHireDate() {
        return hireDate.get();
    }
    public void setHireDate(Date hireDate) {
        this.hireDate.set(hireDate);
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }
    public ObjectProperty<Date> dateOfBirthProperty() {
        return dateOfBirth;
    }
    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }
    public StringProperty addressProperty() {
        return address;
    }
    public StringProperty roleProperty() {
        return role;
    }
    public ObjectProperty<Date> hireDateProperty() {
        return hireDate;
    }
}
