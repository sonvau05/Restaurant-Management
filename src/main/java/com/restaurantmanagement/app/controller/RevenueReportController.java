package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.RevenueReport;
import com.restaurantmanagement.app.service.RevenueReportService;
import com.restaurantmanagement.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RevenueReportController {

    @FXML
    private TableView<RevenueReport> revenueTable;

    @FXML
    private TableColumn<RevenueReport, String> reportMonthColumn;

    @FXML
    private TableColumn<RevenueReport, Double> totalRevenueColumn;

    @FXML
    private TableColumn<RevenueReport, Integer> totalQuantityColumn;

    @FXML
    private BarChart<String, Number> revenueChart;

    @FXML
    private CategoryAxis monthAxis;

    @FXML
    private NumberAxis valueAxis;

    @FXML
    private Button exitButton;

    private final RevenueReportService revenueReportService;

    public RevenueReportController() {
        // Khởi tạo dịch vụ với kết nối cơ sở dữ liệu
        Connection connection = DatabaseConnection.getConnection(); // Đảm bảo kết nối được khởi tạo đúng
        this.revenueReportService = new RevenueReportService(connection);
    }

    @FXML
    public void initialize() {
        // Khởi tạo cột bảng
        reportMonthColumn.setCellValueFactory(new PropertyValueFactory<>("reportMonth"));
        totalRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        totalQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

        // Lấy dữ liệu và hiển thị lên bảng và biểu đồ
        try {
            List<RevenueReport> reports = revenueReportService.getRevenueReports();
            ObservableList<RevenueReport> data = FXCollections.observableArrayList(reports);
            revenueTable.setItems(data);

            // Cập nhật biểu đồ cột với doanh thu và số lượng
            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            revenueSeries.setName("Doanh Thu");
            XYChart.Series<String, Number> quantitySeries = new XYChart.Series<>();
            quantitySeries.setName("Số Lượng Món Bán");

            for (RevenueReport report : reports) {
                String month = report.getReportMonth();
                revenueSeries.getData().add(new XYChart.Data<>(month, report.getTotalRevenue()));
                quantitySeries.getData().add(new XYChart.Data<>(month, report.getTotalQuantity()));
            }

            revenueChart.getData().addAll(revenueSeries, quantitySeries);
        } catch (SQLException e) {
            e.printStackTrace(); // In ra lỗi nếu có
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        // Đóng cửa sổ hoặc quay lại giao diện chính
        System.exit(0); // Dừng ứng dụng (hoặc có thể thay bằng hành động khác)
    }
}
