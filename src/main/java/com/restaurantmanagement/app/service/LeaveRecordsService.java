package com.restaurantmanagement.app.service;

import com.restaurantmanagement.app.entity.LeaveRecords;
import com.restaurantmanagement.app.repository.LeaveRecordsRepository;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class LeaveRecordsService {
    private final LeaveRecordsRepository leaveRecordsRepository;

    // Constructor nhận Connection và truyền cho LeaveRecordsRepository
    public LeaveRecordsService(Connection connection) {
        this.leaveRecordsRepository = new LeaveRecordsRepository(connection);
    }

    /**
     * Lưu thông tin lịch sử nghỉ phép.
     *
     * @param employeeID ID của nhân viên
     * @param startDate  Ngày bắt đầu nghỉ (Date)
     * @param endDate    Ngày kết thúc nghỉ (Date)
     * @param reason     Lý do nghỉ
     * @return true nếu lưu thành công, false nếu thất bại.
     */
    public boolean saveLeaveRecords(int employeeID, Date startDate, Date endDate, String reason) {
        return leaveRecordsRepository.saveLeaveRecords(employeeID, startDate, endDate, reason);
    }

    /**
     * Lấy danh sách tất cả lịch sử nghỉ phép.
     */
    public List<LeaveRecords> getAllLeaveRecords() {
        return leaveRecordsRepository.getAllLeaveRecords();
    }
}
