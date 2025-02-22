package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.Employee;
import com.restaurantmanagement.app.entity.LeaveRecords;
import com.restaurantmanagement.app.repository.EmployeeRepository;
import com.restaurantmanagement.app.repository.LeaveRecordsRepository;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final LeaveRecordsRepository leaveRecordsRepository;

    public EmployeeService(Connection connection) {
        this.employeeRepository = new EmployeeRepository(connection);
        this.leaveRecordsRepository = new LeaveRecordsRepository(connection);
    }

    public boolean saveEmployee(Employee employee) {
        return employeeRepository.saveEmployee(
                employee.getFullName(),
                employee.getDateOfBirth(),
                employee.getPhoneNumber(),
                employee.getAddress(),
                employee.getRole(),
                employee.getHireDate()
        );
    }

    public boolean updateEmployee(Employee employee) {
        return employeeRepository.updateEmployee(employee);
    }

    public boolean deleteEmployee(int employeeID) {
        return employeeRepository.deleteEmployee(employeeID);
    }

    public List<Employee> getEmployeesByName(String name) {
        return employeeRepository.getEmployeesByName(name);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.getAllEmployees();
    }

    public boolean addLeaveRecords(LeaveRecords record) {
        return leaveRecordsRepository.saveLeaveRecords(
                record.getEmployeeID(),
                record.getStartDate(),
                record.getEndDate(),
                record.getReason()
        );
    }

    public List<LeaveRecords> getLeaveRecordsByEmployeeId(int employeeID) {
        return leaveRecordsRepository.getLeaveRecordsByEmployeeId(employeeID);
    }
}
