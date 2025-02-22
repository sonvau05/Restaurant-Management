package com.restaurantmanagement.app.controller;

import com.restaurantmanagement.app.entity.RevenueReport;
import com.restaurantmanagement.app.service.RevenueReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

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
    private ObservableList<RevenueReport> revenueData;

    public RevenueReportController() {
        this.revenueReportService = new RevenueReportService();
    }

    @FXML
    public void initialize() {
        reportMonthColumn.setCellValueFactory(new PropertyValueFactory<>("reportMonth"));
        totalRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        totalQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

        refreshData();
    }

    public void refreshData() {
        List<RevenueReport> reports = revenueReportService.getLast12MonthsRevenueReports();
        revenueData = FXCollections.observableArrayList(reports);
        revenueTable.setItems(revenueData);

        revenueChart.getData().clear();
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenue");
        XYChart.Series<String, Number> quantitySeries = new XYChart.Series<>();
        quantitySeries.setName("Items Sold");

        for (RevenueReport report : reports) {
            String month = report.getReportMonth();
            revenueSeries.getData().add(new XYChart.Data<>(month, report.getTotalRevenue()));
            quantitySeries.getData().add(new XYChart.Data<>(month, report.getTotalQuantity()));
        }

        revenueChart.getData().addAll(revenueSeries, quantitySeries);
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
}