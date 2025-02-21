package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.database.DatabaseConnection;
import com.restaurantmanagement.app.entity.Employee;
import com.restaurantmanagement.app.entity.LeaveRecords;
import com.restaurantmanagement.app.service.EmployeeService;
import com.restaurantmanagement.app.service.LeaveRecordsService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EmployeeController {

    @FXML
    private TextField employeeNameField;
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableView<LeaveRecords> leaveRecordsTable;

    // Các cột của bảng nhân viên
    @FXML
    private TableColumn<Employee, String> fullNameColumn, phoneColumn, addressColumn, roleColumn;
    @FXML
    private TableColumn<Employee, LocalDate> dobColumn, hireDateColumn;

    // Các cột của bảng LeaveRecords
    @FXML
    private TableColumn<LeaveRecords, LocalDate> leaveStartColumn, leaveEndColumn;
    @FXML
    private TableColumn<LeaveRecords, String> leaveReasonColumn;

    private EmployeeService employeeService;
    private LeaveRecordsService leaveRecordsService;

    public EmployeeController() {
        // JavaFX sẽ tự động inject các thành phần qua FXML.
    }

    @FXML
    public void initialize() {
        // Khởi tạo service với Connection từ DatabaseConnection
        employeeService = new EmployeeService(DatabaseConnection.getConnection());
        leaveRecordsService = new LeaveRecordsService(DatabaseConnection.getConnection());

        // Thiết lập cellValueFactory cho bảng nhân viên
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        dobColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDateOfBirth().toLocalDate()));
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        hireDateColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getHireDate().toLocalDate()));

        // Thiết lập cellValueFactory cho bảng LeaveRecords
        leaveStartColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getStartDate().toLocalDate()));
        leaveEndColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getEndDate().toLocalDate()));
        leaveReasonColumn.setCellValueFactory(cellData -> cellData.getValue().reasonProperty());

        loadEmployeeData();

        // Khi chọn 1 nhân viên, load LeaveRecords của nhân viên đó
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadLeaveRecordsForEmployee(newVal.getEmployeeID());
            } else {
                leaveRecordsTable.setItems(FXCollections.observableArrayList());
            }
        });
    }

    @FXML
    public void handleAddEmployee() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Thêm nhân viên mới");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Tạo giao diện nhập thông tin nhân viên (không có email)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField fullNameField = new TextField();
        DatePicker dobField = new DatePicker();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        ComboBox<String> roleField = new ComboBox<>();
        roleField.getItems().addAll("MANAGER", "CHEF", "CASHIER", "SERVICE");
        roleField.setValue("CASHIER"); // giá trị mặc định
        DatePicker hireDateField = new DatePicker(LocalDate.now());

        grid.add(new Label("Họ và Tên:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Ngày sinh:"), 0, 1);
        grid.add(dobField, 1, 1);
        grid.add(new Label("Số điện thoại:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Địa chỉ:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("Chức vụ:"), 0, 4);
        grid.add(roleField, 1, 4);
        grid.add(new Label("Ngày vào làm:"), 0, 5);
        grid.add(hireDateField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String fullName = fullNameField.getText().trim();
                LocalDate dob = dobField.getValue();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String role = roleField.getValue();
                LocalDate hireDate = hireDateField.getValue();

                if (fullName.isEmpty() || dob == null || phone.isEmpty() ||
                        address.isEmpty() || role == null || hireDate == null) {
                    showAlert("Vui lòng điền đầy đủ thông tin!", Alert.AlertType.WARNING);
                    return null;
                }

                if (dob.isAfter(LocalDate.now().minusYears(18))) {
                    showAlert("Nhân viên phải ít nhất 18 tuổi.", Alert.AlertType.WARNING);
                    return null;
                }

                if (!phone.matches("^09\\d{8}$")) {
                    showAlert("Số điện thoại phải bắt đầu bằng 09 và có 10 chữ số.", Alert.AlertType.WARNING);
                    return null;
                }

                // Chuyển đổi LocalDate sang java.sql.Date
                Date sqlDob = Date.valueOf(dob);
                Date sqlHireDate = Date.valueOf(hireDate);

                Employee newEmployee = new Employee(0, fullName, sqlDob, phone, address, role, sqlHireDate);
                if (employeeService.saveEmployee(newEmployee)) {
                    loadEmployeeData();
                    showAlert("Thêm nhân viên thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Thêm nhân viên thất bại!", Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML
    public void handleUpdateEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Vui lòng chọn nhân viên để cập nhật!", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Cập nhật thông tin nhân viên");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Giao diện cập nhật nhân viên (không có email)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField fullNameField = new TextField(selectedEmployee.getFullName());
        DatePicker dobField = new DatePicker(selectedEmployee.getDateOfBirth().toLocalDate());
        TextField phoneField = new TextField(selectedEmployee.getPhoneNumber());
        TextField addressField = new TextField(selectedEmployee.getAddress());
        ComboBox<String> roleField = new ComboBox<>();
        roleField.getItems().addAll("MANAGER", "CHEF", "CASHIER", "SERVICE");
        roleField.setValue(selectedEmployee.getRole());
        DatePicker hireDateField = new DatePicker(selectedEmployee.getHireDate().toLocalDate());

        grid.add(new Label("Họ và Tên:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Ngày sinh:"), 0, 1);
        grid.add(dobField, 1, 1);
        grid.add(new Label("Số điện thoại:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Địa chỉ:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("Chức vụ:"), 0, 4);
        grid.add(roleField, 1, 4);
        grid.add(new Label("Ngày vào làm:"), 0, 5);
        grid.add(hireDateField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                selectedEmployee.setFullName(fullNameField.getText().trim());
                selectedEmployee.setDateOfBirth(Date.valueOf(dobField.getValue()));
                selectedEmployee.setPhoneNumber(phoneField.getText().trim());
                selectedEmployee.setAddress(addressField.getText().trim());
                selectedEmployee.setRole(roleField.getValue());
                selectedEmployee.setHireDate(Date.valueOf(hireDateField.getValue()));

                if (employeeService.updateEmployee(selectedEmployee)) {
                    loadEmployeeData();
                    showAlert("Cập nhật nhân viên thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Cập nhật thất bại!", Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    @FXML
    public void handleDeleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Vui lòng chọn nhân viên để xóa!", Alert.AlertType.WARNING);
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận xóa");
        confirmation.setHeaderText("Bạn có chắc chắn muốn xóa nhân viên: " + selectedEmployee.getFullName() + "?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (employeeService.deleteEmployee(selectedEmployee.getEmployeeID())) {
                    loadEmployeeData();
                    showAlert("Xóa nhân viên thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Xóa nhân viên thất bại!", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    public void handleSearchEmployee() {
        String query = employeeNameField.getText().trim();
        if (!query.isEmpty()) {
            List<Employee> result = employeeService.getEmployeesByName(query);
            employeeTable.setItems(FXCollections.observableArrayList(result));
        } else {
            loadEmployeeData();
        }
    }

    private void loadEmployeeData() {
        List<Employee> employees = employeeService.getAllEmployees();
        employeeTable.setItems(FXCollections.observableArrayList(employees));
    }

    private void loadLeaveRecordsForEmployee(int employeeID) {
        List<LeaveRecords> records = employeeService.getLeaveRecordsByEmployeeId(employeeID);
        leaveRecordsTable.setItems(FXCollections.observableArrayList(records));
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleLeaveEmployee(ActionEvent event) {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert("Vui lòng chọn nhân viên để đăng ký nghỉ phép!", Alert.AlertType.WARNING);
            return;
        }

        Dialog<LeaveRecords> dialog = new Dialog<>();
        dialog.setTitle("Đăng ký nghỉ phép");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        TextArea reasonArea = new TextArea();

        grid.add(new Label("Ngày bắt đầu:"), 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(new Label("Ngày kết thúc:"), 0, 1);
        grid.add(endDatePicker, 1, 1);
        grid.add(new Label("Lý do nghỉ:"), 0, 2);
        grid.add(reasonArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                LocalDate start = startDatePicker.getValue();
                LocalDate end = endDatePicker.getValue();
                String reason = reasonArea.getText().trim();

                if (start == null || end == null || reason.isEmpty()) {
                    showAlert("Vui lòng điền đầy đủ thông tin nghỉ phép!", Alert.AlertType.WARNING);
                    return null;
                }
                if (start.isAfter(end)) {
                    showAlert("Ngày bắt đầu phải trước ngày kết thúc!", Alert.AlertType.WARNING);
                    return null;
                }
                LeaveRecords record = new LeaveRecords(
                        0, // LeaveID tự động do DB gán
                        selectedEmployee.getEmployeeID(),
                        Date.valueOf(start),
                        Date.valueOf(end),
                        reason
                );
                return record;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(record -> {
            if (employeeService.addLeaveRecords(record)) {
                loadLeaveRecordsForEmployee(selectedEmployee.getEmployeeID());
                showAlert("Đăng ký nghỉ phép thành công!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Đăng ký nghỉ phép thất bại!", Alert.AlertType.ERROR);
            }
        });
    }
}
