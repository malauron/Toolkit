package com.Controllers;

import com.DataAccessObjects.Billings;
import com.Interfaces.IBillings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;

public class MainForm {
    @FXML
    PieChart pieChartBillingStatus;
    @FXML
    BarChart barChartBillingsAge;
    @FXML
    CategoryAxis xAxis;
    @FXML
    NumberAxis yAxis;

    private IBillings billings = new Billings();
    private Task<ObservableList<PieChart.Data>> pieChartData;
    private Task<BarChart<String,Number>> barChartData;

    public void initialize() {
        xAxis.setAnimated(false);
        taskLoadPieChart();
        new Thread(pieChartData).start();
        displayPieChart();

        taskLoadBarChart();
        new Thread(barChartData).start();
        displayBarChart();
    }

    private void taskLoadPieChart() {
        pieChartData = new Task<ObservableList<PieChart.Data>>() {
            @Override
            protected ObservableList<PieChart.Data> call() throws Exception {
                return billings.getPieChartData();
            }
        };
    }

    private void displayPieChart() {
        pieChartData.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    pieChartBillingStatus.setData(pieChartData.get());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Thread successful.");
            } else if (newValue == Worker.State.FAILED) {
                System.out.println("Thread failed.");
            } else if (newValue == Worker.State.RUNNING) {
                System.out.println("Thread is running.");
            }else if (newValue == Worker.State.CANCELLED) {
                System.out.println("Thread is cancelled.");
            }
        });
    }

    private void taskLoadBarChart() {
        barChartData = new Task<BarChart<String, Number>>() {
            @Override
            protected BarChart<String, Number> call() throws Exception {
                return billings.getBarChartData();
            }
        };
    }

    private void displayBarChart() {
        barChartData.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {
                    barChartBillingsAge.getData().clear();
                    barChartBillingsAge.setData(barChartData.get().getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Thread successful.");
            } else if (newValue == Worker.State.FAILED) {
                System.out.println("Thread failed.");
            } else if (newValue == Worker.State.RUNNING) {
                System.out.println("Thread is running.");
            }else if (newValue == Worker.State.CANCELLED) {
                System.out.println("Thread is cancelled.");
            }
        });
    }
}
