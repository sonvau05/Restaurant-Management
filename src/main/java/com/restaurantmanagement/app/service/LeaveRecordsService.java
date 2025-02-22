package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.repository.LeaveRecordsRepository;

import java.sql.Connection;

public class LeaveRecordsService {
    private final LeaveRecordsRepository leaveRecordsRepository;

    public LeaveRecordsService(Connection connection) {
        this.leaveRecordsRepository = new LeaveRecordsRepository(connection);
    }
}
